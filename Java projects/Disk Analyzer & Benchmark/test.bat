@echo off
setlocal enabledelayedexpansion

echo ============================================
echo  Disk Analyzer ^& Benchmark - Test Runner
echo ============================================
echo.

set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

set SRC_DIR=src
set TEST_DIR=test
set OUT_DIR=out\classes
set TEST_OUT_DIR=out\test-classes
set LIB_DIR=lib

REM Check for JUnit
if not exist "%LIB_DIR%\junit-platform-console-standalone-1.10.2.jar" (
    echo ERROR: JUnit jars not found in %LIB_DIR%
    echo Please ensure junit-platform-console-standalone-1.10.2.jar is present.
    exit /b 1
)

echo [Step 1/4] Compiling main sources (core only, no GUI)...
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

REM Collect only core + cli source files (exclude gui/ and Main.java which requires JavaFX)
set SRC_FILES=
for /r %SRC_DIR%\com\diskanalyzer\core %%f in (*.java) do set SRC_FILES=!SRC_FILES! "%%f"
for /r %SRC_DIR%\com\diskanalyzer\cli %%f in (*.java) do set SRC_FILES=!SRC_FILES! "%%f"

javac -d "%OUT_DIR%" -encoding UTF-8 --release 17 %SRC_FILES%
if errorlevel 1 (
    echo.
    echo ERROR: Main source compilation failed!
    exit /b 1
)
echo    Main sources compiled successfully.

echo.
echo [Step 2/4] Compiling test sources...
if exist "%TEST_OUT_DIR%" rmdir /s /q "%TEST_OUT_DIR%"
mkdir "%TEST_OUT_DIR%"

REM Build classpath: main classes + all JUnit jars
set TEST_CP=%OUT_DIR%
for %%j in (%LIB_DIR%\*.jar) do set TEST_CP=!TEST_CP!;%%j

REM Collect all test .java files
set TEST_FILES=
for /r %TEST_DIR% %%f in (*.java) do set TEST_FILES=!TEST_FILES! "%%f"

javac -d "%TEST_OUT_DIR%" -encoding UTF-8 --release 17 -cp "%TEST_CP%" %TEST_FILES%
if errorlevel 1 (
    echo.
    echo ERROR: Test source compilation failed!
    exit /b 1
)
echo    Test sources compiled successfully.

echo.
echo [Step 3/4] Running all tests...
echo ============================================

java -jar "%LIB_DIR%\junit-platform-console-standalone-1.10.2.jar" ^
    --class-path "%OUT_DIR%;%TEST_OUT_DIR%" ^
    --scan-class-path "%TEST_OUT_DIR%" ^
    --details verbose

set TEST_EXIT=%ERRORLEVEL%

echo.
echo ============================================
if %TEST_EXIT% equ 0 (
    echo  ALL TESTS PASSED
) else (
    echo  SOME TESTS FAILED (exit code: %TEST_EXIT%)
)
echo ============================================

exit /b %TEST_EXIT%
