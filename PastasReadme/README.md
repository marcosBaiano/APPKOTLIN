# API de Usuários - Documentação

Esta é uma API REST desenvolvida em **Kotlin com Ktor** para gerenciar usuários (cadastro, login e deletar).

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Gradle (incluído no projeto)

### Iniciar a API
```bash
./gradlew run
```

A API estará disponível em: `http://localhost:8080`

---

## 📋 Rotas Disponíveis

### 1. **Cadastro de Usuário** - `POST /users/register`

Registra um novo usuário no sistema.

**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "senha123",
  "name": "João Silva"
}
```

**Response (Success - 201):**
```json
{
  "success": true,
  "message": "Usuário cadastrado com sucesso",
  "data": {
    "email": "usuario@example.com",
    "name": "João Silva"
  }
}
```

**Response (Error - 400):**
```json
{
  "success": false,
  "message": "Email, senha e nome são obrigatórios"
}
```

**Response (Error - 409):**
```json
{
  "success": false,
  "message": "Este email já está cadastrado"
}
```

---

### 2. **Login** - `POST /users/login`

Realiza login de um usuário existente.

**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "senha123"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "email": "usuario@example.com",
    "name": "João Silva",
    "token": "fake-jwt-token-1234567890"
  }
}
```

**Response (Error - 400):**
```json
{
  "success": false,
  "message": "Email e senha são obrigatórios"
}
```

**Response (Error - 401):**
```json
{
  "success": false,
  "message": "Email ou senha incorretos"
}
```

---

### 3. **Deletar Usuário** - `POST /users/delete`

Deleta um usuário existente (requer confirmação de senha).

**Request:**
```json
{
  "userId": "usuario@example.com",
  "password": "senha123"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Usuário deletado com sucesso",
  "data": {
    "deletedUser": "usuario@example.com"
  }
}
```

**Response (Error - 400):**
```json
{
  "success": false,
  "message": "ID do usuário é obrigatório"
}
```

**Response (Error - 401):**
```json
{
  "success": false,
  "message": "Senha incorreta. Não é possível deletar o usuário"
}
```

**Response (Error - 404):**
```json
{
  "success": false,
  "message": "Usuário não encontrado"
}
```

---

### 4. **Health Check** - `GET /health`

Verifica se a API está funcionando.

**Response:**
```json
{
  "success": true,
  "message": "API está funcionando"
}
```

---

## 🧪 Exemplos com cURL

### Cadastro
```bash
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "senha123",
    "name": "João Silva"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "senha123"
  }'
```

### Deletar Usuário
```bash
curl -X POST http://localhost:8080/users/delete \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user@example.com",
    "password": "senha123"
  }'
```

### Health Check
```bash
curl http://localhost:8080/health
```

---

## 📁 Estrutura do Projeto

```
APPKOTLIN/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── Main.kt          # Arquivo principal com todas as rotas
│   │   └── resources/
│   │       └── logback.xml      # Configuração de logging
│   └── test/
├── build.gradle.kts             # Dependências do projeto
├── gradle.properties
└── README.md                    # Esta documentação
```

---

## 🔧 Tecnologias Utilizadas

- **Kotlin** - Linguagem de programação
- **Ktor** - Framework web moderno para Kotlin
- **Gradle** - Gerenciador de build
- **Logback** - Sistema de logging

---

## 📝 Notas Importantes

- Os dados são armazenados **em memória** (mapa). Ao reiniciar a aplicação, os dados são perdidos.
- Para uma aplicação em produção, use um banco de dados real (PostgreSQL, MongoDB, etc.).
- As senhas devem ser **hasheadas** em produção usando bcrypt ou similares.
- Implemente **autenticação JWT** ou OAuth para segurança em produção.

---

## 🎯 Possíveis Melhorias Futuras

1. Integração com banco de dados (Exposed, JPA)
2. Hash seguro de senhas (BCrypt, Argon2)
3. Autenticação JWT
4. Validação de email
5. Testes unitários e de integração
6. Docker para containerização
7. Swagger/OpenAPI para documentação interativa

---

Desenvolvido com ❤️ em Kotlin e Ktor

