# 📚 REFERÊNCIA RÁPIDA - API de Usuários

## 🌐 Base URL
```
http://localhost:8080
```

---

## 📡 ENDPOINTS PÚBLICOS (Sem Autenticação)

### 1. Cadastro de Usuário
```
POST /users/register
```

**Body:**
```json
{
  "email": "usuario@example.com",
  "password": "senha123",
  "name": "João Silva"
}
```

**Respostas:**
```
201 Created - Sucesso
400 Bad Request - Dados inválidos
409 Conflict - Email já existe
```

---

### 2. Login (⭐ Retorna Token)
```
POST /users/login
```

**Body:**
```json
{
  "email": "usuario@example.com",
  "password": "senha123"
}
```

**Respostas:**
```
200 OK - Login bem-sucedido (com token JWT)
400 Bad Request - Dados inválidos
401 Unauthorized - Email ou senha incorretos
```

---

### 3. Deletar Usuário
```
POST /users/delete
```

**Body:**
```json
{
  "userId": "usuario@example.com",
  "password": "senha123"
}
```

**Respostas:**
```
200 OK - Usuário deletado
400 Bad Request - Dados inválidos
401 Unauthorized - Senha incorreta
404 Not Found - Usuário não existe
```

---

### 4. Health Check
```
GET /health
```

**Resposta:**
```
200 OK - API funcionando
```

---

## 🔐 ENDPOINTS PRIVADOS (Requerem Token JWT)

Envie o token no header:
```
Authorization: Bearer seu-token-aqui
```

### 5. Obter Perfil (🔒 Privado)
```
GET /users/profile
Header: Authorization: Bearer <token>
```

---

### 6. Atualizar Perfil (🔒 Privado)
```
PUT /users/profile
Header: Authorization: Bearer <token>
Body: {"name": "Novo Nome"}
```

---

### 7. Alterar Senha (🔒 Privado)
```
POST /users/change-password
Header: Authorization: Bearer <token>
Body: {"currentPassword": "...", "newPassword": "..."}
```

---

### 8. Listar Usuários (🔒 Privado)
```
GET /users/list
Header: Authorization: Bearer <token>
```

---

## 🧪 EXEMPLOS COM CURL

### Registrar
```bash
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@example.com","password":"senha123","name":"Teste"}'
```

### Login (Copie o token!)
```bash
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@example.com","password":"senha123"}'
```

### Acessar Rota Privada (Substitua TOKEN)
```bash
curl -X GET http://localhost:8080/users/profile \
  -H "Authorization: Bearer TOKEN"
```

### Health Check
```bash
curl http://localhost:8080/health
```

---

## 📊 FORMATO DE RESPOSTA

```json
{
  "success": true,
  "message": "Descrição",
  "data": {"campo": "valor"}
}
```

---

## 🔧 CÓDIGOS HTTP

| Código | Significado |
|--------|-------------|
| 200 | OK - Sucesso |
| 201 | Created - Criado |
| 400 | Bad Request - Dados inválidos |
| 401 | Unauthorized - Não autenticado |
| 404 | Not Found - Não encontrado |
| 409 | Conflict - Email duplicado |

---

## 🔑 SEGURANÇA

- ✅ Senhas com BCrypt (12 rounds)
- ✅ Token JWT (válido 10 horas)
- ✅ Rotas privadas protegidas
- ✅ Validação completa

---

## ⚡ FLUXO TÍPICO

1. `POST /users/register` - Cadastre-se
2. `POST /users/login` - Obtenha token
3. `GET /users/profile` - Acesse (com token)
4. `PUT /users/profile` - Atualize (com token)
5. `POST /users/change-password` - Mude senha (com token)

---

**Última atualização:** 2024-06-02  
**Status:** ✅ Autenticação JWT Implementada
