# 📱 Resumo da API de Usuários

## ✅ O que foi criado

Uma **API REST completa** em Kotlin usando o framework **Ktor** para gerenciar usuários com as seguintes funcionalidades:

---

## 🎯 Rotas Implementadas

| Método | Rota | Descrição | Status|
|--------|------|-----------|--------|
| **POST** | `/users/register` | Cadastro de novo usuário | ✅ |
| **POST** | `/users/login` | Login de usuário | ✅ |
| **POST** | `/users/delete` | Deletar usuário | ✅ |
| **GET** | `/health` | Verificar status da API | ✅ |

---

## 📦 Estrutura do Projeto

```
APPKOTLIN/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── Main.kt                 # 🎯 API com todas as rotas
│   │   └── resources/
│   │       └── logback.xml            # Configuração de logging
│   └── test/
├── build.gradle.kts                   # ⚙️ Dependências do projeto
├── gradle.properties
├── README.md                          # 📚 Documentação completa
├── test-api.sh                        # 🧪 Script de testes (Bash)
├── test-api.ps1                       # 🧪 Script de testes (PowerShell)
└── SUMARIO.md                         # 📋 Este arquivo
```

---

## 🚀 Como Usar

### 1. **Iniciar a API**
```bash
./gradlew run
```

A API estará disponível em: `http://localhost:8080`

### 2. **Testar as Rotas (Windows/PowerShell)**
```powershell
.\test-api.ps1
```

### 3. **Testar as Rotas (Linux/Mac/Bash)**
```bash
bash test-api.sh
```

### 4. **Usar cURL (Manual)**
```bash
# Cadastro
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"senha","name":"João"}'

# Login
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"senha"}'

# Deletar
curl -X POST http://localhost:8080/users/delete \
  -H "Content-Type: application/json" \
  -d '{"userId":"user@example.com","password":"senha"}'
```

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Kotlin 2.3.20
- **Runtime**: JVM 21
- **Framework**: Ktor 2.3.7
- **Serialização**: Kotlinx Serialization (JSON)
- **Build Tool**: Gradle 9.2.1
- **Logging**: Logback 1.4.11

---

## 💾 Dados & Armazenamento

- Dados armazenados **em memória** (não persistem após reiniciar)
- Usável para testes e desenvolvimento
- Em produção, integrar com banco de dados real (PostgreSQL, MongoDB, etc.)

---

## ✨ Recursos Principais

✅ **Cadastro de Usuários**
- Validação de campos obrigatórios
- Prevenção de emails duplicados
- Retorno de dados do usuário criado

✅ **Login**
- Validação de credenciais
- Geração de token (fake JWT para demo)
- Retorno de informações do usuário

✅ **Deletar Usuário**
- Confirmação com senha
- Validação de usuário existente
- Remoção segura de dados

✅ **Tratamento de Erros**
- Respostas com HTTP status corretos
- Mensagens de erro descritivas
- Response padronizado com `success` e `message`

✅ **Logging**
- Configuração de logging com Logback
- Rastreamento de requisições
- Facilita debug e monitoramento

---

## 📊 Resposta Padrão da API

Todas as rotas retornam um JSON padronizado:

```json
{
  "success": boolean,
  "message": "Descrição da resposta",
  "data": {
    "campo1": "valor1",
    "campo2": "valor2"
  }
}
```

---

## 🔐 Nota sobre Segurança

⚠️ **IMPORTANTE**: Este projeto é para fins educacionais/desenvolvimento!

Para produção, implementar:
- ✅ Hash de senhas com BCrypt/Argon2
- ✅ Autenticação JWT robusta
- ✅ HTTPS/TLS
- ✅ Validação de email
- ✅ Rate limiting
- ✅ CORS configurado
- ✅ Banco de dados seguro

---

## 📝 Próximos Passos Sugeridos

1. Integrar com banco de dados (Exposed ORM ou JPA)
2. Implementar autenticação JWT
3. Adicionar validação de email
4. Hash seguro de senhas
5. Testes unitários e de integração
6. Swagger/OpenAPI para documentação interativa
7. Docker para containerização
8. CI/CD com GitHub Actions

---

## 📞 Suporte

Para documentação detalhada, veja **README.md**

---

**Status**: ✅ API pronta para testes e desenvolvimento!

