FROM maven:3.9.9-eclipse-temurin-17 AS build

# Đặt thư mục làm việc cho Maven
WORKDIR /app

# Sao chép các tệp dự án vào thư mục /app
COPY . .

# Build ứng dụng
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-alpine

# Đặt thư mục làm việc cho image cuối cùng
WORKDIR /app

# Sao chép tệp JAR từ giai đoạn trước
COPY --from=build /app/target/beehub-0.0.1-SNAPSHOT.jar .

# Mở cổng 8080
EXPOSE 8080

# Thiết lập entry point cho tệp JAR
ENTRYPOINT ["java", "-jar", "beehub-0.0.1-SNAPSHOT.jar"]