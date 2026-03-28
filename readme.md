# Для запуска

### Создать .env, закинуть переменные <br> (При развертывании обязательно DB_USERNAME, DB_PASSWORD, JWT_SECRET)

CORS_ALLOWED_ORIGINS= { адрес фронта }

DB_URL= { URL на Postgres бд } <br>
DB_USERNAME= { Бд юзер } <br>
DB_PASSWORD= { Бд пароль } <br>


\# Тут всё можно оставить пустым <br>
REDIS_HOST= <br>
REDIS_PORT= <br>
REDIS_PASSWORD= <br>
REDIS_TIMEOUT= <br>
REDIS_MAX_ACTIVE= 

JWT_SECRET= { 512 битный ключ } <br>
JWT_LIFETIME= { время в мс: 1800000 = 30 минут } <br>

### Запустить командой "docker-compose --env-file .env up --build -d"
### Бек поднимится как localhost:8080
# Cписок ручек:

### Аутентификация:
#### POST /api/v1/auth/generate?username={номер телефона} <br>
Для отправки одноразового кода (пока в консоль бека) <br>
#### POST /api/v1/auth/login с телом {token: string} <br>
Для входа по коду (отправляется только код) <br>
#### POST /api/v1/auth/register c телом {username: string, role: 'MEMBER' | 'KEEPER'} <br>
Для регистрации по телефону, после этого сразу отправляется код для входа. <br>
### Пользователь:
#### DELETE /api/v1/users/delete/{id}
Для удаления юзера по id, работает только если запрос отправляет сам юзер. <br>
### Контакты:
#### GET /api/v1/contacts
Для получения списка контактов юзера
#### POST /api/v1/contacts с телом {contactUsername: string}
Для создания контакта между текущим пользователем и contactUsername
#### POST /api/v1/contacts/{id}/respond
Для принятия контакта со стороны того, кому его предложили
#### DELETE /api/v1/contacts/{id}
Для удаления контакта




