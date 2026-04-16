@REM Maven Wrapper script for Windows
@REM Downloads Maven automatically if not present

@echo off
setlocal

set "MAVEN_PROJECTBASEDIR=%~dp0"
set "MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties"
set "MAVEN_HOME=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven"
set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if exist "%MAVEN_CMD%" goto runMaven

echo Maven nao encontrado. Baixando automaticamente...
mkdir "%MAVEN_HOME%" 2>nul

for /f "tokens=1,* delims==" %%a in ('findstr "distributionUrl" "%MAVEN_WRAPPER_PROPERTIES%"') do set "distributionUrl=%%b"

powershell -Command "Invoke-WebRequest -Uri '%distributionUrl%' -OutFile '%MAVEN_HOME%\maven.zip'"
powershell -Command "Expand-Archive -Path '%MAVEN_HOME%\maven.zip' -DestinationPath '%MAVEN_HOME%' -Force"

for /d %%i in ("%MAVEN_HOME%\apache-maven-*") do (
    xcopy "%%i\*" "%MAVEN_HOME%\" /s /e /y /q >nul
    rmdir "%%i" /s /q
)
del "%MAVEN_HOME%\maven.zip"

echo Maven baixado com sucesso!

:runMaven
"%MAVEN_CMD%" %*
