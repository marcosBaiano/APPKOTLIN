# 🚀 Início Rápido - API de Usuários

## ⚡ Passo 1: Iniciar a API

### Opção 1: Clique no arquivo (Mais fácil 🎯)
```
▶️ Clique 2x em: start-api.bat
```

### Opção 2: Terminal PowerShell
```powershell
.\gradlew run
```

### Opção 3: Terminal Git Bash/WSL
```bash
./gradlew run
```

**Esperado:**
```
2024-XX-XX XX:XX:XX.XXX [main] INFO  ktor.application - Application started in ...
```

A API estará em: `http://localhost:8080` ✅

---

## ⚡ Passo 2: Testar a API

### Opção 1: Script Automático (Recomendado 🎯)
```powershell
.\test-api.ps1
```

### Opção 2: Manual com cURL
```bash
# Cadastro
curl -X POST http://localhost:8080/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"user@example.com\",\"password\":\"senha\",\"name\":\"João\"}"

# Login  
curl -X POST http://localhost:8080/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"user@example.com\",\"password\":\"senha\"}"
```

### Opção 3: Postman/Insomnia
- Veja arquivo `EXEMPLOS-REQUISICOES.md`

---

## 📊 Resumo das Rotas

```
┌─────────────────────────────────────────────────────┐
│  ROTAS DISPONÍVEIS                                  │
├─────────────────────────────────────────────────────┤
│ POST   /users/register  → Cadastro                  │
│ POST   /users/login     → Login                     │
│ POST   /users/delete    → Deletar usuário           │
│ GET    /health          → Verificar status          │
└─────────────────────────────────────────────────────┘
```

---

## 📁 Arquivos do Projeto

| Arquivo | Descrição |
|---------|-----------|
| `src/main/kotlin/Main.kt` | 🎯 **Código principal da API** |
| `build.gradle.kts` | ⚙️ Configuração de dependências |
| `README.md` | 📚 Documentação detalhada |
| `SUMARIO.md` | 📋 Resumo do projeto |
| `EXEMPLOS-REQUISICOES.md` | 💡 Exemplos de JSON para testes |
| `test-api.ps1` | 🧪 Script de testes (PowerShell) |
| `test-api.sh` | 🧪 Script de testes (Bash) |
| `start-api.bat` | ▶️ Atalho para iniciar (Windows) |

---

## ✨ Exemplo de Uso Completo

### 1. Iniciar
```powershell
.\start-api.bat
```

### 2. Em outro terminal, testar
```powershell
# Registrar usuário
$body = @{
    email = "teste@example.com"
    password = "senha123"
    name = "Teste"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/users/register" `
  -Method Post -Headers @{"Content-Type"="application/json"} `
  -Body $body | Select-Object StatusCode, Content
```

**Resposta esperada:**
```json
{
  "success": true,
  "message": "Usuário cadastrado com sucesso",
  "data": {"email": "teste@example.com", "name": "Teste"}
}
```

---

## 🔍 Verificar Logs

Os logs aparecem no terminal onde a API está rodando:

```
2024-XX-XX XX:XX:XX.XXX [eventLoopGroupProxy-4-1] DEBUG org.example - ...
2024-XX-XX XX:XX:XX.XXX [eventLoopGroupProxy-4-1] INFO  ktor.application - ...
```

---

## 🛑 Parar a API

- Pressione: **Ctrl + C** no terminal

---

## ❓ Perguntas Comuns

**P: Onde está o banco de dados?**
> A: Atualmente usa memória. Os dados são perdidos ao reiniciar.

**P: Como mudei para usar um banco de dados?**
> A: Veja seção "Próximos Passos" no README.md

**P: Qual a porta padrão?**
> A: 8080 - pode ser alterada no arquivo `Main.kt`

**P: Como adiciono mais rotas?**
> A: Edite o arquivo `src/main/kotlin/Main.kt` na seção `routing { }`

---

## 🎓 Próximos Passos

1. ✅ Rotas básicas: FEITO!
2. ⬜ Adicionar banco de dados
3. ⬜ Implementar JWT
4. ⬜ Adicionar validação de email
5. ⬜ Hash de senhas
6. ⬜ Testes automatizados
7. ⬜ Docker
8. ⬜ Deploy em nuvem

---

## 📞 Suporte

- 📖 **Documentação Ktor**: https://ktor.io/docs/
- 📖 **Kotlin Docs**: https://kotlinlang.org/docs/
- 💬 **Stack Overflow**: tag `ktor` ou `kotlin`

---

**Status**: ✅ API pronta para testes!

Bom desenvolvimento! 🎉

