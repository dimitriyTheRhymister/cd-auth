# Auth Service

Сервис аутентификации и управления пользователями.

## Технологии
- Java 21
- Spring Boot 2.7.11
- Spring Security + OAuth2 + JWT
- PostgreSQL
- Liquibase
- Eureka Client

## Порты
- 9900 — REST API
- 9901 — Management (Actuator: health, info)

## База данных
- Имя: cd_auth
- Пользователь: postgres
- Пароль: password

## API Endpoints

| Метод | URL | Описание |
|-------|-----|----------|
| POST | /registration | Регистрация |
| GET | /auth/activated/{key} | Активация по email |
| POST | /person/updateMultipart | Обновление профиля с фото |
| GET | /profiles/{id} | Профиль по ID |
| GET | /person/current | Текущий пользователь |
| POST | /oauth/token | Получение JWT токена |
| POST | /forgot | Восстановление пароля |

## Swagger
http://localhost:9900/swagger-ui/index.html

## Eureka
Регистрируется под именем auth.

## Запуск локально (без Docker)

    mvn spring-boot:run

## Запуск через Docker Compose

### Требования
- Docker (20.10+)
- Docker Compose v2 (docker compose ...)

### Запуск

1. Собрать образ приложения:

       docker compose build

2. Запустить сервисы:

       docker compose up -d

3. Дождаться старта приложения (~25 секунд):

       docker compose logs -f auth

   Дождись строки Started AuthSrv in XX seconds, затем нажми Ctrl+C.

### Доступ

| Что | URL |
|---|---|
| REST API + Swagger UI | http://localhost:9900/swagger-ui/index.html |
| Management (health) | http://localhost:9901/actuator/health |

Пример health-check:

    curl http://localhost:9901/actuator/health

Ожидаемый ответ:

    {"status":"UP","components":{"db":{"status":"UP"},...}}

### Остановка

    docker compose down

### Данные

База данных PostgreSQL сохраняется в локальной папке ./db_data благодаря
bind mount (см. docker-compose.yml), поэтому данные не теряются
при перезапуске контейнеров.

### Состав сервисов

| Сервис | Образ | Порт | Назначение |
|---|---|---|---|
| db | postgres:16 | 5432 | База данных PostgreSQL |
| auth | cd-auth-auth | 9900 | REST API приложения |
| auth (management) | cd-auth-auth | 9901 | Actuator: health, info |

### Переменные окружения

Заданы в docker-compose.yml:

- SPRING_DATASOURCE_URL — JDBC URL базы данных
- SPRING_DATASOURCE_USERNAME — пользователь БД
- SPRING_DATASOURCE_PASSWORD — пароль БД
- EUREKA_CLIENT_* — отключение Eureka для локального запуска
