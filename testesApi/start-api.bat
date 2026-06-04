@echo off
REM Script para iniciar a API de Usuários no Windows

echo.
echo ================================
echo    API de Usuarios - Kotlin
echo ================================
echo.
echo Iniciando a API em http://localhost:8080
echo Pressione Ctrl+C para parar
echo.

call gradlew.bat run

pause

