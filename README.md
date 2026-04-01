# OnlyOne

## Giới thiệu

OnlyOne là một hệ thống microservices mẫu được tổ chức theo kiến trúc phân tán. Dự án kết hợp NestJS và Spring Boot để xây dựng gateway, dịch vụ người dùng, dịch vụ xác thực và thông báo, cùng hạ tầng cơ sở dữ liệu và message broker bằng Docker.

Mục tiêu của dự án là minh họa một giải pháp API gateway với gRPC, bảo mật JWT/OAuth2, quản lý người dùng, profile và notification, trong một môi trường container hóa.

## Kiến trúc chính

- `gateway/`: NestJS API gateway và microservice client.
  - Kết nối gRPC tới các service backend.
  - Cung cấp endpoint HTTP cho client.
  - Áp dụng bảo mật qua `AuthGuard` và global exception filter.
  - Sử dụng request context để truyền metadata như token.

- `services/identity-service/`: Spring Boot identity service.
  - Quản lý xác thực và authorization.
  - Kết nối MySQL, Redis và RabbitMQ.
  - Triển khai với gRPC và REST.

- `services/user-service/`: Spring Boot user service.
  - Cung cấp chức năng quản lý thông tin người dùng.
  - Kết nối MySQL, Redis và RabbitMQ.
  - Ưu tiên tích hợp với gRPC.

- `services/notification-service/`: NestJS notification microservice.
  - Xử lý thông báo và event messaging.
  - Sử dụng RabbitMQ để truyền message.

- `packages/grpc-security-starter/`: Thư viện hỗ trợ gRPC và bảo mật, dùng chung cho dự án.

- `docker-compose.yml`: Khởi tạo toàn bộ hệ thống, bao gồm MySQL, MongoDB, Redis, RabbitMQ và Nginx proxy.

## Công nghệ sử dụng

- NestJS (TypeScript)
- Spring Boot (Java)
- gRPC
- Docker / Docker Compose
- MySQL
- MongoDB
- Redis
- RabbitMQ
- Nginx proxy

## Chức năng chính

- Gateway nhận yêu cầu HTTP và chuyển tiếp tới backend qua gRPC.
- Xác thực người dùng qua access token.
- Truy vấn profile và thông tin người dùng công khai.
- Quản lý luồng gRPC metadata và request context.
- Xử lý thông báo và event message bằng RabbitMQ.
- Container hóa toàn bộ ứng dụng với Docker Compose.

## Hướng dẫn chạy dự án

### Chạy bằng Docker Compose

```bash
docker compose up --build
```

Hệ thống sẽ khởi tạo các service sau:

- `gateway`
- `identity-service`
- `user-service`
- `onlyone-mysql`
- `onlyone-mongo`
- `onlyone-redis`
- `onlyone-rabbitmq`
- `nginx-proxy`

### Chạy từng service riêng lẻ

#### Gateway

```bash
cd gateway
npm install
npm run start:dev
```

#### Notification Service

```bash
cd services/notification-service
npm install
npm run start:dev
```

#### Identity / User Service

```bash
cd services/identity-service
./mvnw spring-boot:run
```

```bash
cd services/user-service
./mvnw spring-boot:run
```

> Lưu ý: các service Java sử dụng Maven wrapper (`mvnw` / `mvnw.cmd`).

## Cấu hình môi trường

- Mỗi service chứa file `.env.prod` riêng.
- `gateway` sử dụng `.env.dev` khi chạy local với `start:dev`.
- Docker Compose sử dụng biến cấu hình được định nghĩa trực tiếp trong `docker-compose.yml`.

## Cấu trúc thư mục chính

- `gateway/`: API gateway NestJS.
- `services/identity-service/`: dịch vụ xác thực Spring Boot.
- `services/user-service/`: dịch vụ người dùng Spring Boot.
- `services/notification-service/`: microservice thông báo NestJS.
- `packages/grpc-security-starter/`: thư viện gRPC và bảo mật dùng chung.
- `docker/`: tập lệnh khởi tạo MySQL.
- `nginx-proxy/`: cấu hình Nginx reverse proxy.