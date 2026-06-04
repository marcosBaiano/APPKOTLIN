# 🔐 Autenticação e Segurança - Documentação Completa

## 📋 Resumo

A API agora implementa **autenticação JWT** com **hash de senhas BCrypt**, garantindo segurança completa:

- ✅ Senhas com hash BCrypt (12 rounds)
- ✅ Token JWT com expiração de 10 horas
- ✅ Rotas privadas protegidas por autenticação
- ✅ Validação completa de campos
- ✅ Tratamento seguro de erros

---

## 🔒 FLUXO DE SEGURANÇA

### 1. **Cadastro (Registro)**
```
Usuário envia: email + senha + nome
     ↓
Validação de email/senha/nome
     ↓
Verificar se email já existe
     ↓
Hash da senha com BCrypt (12 rounds)
     ↓
Armazenar: email + hashedPassword + nome + timestamp
```

### 2. **Login**
```
Usuário envia: email + senha
     ↓
Validação de email/senha
     ↓
Buscar usuário no banco
     ↓
Comparar senha com hash (BCrypt.verifyer)
     ↓
Se correto: Gerar JWT Token
     ↓
Retornar: token + email + nome + expiração
```

### 3. **Acesso a Rotas Privadas**
```
Usuário envia: GET/POST/PUT + token no header
     ↓
Ktor valida o token
     ↓
Extrai email do token
     ↓
Se válido: Executa a rota
Se inválido/expirado: Retorna 401 Unauthorized
```

---

## 🔑 NOVO: Endpoints com Autenticação

### Endpoints PÚBLICOS (Sem token necessário)

| Rota | Método | Descrição |
|------|--------|-----------|
| `/users/register` | POST | Cadastrar novo usuário |
| `/users/login` | POST | Fazer login (retorna token) |
| `/users/delete` | POST | Deletar usuário (requer senha) |
| `/health` | GET | Verificar status da API |

### Endpoints PRIVADOS (Requerem token JWT)

| Rota | Método | Descrição |
|------|--------|-----------|
| `/users/profile` | GET | Obter perfil do usuário autenticado |
| `/users/profile` | PUT | Atualizar perfil (nome) |
| `/users/change-password` | POST | Alterar senha |
| `/users/list` | GET | Listar todos os usuários (admin) |

---

## 📝 Exemplos de Uso

### 1️⃣ Cadastro

**Request:**
```bash
POST http://localhost:8080/users/register
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123",
  "name": "João Silva"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Usuário cadastrado com sucesso",
  "data": {
    "email": "joao@example.com",
    "name": "João Silva"
  }
}
```

---

### 2️⃣ Login (IMPORTANTE! ⭐)

**Request:**
```bash
POST http://localhost:8080/users/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "email": "joao@example.com",
    "name": "João Silva",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": "10 horas"
  }
}
```

**⚠️ IMPORTANTE:** Copie o `token` para usar em requisições autenticadas!

---

### 3️⃣ Obter Perfil (Requer Token)

**Request:**
```bash
GET http://localhost:8080/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Perfil do usuário obtido com sucesso",
  "data": {
    "email": "joao@example.com",
    "name": "João Silva",
    "createdAt": "1717344000000"
  }
}
```

---

### 4️⃣ Atualizar Perfil (Requer Token)

**Request:**
```bash
PUT http://localhost:8080/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "name": "João Silva Santos"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Perfil atualizado com sucesso",
  "data": {
    "email": "joao@example.com",
    "name": "João Silva Santos"
  }
}
```

---

### 5️⃣ Alterar Senha (Requer Token)

**Request:**
```bash
POST http://localhost:8080/users/change-password
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "currentPassword": "senha123",
  "newPassword": "novasenha456"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Senha alterada com sucesso"
}
```

---

### 6️⃣ Listar Usuários (Requer Token)

**Request:**
```bash
GET http://localhost:8080/users/list
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lista de usuários obtida com sucesso",
  "count": 2,
  "data": [
    {
      "email": "joao@example.com",
      "name": "João Silva",
      "createdAt": "1717344000000"
    },
    {
      "email": "maria@example.com",
      "name": "Maria Santos",
      "createdAt": "1717344001000"
    }
  ]
}
```

---

## 🔐 Mecanismo de Segurança

### BCrypt - Hash de Senhas

```kotlin
// Cadastro: Hash da senha
val hashedPassword = hashPassword("senha123")
// Resultado: $2a$12$...hashedpassword...

// Login: Verificar senha
val isValid = verifyPassword("senha123", hashedPassword)
// Resultado: true
```

**Benefícios:**
- ✅ Senhas NUNCA são armazenadas em texto plano
- ✅ Impossível reverter o hash para obter a senha original
- ✅ 12 rounds (iterações) = segurança alta
- ✅ Cada hash é único (mesmo para mesma senha)

---

### JWT - Token de Acesso

```
Token Structure:
┌─────────────────┬─────────────────┬─────────────────┐
│     Header      │     Payload     │    Signature    │
├─────────────────┼─────────────────┼─────────────────┤
│ {algorithm}     │ {email, exp}    │ HMAC256(SECRET) │
└─────────────────┴─────────────────┴─────────────────┘

Exemplo:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJlbWFpbCI6ImpvaW9AZXhhbXBsZS5jb20iLCJleHAiOjE3MTczNDQwMDB9.
signature...
```

**Características:**
- ✅ Válido por 10 horas
- ✅ Contém email do usuário
- ✅ Assinado com SECRET (segredo da API)
- ✅ Impossível falsificar sem o SECRET

---

## 📋 Headers de Autenticação

Para acessar rotas privadas, envie o token no header:

```
Authorization: Bearer <token>
```

**Exemplo com cURL:**
```bash
curl -X GET http://localhost:8080/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Exemplo com PowerShell:**
```powershell
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Invoke-WebRequest -Uri "http://localhost:8080/users/profile" `
  -Method Get -Headers $headers
```

---

## ❌ Erros de Autenticação

### 401 Unauthorized - Token Inválido
```json
{
  "success": false,
  "message": "Token inválido ou expirado"
}
```

**Motivos:**
- Sem enviar o token
- Token expirado (mais de 10 horas)
- Token foi alterado/corrompido
- Assinatura inválida

### 401 Unauthorized - Credenciais Incorretas
```json
{
  "success": false,
  "message": "Email ou senha incorretos"
}
```

**Motivos:**
- Email não existe
- Senha errada

---

## 🛠️ Configuração de Segurança

### Em desenvolvimento (atual):
```kotlin
private const val SECRET = "your-secret-key-change-this-in-production-12345678"
private const val VALIDITY_IN_MS = 36_000_000 // 10 horas
```

### Para PRODUÇÃO:
```kotlin
// ⚠️ IMPORTANTE: Alterar estas variáveis!

// 1. Gerar secret seguro (256 bits mínimo)
// Comando: openssl rand -base64 32
private const val SECRET = "sua-chave-secreta-muito-longa-aleatorias..."

// 2. Usar variáveis de ambiente
private val SECRET = System.getenv("JWT_SECRET") ?: throw Exception("JWT_SECRET não configurado")

// 3. Reduzir tempo de expiração se necessário
private const val VALIDITY_IN_MS = 3_600_000 // 1 hora

// 4. Implementar refresh tokens
// 5. HTTPS obrigatório
// 6. Rate limiting
```

---

## 🔍 Fluxo Completo de Teste

### Passo 1: Cadastrar Usuário
```bash
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@example.com","password":"senha123","name":"Teste"}'
```

### Passo 2: Fazer Login
```bash
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@example.com","password":"senha123"}'
```

**Copie o `token` da resposta**

### Passo 3: Acessar Rota Privada
```bash
curl -X GET http://localhost:8080/users/profile \
  -H "Authorization: Bearer COLE_O_TOKEN_AQUI"
```

### Passo 4: Alterar Perfil
```bash
curl -X PUT http://localhost:8080/users/profile \
  -H "Authorization: Bearer COLE_O_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"name":"Novo Nome"}'
```

### Passo 5: Alterar Senha
```bash
curl -X POST http://localhost:8080/users/change-password \
  -H "Authorization: Bearer COLE_O_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"currentPassword":"senha123","newPassword":"novasenha456"}'
```

---

## 📊 Comparativo: Antes vs Depois

| Recurso | Antes | Depois |
|---------|-------|--------|
| Hash de Senha | ❌ Não | ✅ BCrypt (12 rounds) |
| Autenticação | ❌ Nenhuma | ✅ JWT |
| Rotas Privadas | ❌ Não | ✅ Sim |
| Token | ❌ Fake | ✅ Real |
| Expiração | ❌ Nunca | ✅ 10 horas |
| Alterar Senha | ❌ Não | ✅ Sim |
| Perfil | ❌ Não | ✅ Sim |
| Segurança | 🟡 Básica | 🟢 Alta |

---

## 🚀 Próximas Melhorias (Production-Ready)

- [ ] Refresh tokens
- [ ] Rate limiting (anti-brute force)
- [ ] 2FA (Two Factor Authentication)
- [ ] Revogar tokens
- [ ] Auditoria de login
- [ ] HTTPS obrigatório
- [ ] CORS configurado
- [ ] SQL Injection protection
- [ ] Validação de email
- [ ] Recuperação de senha

---

## ⚙️ Tecnologias Utilizadas

- **BCrypt**: at.favre.lib:bcrypt:0.9.0
- **JWT**: com.auth0:java-jwt:4.4.0
- **Ktor Auth**: io.ktor:ktor-server-auth-jwt:2.3.7

---

**Status**: ✅ Autenticação JWT + BCrypt implementada com sucesso!

Bom desenvolvimento seguro! 🔐

