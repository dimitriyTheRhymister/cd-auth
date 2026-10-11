# ========================================
# Этап 1: Сборка jar в Maven-образе
# ========================================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ========================================
# Этап 2: Запуск приложения на лёгком JRE
# ========================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем готовый jar из этапа сборки
COPY --from=builder /app/target/auth-1.0.0.jar app.jar

EXPOSE 9900 9901

# Запуск jar
CMD ["java", "-jar", "app.jar"]
