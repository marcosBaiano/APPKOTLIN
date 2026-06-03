#!/bin/env bash

# Script de testes para a API de Usuários
# Este arquivo contém exemplos de requisições cURL para testar todas as rotas

API_URL="http://localhost:8080"

echo "================================"
echo "Testando API de Usuários"
echo "================================"
echo ""

# 1. Health Check
echo "1️⃣  Health Check (GET /health)"
echo "================================"
curl -X GET "$API_URL/health" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 2. Cadastro de Usuário
echo "2️⃣  Cadastro de Usuário (POST /users/register)"
echo "================================"
curl -X POST "$API_URL/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha123",
    "name": "João Silva"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 3. Tentar registrar com mesmo email (deve falhar)
echo "3️⃣  Registrar com Email Duplicado (POST /users/register) - Deve Falhar"
echo "================================"
curl -X POST "$API_URL/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha456",
    "name": "Outro João"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 4. Registrar outro usuário
echo "4️⃣  Registrar outro Usuário (POST /users/register)"
echo "================================"
curl -X POST "$API_URL/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@example.com",
    "password": "senha789",
    "name": "Maria Santos"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 5. Login com sucesso
echo "5️⃣  Login com Sucesso (POST /users/login)"
echo "================================"
curl -X POST "$API_URL/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha123"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 6. Login com senha incorreta
echo "6️⃣  Login com Senha Incorreta (POST /users/login) - Deve Falhar"
echo "================================"
curl -X POST "$API_URL/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senhaErrada"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 7. Login com email inexistente
echo "7️⃣  Login com Email Inexistente (POST /users/login) - Deve Falhar"
echo "================================"
curl -X POST "$API_URL/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "inexistente@example.com",
    "password": "senha"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 8. Deletar usuário com sucesso
echo "8️⃣  Deletar Usuário com Sucesso (POST /users/delete)"
echo "================================"
curl -X POST "$API_URL/users/delete" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "maria@example.com",
    "password": "senha789"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 9. Tentar deletar usuário já deletado
echo "9️⃣  Deletar Usuário Inexistente (POST /users/delete) - Deve Falhar"
echo "================================"
curl -X POST "$API_URL/users/delete" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "maria@example.com",
    "password": "senha789"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo ""

# 10. Deletar com senha incorreta
echo "🔟  Deletar com Senha Incorreta (POST /users/delete) - Deve Falhar"
echo "================================"
curl -X POST "$API_URL/users/delete" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "joao@example.com",
    "password": "senhaErrada"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo "✅ Testes concluídos!"
echo "================================"

