@echo off
setlocal
if "%~1"=="" (
  echo Uso: run.bat arquivo-fonte [--all-errors]
  exit /b 2
)
if "%~2"=="--all-errors" (
  java -jar "%~dp0dist\compilador-etapa1.jar" --all-errors "%~1"
) else (
  java -jar "%~dp0dist\compilador-etapa1.jar" "%~1"
)
exit /b %errorlevel%

