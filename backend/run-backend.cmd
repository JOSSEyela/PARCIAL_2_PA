@echo off
REM Lanza el backend aunque la ventana actual tenga un JAVA_HOME invalido.
setlocal
echo JAVA_HOME en esta ventana: [%JAVA_HOME%]

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" goto run

echo JAVA_HOME no apunta a un JDK valido. Buscando un JDK 21 instalado...
set "JAVA_HOME="
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-21*" "C:\Program Files\Microsoft\jdk-21*" "C:\Program Files\Java\jdk-21*") do (
  if exist "%%~D\bin\java.exe" set "JAVA_HOME=%%~D"
)
if not defined JAVA_HOME (
  echo No se encontro un JDK 21. Instala uno o corrige JAVA_HOME.
  exit /b 1
)
echo Usando JAVA_HOME=%JAVA_HOME%

:run
call "%~dp0mvnw.cmd" spring-boot:run %*
