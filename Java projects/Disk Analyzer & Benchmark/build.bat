@echo off
setlocal enabledelayedexpansion

set "JDK_HOME=C:\Program Files\Java\jdk-17"
set "JFX_HOME=C:\Program Files\Java\javafx-sdk-17.0.10"
set "MINGW_DIR=C:\msys64\mingw64"
set "GCC=%MINGW_DIR%\bin\gcc.exe"
set "PATH=%MINGW_DIR%\bin;%PATH%"
set "PROJECT_DIR=%~dp0"
set "SRC_DIR=%PROJECT_DIR%src"
set "NATIVE_DIR=%PROJECT_DIR%native"
set "OUT_DIR=%PROJECT_DIR%out"
set "RES_DIR=%PROJECT_DIR%resources"

echo ============================================================
echo   Disk Analyzer / Benchmark - Build System
echo ============================================================
echo.

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
if not exist "%OUT_DIR%\classes" mkdir "%OUT_DIR%\classes"

echo [1/4] Compiling Java sources...

powershell -Command "Get-ChildItem -Path '%SRC_DIR%' -Recurse -Filter *.java | ForEach-Object { '\"' + $_.FullName.Replace('\', '/') + '\"' } | Out-File -FilePath '%OUT_DIR%\sources.txt' -Encoding ASCII"

"%JDK_HOME%\bin\javac" -encoding UTF-8 --module-path "%JFX_HOME%\lib" --add-modules javafx.controls,javafx.graphics -d "%OUT_DIR%\classes" @"%OUT_DIR%\sources.txt"
if errorlevel 1 (
    echo ERROR: Java compilation failed.
    exit /b 1
)
echo   Java compilation successful.

echo.
echo [2/4] Copying resources...
if exist "%RES_DIR%\*.css" (
    copy /Y "%RES_DIR%\*.css" "%OUT_DIR%\classes\"
)
echo   Resources copied.

echo.
echo [3/4] Generating JNI headers...
"%JDK_HOME%\bin\javac" -h "%NATIVE_DIR%\include" --module-path "%JFX_HOME%\lib" --add-modules javafx.controls -d "%OUT_DIR%\jni_tmp" "%SRC_DIR%\com\diskanalyzer\core\NativeBridge.java"
if exist "%OUT_DIR%\jni_tmp" rmdir /s /q "%OUT_DIR%\jni_tmp"
echo   JNI headers generated.

echo.
echo [4/4] Compiling native DLL...
"%GCC%" -shared -o "%OUT_DIR%\diskanalyzer.dll" -I"%NATIVE_DIR%\include" -I"%JDK_HOME%\include" -I"%JDK_HOME%\include\win32" "%NATIVE_DIR%\src\jni_bridge.c" "%NATIVE_DIR%\src\disk_info.c" "%NATIVE_DIR%\src\disk_benchmark.c" "%NATIVE_DIR%\src\file_scanner.c" "%NATIVE_DIR%\src\disk_fragmentation.c" -lkernel32 -lpsapi -O2 -Wall -Wno-unused-function -Wl,--kill-at -static-libgcc
if errorlevel 1 (
    echo ERROR: Native compilation failed.
    exit /b 1
)

copy /Y "%OUT_DIR%\diskanalyzer.dll" "%PROJECT_DIR%diskanalyzer.dll"
echo   Native DLL compiled successfully.

echo.
echo ============================================================
echo   Build complete!
echo   Output: "%OUT_DIR%"
echo.
echo   Run CLI:  run-cli.bat [command] [args]
echo   Run GUI:  run-gui.bat
echo ============================================================