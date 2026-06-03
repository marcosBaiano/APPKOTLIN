# 🔑 Postman/Insomnia - Exemplos com Autenticação

Esta é uma coleção de requisições de exemplo para testar a API com autenticação JWT.

## 📌 Postman: Importar coleção

1. Abra Postman
2. Clique em "Import"
3. Cole o JSON abaixo
4. Clique em "Import"

## 📌 Setup Necessário

Antes de testar, configure:

**Variável de Ambiente:**
- Variable: `token`
- Value: (deixar vazio, será preenchido automaticamente)

**Base URL:**
- `{{baseUrl}}` = `http://localhost:8080`

---

## 📋 REQUISIÇÕES

### 1️⃣ CADASTRO

**Endpoint:** `POST {{baseUrl}}/users/register`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "usuario@example.com",
  "password": "senha123",
  "name": "Nome do Usuário"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Usuário cadastrado com sucesso",
  "data": {
    "email": "usuario@example.com",
    "name": "Nome do Usuário"
  }
}
```

---

### 2️⃣ LOGIN (Obter Token)

**Endpoint:** `POST {{baseUrl}}/users/login`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "usuario@example.com",
  "password": "senha123"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "email": "usuario@example.com",
    "name": "Nome do Usuário",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": "10 horas"
  }
}
```

**📌 Ação no Postman:**
1. Na aba "Tests", adicionar:
```javascript
var jsonData = pm.response.json();
pm.environment.set("token", jsonData.data.token);
```

---

### 3️⃣ OBTER PERFIL (Privado)

**Endpoint:** `GET {{baseUrl}}/users/profile`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Perfil do usuário obtido com sucesso",
  "data": {
    "email": "usuario@example.com",
    "name": "Nome do Usuário",
    "createdAt": "1717344000000"
  }
}
```

---

### 4️⃣ ATUALIZAR PERFIL (Privado)

**Endpoint:** `PUT {{baseUrl}}/users/profile`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body (JSON):**
```json
{
  "name": "Novo Nome do Usuário"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Perfil atualizado com sucesso",
  "data": {
    "email": "usuario@example.com",
    "name": "Novo Nome do Usuário"
  }
}
```

---

### 5️⃣ ALTERAR SENHA (Privado)

**Endpoint:** `POST {{baseUrl}}/users/change-password`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body (JSON):**
```json
{
  "currentPassword": "senha123",
  "newPassword": "novaSenha456"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Senha alterada com sucesso"
}
```

---

### 6️⃣ LISTAR USUÁRIOS (Privado)

**Endpoint:** `GET {{baseUrl}}/users/list`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Lista de usuários obtida com sucesso",
  "count": 2,
  "data": [
    {
      "email": "usuario1@example.com",
      "name": "Usuário 1",
      "createdAt": "1717344000000"
    },
    {
      "email": "usuario2@example.com",
      "name": "Usuário 2",
      "createdAt": "1717344001000"
    }
  ]
}
```

---

### 7️⃣ DELETAR USUÁRIO

**Endpoint:** `POST {{baseUrl}}/users/delete`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "userId": "usuario@example.com",
  "password": "senha123"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Usuário deletado com sucesso",
  "data": {
    "deletedUser": "usuario@example.com"
  }
}
```

---

### 8️⃣ HEALTH CHECK

**Endpoint:** `GET {{baseUrl}}/health`

**Headers:**
```
Content-Type: application/json
```

**Response (200):**
```json
{
  "success": true,
  "message": "API está funcionando"
}
```

---

## 🔐 SEGURANÇA

### Header de Autenticação

Todas as rotas privadas (com **🔒**) requerem o header:

```
Authorization: Bearer <TOKEN_JWT>
```

### Token JWT

Estrutura:
```
Header.Payload.Signature
```

Exemplo:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJlbWFpbCI6InVzdWFyaW9AZXhhbXBsZS5jb20iLCJleHAiOjE3MTczNDQwMDB9.
signature...
```

### Validade do Token

- **Duração:** 10 horas
- **Após expirado:** Fazer login novamente

---

## 🔄 Fluxo Típico no Postman

1. **Executar:** Cadastro
   - Registre um novo usuário

2. **Executar:** Login
   - Faça login e obtenha o token
   - ✅ Token será salvo automaticamente

3. **Executar:** Obter Perfil
   - Acesse dados do usuário usando o token

4. **Executar:** Atualizar Perfil
   - Atualize o nome usando o token

5. **Executar:** Alterar Senha
   - Mude a senha usando o token

6. **Executar:** Listar Usuários
   - Liste todos os usuários usando o token

7. **Executar:** Deletar Usuário
   - Remova o usuário

---

## ⚠️ Erros Comuns

| Erro | Motivo | Solução |
|------|--------|---------|
| 401 Unauthorized | Token ausente/inválido | Fazer login novamente |
| 401 Unauthorized | Token expirado | Fazer login novamente |
| 400 Bad Request | Campo obrigatório vazio | Verificar o JSON |
| 409 Conflict | Email já cadastrado | Usar outro email |
| 404 Not Found | Usuário não existe | Verificar o email |

---

## 🧪 Teste Rápido

### cURL - Fluxo completo:

```bash
# 1. Cadastro
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"senha123","name":"Test"}'

# 2. Login (copiar o token)
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"senha123"}'

# 3. Obter perfil (substituir TOKEN)
curl -X GET http://localhost:8080/users/profile \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json"

# 4. Atualizar perfil
curl -X PUT http://localhost:8080/users/profile \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Novo Nome"}'
```

---

**Última atualização:** 2024-06-02  
**Status:** ✅ Documentação Atualizada

