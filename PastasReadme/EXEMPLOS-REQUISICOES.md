# 📋 Exemplos de Requisições - API de Usuários

> Copie e cole esses JSONs em ferramentas como Postman, Insomnia ou cURL

## 1️⃣ Cadastro de Usuário

**URL:** `POST http://localhost:8080/users/register`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "email": "joao@example.com",
  "password": "senha123",
  "name": "João Silva"
}
```

**Esperado:** Status 201 Created

---

## 2️⃣ Login

**URL:** `POST http://localhost:8080/users/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Esperado:** Status 200 OK com token

---

## 3️⃣ Deletar Usuário

**URL:** `POST http://localhost:8080/users/delete`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "userId": "joao@example.com",
  "password": "senha123"
}
```

**Esperado:** Status 200 OK

---

## 4️⃣ Verificar Status (Health Check)

**URL:** `GET http://localhost:8080/health`

**Headers:**
```
Content-Type: application/json
```

**Esperado:** Status 200 OK

---

## 📝 Casos de Teste Recomendados

### ✅ Casos de Sucesso:
1. Registrar novo usuário com dados válidos
2. Fazer login com credenciais corretas
3. Deletar usuário com confirmação de senha
4. Verificar health check

### ❌ Casos de Erro:
1. Registrar com email duplicado (deve retornar 409)
2. Login com email inválido (deve retornar 401)
3. Login com senha incorreta (deve retornar 401)
4. Deletar com senha incorreta (deve retornar 401)
5. Deletar usuário inexistente (deve retornar 404)
6. Enviar campo obrigatório vazio (deve retornar 400)

---

## 🔄 Fluxo Completo de Teste

1. **Cadastro:**
   - Registrar: João Silva (joao@example.com / senha123)
   - Registrar: Maria Santos (maria@example.com / senha456)

2. **Login:**
   - Login João com senha correta → ✅ Sucesso
   - Login João com senha errada → ❌ Erro 401
   - Login com email inexistente → ❌ Erro 401

3. **Deletar:**
   - Deletar Maria com senha correta → ✅ Sucesso
   - Deletar Maria novamente → ❌ Erro 404 (não existe mais)
   - Deletar João com senha errada → ❌ Erro 401

---

## 🛠️ Ferramentas Recomendadas para Testar

- **Postman** - Interface gráfica completa
  - Download: https://www.postman.com/downloads/
  
- **Insomnia** - Alternativa leve
  - Download: https://insomnia.rest/

- **cURL** - Linha de comando (já vem no Windows 10+)
  - Integrado no PowerShell

- **REST Client (VS Code)** - Extensão para VS Code
  - https://marketplace.visualstudio.com/items?itemName=humao.rest-client

---

## 💡 Dicas

- Use o script `test-api.ps1` para testar todas as rotas automaticamente
- Certifique-se de que a API está rodando antes de testar
- A API usa porta 8080 por padrão
- Dados são perdidos ao reiniciar a aplicação (memória)

---

