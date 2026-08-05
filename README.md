# RabbitTest

Projeto de estudos com dois microservicos Spring Boot:

- `Auth`: cadastra usuarios e publica um evento de boas-vindas no RabbitMQ.
- `email`: consome a fila `email-queue`, envia o email via SMTP e grava o status do envio no PostgreSQL.

Fluxo principal:

```text
POST /user/save -> Auth -> RabbitMQ email-queue -> email -> SMTP -> destinatario
```

## Tecnologias

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Flyway
- PostgreSQL
- RabbitMQ / CloudAMQP
- Spring Mail SMTP
- Docker Compose

## Estrutura

```text
.
├── Auth/                  # Microservico de usuarios
├── email/                 # Microservico de envio de emails
├── docker/postgres/init/  # Script para criar os bancos auth e email
├── docker-compose.yml     # Compose principal
├── .env.example           # Modelo de variaveis de ambiente
└── .gitignore             # Ignora .env, target, IDE etc.
```

## Variaveis de ambiente

Os dados sensiveis nao ficam no codigo. Eles devem ficar em um arquivo `.env`, que esta ignorado pelo Git.

Crie seu arquivo local a partir do exemplo:

```bash
cp .env.example .env
```

Depois edite o `.env` com seus valores reais.

### Banco de dados

| Variavel | Exemplo | Descricao |
| --- | --- | --- |
| `POSTGRES_DB` | `auth` | Banco inicial criado pelo container |
| `POSTGRES_USER` | `postgres` | Usuario do PostgreSQL |
| `POSTGRES_PASSWORD` | `change-me` | Senha do PostgreSQL |
| `POSTGRES_HOST_PORT` | `5436` | Porta do Postgres exposta no host |
| `AUTH_DATASOURCE_URL` | `jdbc:postgresql://localhost:5436/auth` | URL local do banco do Auth |
| `AUTH_DATASOURCE_USERNAME` | `postgres` | Usuario do banco do Auth |
| `AUTH_DATASOURCE_PASSWORD` | `change-me` | Senha do banco do Auth |
| `EMAIL_DATASOURCE_URL` | `jdbc:postgresql://localhost:5436/email` | URL local do banco do email |
| `EMAIL_DATASOURCE_USERNAME` | `postgres` | Usuario do banco do email |
| `EMAIL_DATASOURCE_PASSWORD` | `change-me` | Senha do banco do email |

### RabbitMQ

Para CloudAMQP normalmente use porta `5671` com SSL ligado.

| Variavel | Exemplo | Descricao |
| --- | --- | --- |
| `RABBITMQ_HOST` | `your-rabbitmq-host` | Host do RabbitMQ |
| `RABBITMQ_PORT` | `5671` | Porta do RabbitMQ |
| `RABBITMQ_USERNAME` | `your-rabbitmq-username` | Usuario do RabbitMQ |
| `RABBITMQ_PASSWORD` | `your-rabbitmq-password` | Senha do RabbitMQ |
| `RABBITMQ_VIRTUAL_HOST` | `your-rabbitmq-vhost` | Virtual host do RabbitMQ |
| `RABBITMQ_SSL_ENABLED` | `true` | Liga/desliga SSL |

Para RabbitMQ local, geralmente use:

```properties
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_VIRTUAL_HOST=/
RABBITMQ_SSL_ENABLED=false
```

### SMTP

Para Gmail, use uma senha de app, nao a senha normal da conta.

| Variavel | Exemplo | Descricao |
| --- | --- | --- |
| `SMTP_HOST` | `smtp.gmail.com` | Host SMTP |
| `SMTP_PORT` | `587` | Porta SMTP com STARTTLS |
| `SMTP_USERNAME` | `your-email@gmail.com` | Email usado para autenticar |
| `SMTP_PASSWORD` | `your-app-password` | Senha de app do provedor |
| `SMTP_FROM` | `your-email@gmail.com` | Remetente usado no email |
| `SMTP_DEBUG` | `false` | Mostra logs detalhados do SMTP |

## Como rodar com Docker

Configure o `.env` e rode:

```bash
docker compose --profile apps up --build
```

Servicos:

- Auth: `http://localhost:8080`
- email: `http://localhost:8081`
- PostgreSQL: `localhost:5436`

O container do PostgreSQL inicia com o banco principal e executa o script em `docker/postgres/init/01-create-email-database.sql`, que cria os bancos `auth` e `email`.

## Como rodar localmente

Suba apenas o PostgreSQL:

```bash
docker compose up -d postgres-dev
```

Em um terminal, rode o Auth:

```bash
cd Auth
./mvnw spring-boot:run
```

Em outro terminal, rode o email:

```bash
cd email
./mvnw spring-boot:run
```

Os dois microservicos leem variaveis em:

- `../.env`
- `./.env`
- `./Auth/.env` ou `./email/.env`

## Endpoints principais

### Auth

Cadastrar usuario e publicar evento de email:

```http
POST http://localhost:8080/user/save
Content-Type: application/json
```

```json
{
  "name": "Joao Gabriel",
  "email": "jjgabriel10@outlook.com"
}
```

Listar usuarios:

```http
GET http://localhost:8080/user
```

Buscar usuario por id:

```http
GET http://localhost:8080/user/{id}
```

Atualizar usuario:

```http
PUT http://localhost:8080/user/{id}
```

Remover usuario:

```http
DELETE http://localhost:8080/user/{id}
```

### email

Listar ultimos envios:

```http
GET http://localhost:8081/email?limit=10
```

Enviar email direto, sem passar pelo Auth/RabbitMQ:

```http
POST http://localhost:8081/email/send
Content-Type: application/json
```

```json
{
  "emailTo": "jjgabriel10@outlook.com",
  "emailSubject": "Teste",
  "emailBody": "Teste de envio"
}
```

Buscar envio por id:

```http
GET http://localhost:8081/email/{emailId}
```

## Status do email

O campo `emailStatus` usa o enum:

| Valor | Status |
| --- | --- |
| `0` | `PENDING` |
| `1` | `SENT` |
| `2` | `FAILED` |
| `3` | `DELIVERED` |
| `4` | `OPENED` |

Atualmente `SENT` significa que o servidor SMTP aceitou o email. Isso nao garante que o provedor do destinatario colocou a mensagem na caixa de entrada. Se estiver `SENT` e nao chegou, verifique spam, filtros, bloqueios e a caixa de enviados do remetente.

## Testes

Rodar testes do microservico de email:

```bash
cd email
./mvnw test
```

Compilar o Auth:

```bash
cd Auth
./mvnw compile
```

## Seguranca antes do commit

- Nunca commite `.env`.
- Commits devem incluir apenas `.env.example`.
- Se uma senha real ja apareceu no repositorio ou no chat, revogue e gere outra.
- Confira o que vai para o commit com:

```bash
git status
git diff --cached --name-only
```

O aviso sobre `mvnw.cmd` trocar `LF` por `CRLF` e esperado, porque os arquivos `.cmd` sao scripts de Windows.
