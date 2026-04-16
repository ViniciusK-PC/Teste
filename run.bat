@echo off
setlocal
set "BASE_DIR=%~dp0"

:: Configuracao de caminho do Java - Garantindo que tenha aspas apenas se houver espacos
set JAVA_EXE=java
if exist "C:\Program Files (x86)\Java\jdk-17.0.0.1\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files (x86)\Java\jdk-17.0.0.1\bin\java.exe"
)

title Coupon Management System - Menu Control

:MENU
cls
echo ============================================================
echo   SEJAM BEM VINDOS AO MEU TESTE TECNICO, BRUNO E ROBSON!
echo              NAO SE ASSUSTEM, ESTA TUDO SOB CONTROLE!
echo ============================================================
echo.
echo ============================================================
echo           COUPON MANAGEMENT SYSTEM - MENU DE CONTROLE
echo ============================================================
echo.
echo [1] Rodar BACK-END (Maven Local + Swagger)
echo [2] Rodar FULL STACK (Front + Back Local)
echo [3] Rodar Containers (Docker Compose) /\ (APP - GERAL)
echo [4] Rodar TESTES (Maven Test)
echo [5] Ver Dados (Listar Cupons via Terminal)
echo [6] Sair
echo.
echo ============================================================
set /p opt="Escolha uma opcao: "

if "%opt%"=="1" goto BACKEND
if "%opt%"=="2" goto FULLSTACK
if "%opt%"=="3" goto DOCKER
if "%opt%"=="4" goto TESTES
if "%opt%"=="5" goto VIEW_DATA
if "%opt%"=="6" goto OPEN_ATLAS
if "%opt%"=="7" exit
goto MENU

:BACKEND
:BACKEND_LOCAL
cls
echo [INFO] Compilando e iniciando Back-end...
call "%BASE_DIR%mvnw.cmd" clean package -DskipTests
cls
echo ============================================================
echo    BACK-END INICIANDO... O SWAGGER ABRIRA EM INSTANTES
echo ============================================================
start "" http://localhost:8080/swagger-ui.html
start "Coupon-Backend-Server" "%JAVA_EXE%" -jar "%BASE_DIR%target\coupon-challenge-0.0.1-SNAPSHOT.jar"
echo.
echo [OK] Servidor lancado em uma nova janela!
echo Ja voce pode usar as outras opcoes do menu.
echo.
pause
goto MENU

:BACKEND_DOCKER
cls
echo [INFO] Montando e iniciando Docker...
docker build -t coupon-api .
docker run -p 8080:8080 coupon-api
pause
goto MENU

:FULLSTACK
:FRONTEND_FULL
cls
echo [INFO] Iniciando FULL STACK (Back + Front)...
call "%BASE_DIR%mvnw.cmd" clean package -DskipTests
cls
echo [INFO] Abrindo o Front-end...
start "" "%BASE_DIR%FrontEND\index.html"
echo [INFO] Iniciando o servidor em nova janela...

:: Técnica robusta para abrir nova janela com caminhos que podem conter espaços
cd /d "%BASE_DIR%"
start "Coupon-Backend-Server" cmd /k ""%JAVA_EXE%" -jar "target\coupon-challenge-0.0.1-SNAPSHOT.jar""

goto MENU

:DOCKER
:ALL_DOCKER
cls
echo [INFO] Iniciando Stack Completa via Docker Compose...
docker-compose up --build
pause
goto MENU

:TESTES
:RUN_TESTS
cls
echo [INFO] Executando Testes...
call mvn clean test
pause
goto MENU

:VIEW_DATA
:VIEW_TABLES
cls
echo ============================================================
echo           INSPECIONANDO MONGODB VIA JAVA (NATIVO)
echo ============================================================
echo.
echo [INFO] Conectando ao MongoDB Atlas e recuperando dados...
echo.
call "%BASE_DIR%mvnw.cmd" exec:java -Dexec.mainClass="com.example.coupon.tools.DatabaseInspector" -q
echo.
pause
goto MENU

:OPEN_ATLAS
cls
echo Abrindo MongoDB Atlas no navegador...
start https://cloud.mongodb.com/
pause
goto MENU

:EXIT
exit
