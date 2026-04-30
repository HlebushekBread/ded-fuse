# Для запуска

### Создать .env, закинуть переменные <br> (При развертывании обязательно DB_USERNAME, DB_PASSWORD, JWT_SECRET)

CORS_ALLOWED_ORIGINS= { пустое }

DB_URL= { URL на бд, в виде jdbc:postgresql://localhost:5432/имя } <br>
DB_USERNAME= { Бд юзер, обычно postgres } <br>
DB_PASSWORD= { Бд пароль, обычно postgres } <br>

REDIS_HOST= { Хост для редиса, обычно localhost } <br>
REDIS_PORT= { Порт для редиса, обычно 6379 } <br>
REDIS_PASSWORD= { Можно пустой } <br>
REDIS_TIMEOUT= { Время ожидания, обычно 2000ms } <br>
REDIS_MAX_ACTIVE= { Максимум в пулле, пока что 8 } <br>

JWT_SECRET= { 512 битный ключ } <br>
JWT_LIFETIME= { время в мс, пока что 1800000 (30 минут) } <br>

### Запустить командой "docker-compose --env-file .env up --build -d"
### Бек поднимется как localhost:8080
### (Или просто отправляйте запросы на http://breadlab.net:2026)
### <a href="http://breadlab.net:2026/swagger-ui/index.html#">Swagger-ui</a>