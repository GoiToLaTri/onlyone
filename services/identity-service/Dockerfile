# Stage 1: Build - Sử dụng JDK để đóng gói ứng dụng
FROM maven:3.9.12-eclipse-temurin-25-noble AS build

WORKDIR /app

# Copy file cấu hình Maven và tải dependencies (tối ưu hóa layer cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy mã nguồn và build file jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run - Sử dụng JRE nhẹ hơn để chạy ứng dụng
# Chuyển từ 'noble' (Ubuntu) sang 'alpine' để giảm dung lượng
FROM eclipse-temurin:25-jre-alpine-3.23

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-XX:+UseZGC", "-Xmx512m", "-jar", "app.jar"]