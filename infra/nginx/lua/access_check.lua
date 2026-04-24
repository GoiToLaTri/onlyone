local sha256 = require "resty.sha256"
local str = require "resty.string"
local redis = require "resty.redis"
local cjson = require "cjson"
local jwt = require "resty.jwt"

local function hash_token(token)
    local sha = sha256:new()
    sha:update(token)
    return str.to_hex(sha:final())
end

local function unauthorized()
    ngx.status = 401
    ngx.say("Unauthorized")
    return ngx.exit(401)
end

local function build_scopes(account)
    local scopes = {}

    if account.roles then
        for _, r in ipairs(account.roles) do
            if r.name then
                table.insert(scopes, "ROLE_" .. r.name)
            end
        end
    end

    if account.permissions then
        for _, p in ipairs(account.permissions) do
            if p.name then
                table.insert(scopes, p.name)
            end
        end
    end

    return table.concat(scopes, " ")
end

local headers = ngx.req.get_headers()
local token = headers["authorization"]
local uri = ngx.var.uri
local red = redis:new()
red:set_timeout(1000)
-- Lấy IP thật (xử lý cả trường hợp qua Proxy)
local client_ip = ngx.var.http_x_forwarded_for or ngx.var.remote_addr
-- Lấy dấu vân tay trình duyệt
local ua = ngx.req.get_headers()["user-agent"] or "unknown"
-- Lấy một số đặc điểm khác để tăng tính duy nhất
local accept_encoding = ngx.req.get_headers()["accept-encoding"] or ""
local lang = ngx.req.get_headers()["accept-language"] or "unknown"
local primary_lang = string.sub(lang, 1, 5)
local request_id = ngx.var.request_id -- Unique ID của Nginx
local method = ngx.req.get_method()

local public_routes = {
    -- Identity service
    "^/identityservice/authentication/token",
    "^/identityservice/authentication/refreshtoken",
    "^/identityservice/api",

    -- User service
    "^/userservice/users/[^/]+/profile$",
}

if lang == "unknown" then
    primary_lang = lang
end

-- Clear sạch header trước khi set
ngx.req.clear_header("X-Source-IP")
ngx.req.clear_header("X-Source-UA")
ngx.req.clear_header("X-Source-Encoding")
ngx.req.clear_header("X-Request-Nonce")
ngx.req.clear_header("X-Source-Lang")

-- Set header UA gửi cho server
ngx.req.set_header("X-Source-IP", client_ip)
ngx.req.set_header("X-Source-UA", ua)
ngx.req.set_header("X-Source-Encoding", accept_encoding)
ngx.req.set_header("X-Request-Nonce", request_id)
ngx.req.set_header("X-Source-Lang", primary_lang)

-- Kiểm tra riêng cho trường hợp đặc biệt
if string.match(uri, "^/identityservice/accounts/") and method == "POST" then
    return
end

-- Lặp qua các public router
for _, pattern in ipairs(public_routes) do
    if string.match(uri, pattern) then
        return
    end
end

-- Trích xuất token
if not token then
    unauthorized()
else
    token = string.gsub(token, "^Bearer%s+", "")
end

-- Connect redis
local ok, err = red:connect("redis", 6379)
if not ok then
    ngx.log(ngx.ERR, "Redis connect failed: ", err)
    return ngx.exit(500)
end

local ok, err = red:auth("0000")
if not ok then
    ngx.log(ngx.ERR, "Auth failed: ", err)
    return
end

-- Lấy session
local session, err = red:get("SID_" .. hash_token(token))
if err then
    ngx.log(ngx.ERR, err)
    return ngx.exit(500)
end

if not session or session == ngx.null then
    unauthorized()
end
session = cjson.decode(session)

-- Lấy account từ session Id
local account, err = red:get("AID_" .. session.accountId)
if err then
    ngx.log(ngx.ERR, err)
    return ngx.exit(500)
end

if not account or account == ngx.null then
    ngx.log(ngx.WARN, "Account NOT FOUND")
    return unauthorized()
end
account = cjson.decode(account)

-- ngx.log(ngx.INFO, ":::: Auth OK session=", session.id)
-- ngx.log(ngx.INFO, ":::: Auth OK account=", account.email)

-- Set header sau khi user đăng nhập
-- 1. Kiểm tra xem Private Key đã được load chưa
if not _G.PRIVATE_KEY then
    ngx.status = ngx.HTTP_INTERNAL_SERVER_ERROR
    ngx.say('{"error": "Server has not set up Private Key"}')
    return
end

-- 2. Chuẩn bị Payload (Dữ liệu muốn lưu trong Token)
local payload = {
    iss = ngx.var.host,           -- Người phát hành
    sub = session.accountId,           -- ID người dùng
    iat = ngx.time(),             -- Thời điểm tạo (Issued At)
    exp = ngx.time() + 300,      -- Hết hạn sau 5 phút (Expires At)
    scopes = build_scopes(account)              -- Thông tin thêm tùy ý
}

local token = jwt:sign(
    _G.PRIVATE_KEY,
    {
        header = {
            typ = "JWT",
            alg = "RS256" -- Bắt buộc phải là RS256 khi dùng cặp khóa RSA
        },
        payload = payload
    }
)

-- ngx.req.set_header("X-Session-Id", session.id)
-- ngx.req.set_header("X-Account-Id", session.accountId)
-- ngx.req.set_header("X-Account", account.email)
-- ngx.req.set_header("X-Sopce", build_scopes(account))
if token then
    ngx.req.clear_header("X-Token", primary_lang)
    ngx.req.set_header("X-Token", token)
end