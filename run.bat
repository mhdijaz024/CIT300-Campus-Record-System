@echo off
REM ===================================================================
REM  CIT300 - University Student Record and Campus Route Management
REM  Compiles the project into bin\ and starts the console application.
REM ===================================================================

echo.
echo  Compiling the project...

if not exist bin mkdir bin

javac -d bin src\model\*.java src\structures\*.java src\service\*.java src\ui\*.java src\app\*.java

if errorlevel 1 (
    echo.
    echo  COMPILATION FAILED. Check the errors above.
    pause
    exit /b 1
)

echo  Compilation successful.
echo.
echo  Choose what to run:
echo    1. The application
echo    2. The automated test suite
echo.
set /p choice="  Enter 1 or 2: "

if "%choice%"=="2" (
    java -cp bin app.StructureTest
) else (
    java -cp bin app.Main
)

echo.
pause
