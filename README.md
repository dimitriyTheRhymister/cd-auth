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

## ☸️ Запуск в Kubernetes (minikube)

### Требования
- Docker
- [minikube](https://minikube.sigs.k8s.io/docs/start/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)

### Запуск

1. Запустить локальный кластер:

   ```bash
   minikube start --driver=docker

2. Загрузить образ приложения в кластер minikube:

   minikube image load cd-auth-auth:latest

3. Применить манифесты из папки k8s/ в правильном порядке:

   cd k8s
   kubectl apply -f postgresdb-secret.yml
   kubectl apply -f postgresdb-configmap.yml
   kubectl apply -f postgresdb-deployment.yml
   kubectl apply -f cd-auth-deployment.yml

4. Проверить, что pod-ы запустились:

   kubectl get pods

   Ожидаемо: 2 pod-а в статусе Running (cd-auth и postgresdb-deployment).

5. Получить URL для доступа к приложению:

   minikube service cd-auth-service --url

   Выведет два URL:
   - первый — REST API (порт 9900)
   - второй — Management (порт 9901)

6. Проверить health-check (используй второй URL + путь /actuator/health):

   http://IP:PORT/actuator/health

   Ожидаемый ответ:

   {"status":"UP","components":{"db":{"status":"UP"},...}}

7. Swagger UI доступен по первому URL:

   http://IP:PORT/swagger-ui/index.html

### Состав манифестов

| Файл | Что описывает |
|---|---|
| postgresdb-secret.yml | Логин/пароль Postgres (base64) |
| postgresdb-configmap.yml | URL БД и имя базы |
| postgresdb-deployment.yml | Deployment + Service для Postgres |
| cd-auth-deployment.yml | Deployment + Service (LoadBalancer) для приложения |

### Проверка самовосстановления и масштабирования

    # Удалить pod — Kubernetes создаст новый автоматически
    kubectl delete pod <имя-pod-а>

    # Масштабировать приложение до 3 копий
    kubectl scale deployment cd-auth --replicas=3

    # Вернуть обратно
    kubectl scale deployment cd-auth --replicas=1

### Остановка

    # Удалить ресурсы из кластера
    kubectl delete -f k8s/

    # Остановить кластер minikube
    minikube stop

    # Полностью удалить кластер
    minikube delete
