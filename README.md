# Auth Service

Сервис аутентификации и управления пользователями.

## 🔧 Технологии
- Java 21
- Spring Boot 2.7.11
- Spring Security + OAuth2 + JWT
- PostgreSQL
- Liquibase
- Eureka Client

## 📦 Порт
9900

## 🗄️ База данных
- Имя: cd_auth
- Пользователь: postgres
- Пароль: password

## 📚 API Endpoints

| Метод | URL | Описание |
|-------|-----|----------|
| POST | /registration | Регистрация |
| GET | /auth/activated/{key} | Активация по email |
| POST | /person/updateMultipart | Обновление профиля с фото |
| GET | /profiles/{id} | Профиль по ID |
| GET | /person/current | Текущий пользователь |
| POST | /oauth/token | Получение JWT токена |
| POST | /forgot | Восстановление пароля |

## 📄 Swagger
http://localhost:9900/swagger-ui/index.html

## 🔗 Eureka
Регистрируется под именем auth

## 🚀 Запуск
```bash
mvn spring-boot:run