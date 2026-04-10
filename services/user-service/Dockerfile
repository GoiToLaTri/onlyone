# Stage 1: Build - Sử dụng JDK để đóng gói ứng dụng
FROM maven:3.9.12-eclipse-temurin-25-noble AS build

WORKDIR /app

COPY packages/grpc-security-starter/pom.xml ./packages/grpc-security-starter/pom.xml
RUN mvn -f packages/grpc-security-starter/pom.xml dependency:go-offline

COPY packages/grpc-security-starter/src ./packages/grpc-security-starter/src
RUN mvn -f packages/grpc-security-starter/pom.xml clean install -DskipTests

# Copy file cấu hình Maven và tải dependencies (tối ưu hóa layer cache)
COPY services/user-service/pom.xml ./services/user-service/pom.xml 
RUN mvn -f services/user-service/pom.xml dependency:go-offline

# Copy mã nguồn và build file jar
COPY services/user-service/src ./services/user-service/src
WORKDIR /app/services/user-service
RUN mvn clean package -DskipTests

# Stage 2: Run - Sử dụng JRE nhẹ hơn để chạy ứng dụng
# Chuyển từ 'noble' (Ubuntu) sang 'alpine' để giảm dung lượng
FROM eclipse-temurin:25-jre-alpine-3.23

WORKDIR /app

COPY --from=build /app/services/user-service/target/*.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-XX:+UseZGC", "-Xmx512m", "-jar", "app.jar"]