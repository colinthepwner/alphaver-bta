@echo off
setlocal
title AlphaVer - BTA client

set "GRADLEW=%~dp0gradlew.bat"

cd /d "%~dp0."

if not exist "%GRADLEW%" (
    echo.
    echo   ERROR: gradlew.bat is not next to this script.
    echo   "run game.bat" has to sit in the repository root.
    echo.
    pause
    exit /b 1
)

set "USERNAME_ARG=%~1"

if not exist "%~dp0run" mkdir "%~dp0run"

set "CYPRESS_SRC=%~dp0research\jars\alphaver\ext1605_20_client.jar"
set "CYPRESS_DST=%~dp0run\cypress\ext1605_20_client.jar"
if exist "%CYPRESS_SRC%" if not exist "%CYPRESS_DST%" (
    if not exist "%~dp0run\cypress" mkdir "%~dp0run\cypress"
    mklink /H "%CYPRESS_DST%" "%CYPRESS_SRC%" >nul 2>&1 || copy /y "%CYPRESS_SRC%" "%CYPRESS_DST%" >nul
    if exist "%CYPRESS_DST%" echo   Linked the research copy of Cypress into run\cypress\
)

echo.
echo === Starting Better than Adventure with AlphaVer ===
echo   World and options: run\
echo   Close the game window when you are done.
echo.

if defined USERNAME_ARG (
    call "%GRADLEW%" runClient --args="--username %USERNAME_ARG%"
) else (
    call "%GRADLEW%" runClient
)
if errorlevel 1 (
    echo.
    echo   The client did not start cleanly. Scroll up for the error, or read
    echo   run\logs\latest.log. If no window ever opened, try a clean build:
    echo       gradlew.bat clean build
    echo.
    pause
    exit /b 1
)

endlocal
