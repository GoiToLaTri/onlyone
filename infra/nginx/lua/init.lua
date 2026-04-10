local function read_file(path)
    local f = io.open(path, "rb")
    if not f then 
        ngx.log(ngx.ERR, "Cannot open file: ", path)
        return nil 
    end
    local content = f:read("*all")
    f:close()
    return content
end

-- Load vao bien Global
_G.PRIVATE_KEY = read_file("/usr/local/openresty/nginx/certs/private.pem")
_G.PUBLIC_KEY = read_file("/usr/local/openresty/nginx/certs/public.pem")

ngx.log(ngx.INFO, "--- Keys loaded successfully ---")