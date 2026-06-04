# Script de testes para a API de Usuários (PowerShell)
# Execute este arquivo para testar todas as rotas

$API_URL = "http://localhost:8080"
$headers = @{
    "Content-Type" = "application/json"
}

Write-Host "================================" -ForegroundColor Cyan
Write-Host "Testando API de Usuários" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# 1. Health Check
Write-Host "1️⃣  Health Check (GET /health)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
$response = Invoke-WebRequest -Uri "$API_URL/health" -Method Get -Headers $headers
Write-Host "Response (HTTP $(​$response.StatusCode)):" -ForegroundColor Green
$response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
Write-Host ""

# 2. Cadastro de Usuário
Write-Host "2️⃣  Cadastro de Usuário (POST /users/register)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
$body = @{
    email = "joao@example.com"
    password = "senha123"
    name = "João Silva"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$API_URL/users/register" -Method Post -Headers $headers -Body $body
    Write-Host "Response (HTTP $($response.StatusCode)):" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "Response (HTTP $($_.Exception.Response.StatusCode)):" -ForegroundColor Red
    $_.Exception.Response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
}
Write-Host ""

# 3. Tentar registrar com mesmo email
Write-Host "3️⃣  Registrar com Email Duplicado (POST /users/register) - Deve Falhar" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
$body = @{
    email = "joao@example.com"
    password = "senha456"
    name = "Outro João"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$API_URL/users/register" -Method Post -Headers $headers -Body $body
    Write-Host "Response (HTTP $($response.StatusCode)):" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "Response (HTTP $($_.Exception.Response.StatusCode)):" -ForegroundColor Red
    $_.Exception.Response | ConvertFrom-Json | ConvertTo-Json | Write-Host 2>$null
}
Write-Host ""

# 4. Registrar outro usuário
Write-Host "4️⃣  Registrar outro Usuário (POST /users/register)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
$body = @{
    email = "maria@example.com"
    password = "senha789"
    name = "Maria Santos"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$API_URL/users/register" -Method Post -Headers $headers -Body $body
    Write-Host "Response (HTTP $($response.StatusCode)):" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "Response (HTTP $($_.Exception.Response.StatusCode)):" -ForegroundColor Red
    $_.Exception.Response | ConvertFrom-Json | ConvertTo-Json | Write-Host 2>$null
}
Write-Host ""

# 5. Login com sucesso
Write-Host "5️⃣  Login com Sucesso (POST /users/login)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
$body = @{
    email = "joao@example.com"
    password = "senha123"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$API_URL/users/login" -Method Post -Headers $headers -Body $body
    Write-Host "Response (HTTP $($response.StatusCode)):" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "Response (HTTP $($_.Exception.Response.StatusCode)):" -ForegroundColor Red
}
Write-Host ""

# 6. Login com senha incorreta
Write-Host "6️⃣  Login com Senha Incorreta (POST /users/login) - Deve Falhar" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
$body = @{
    email = "joao@example.com"
    password = "senhaErrada"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$API_URL/users/login" -Method Post -Headers $headers -Body $body
    Write-Host "Response (HTTP $($response.StatusCode)):" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "Response (HTTP $($_.Exception.Response.StatusCode)):" -ForegroundColor Red
}
Write-Host ""

# 7. Deletar usuário com sucesso
Write-Host "7️⃣  Deletar Usuário com Sucesso (POST /users/delete)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
$body = @{
    userId = "maria@example.com"
    password = "senha789"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$API_URL/users/delete" -Method Post -Headers $headers -Body $body
    Write-Host "Response (HTTP $($response.StatusCode)):" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "Response (HTTP $($_.Exception.Response.StatusCode)):" -ForegroundColor Red
}
Write-Host ""

# 8. Deletar com senha incorreta
Write-Host "8️⃣  Deletar com Senha Incorreta (POST /users/delete) - Deve Falhar" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
$body = @{
    userId = "joao@example.com"
    password = "senhaErrada"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$API_URL/users/delete" -Method Post -Headers $headers -Body $body
    Write-Host "Response (HTTP $($response.StatusCode)):" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "Response (HTTP $($_.Exception.Response.StatusCode)):" -ForegroundColor Red
}
Write-Host ""

Write-Host "✅ Testes concluídos!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Cyan

