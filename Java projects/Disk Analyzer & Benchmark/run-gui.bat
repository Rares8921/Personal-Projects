@echo off
set "JDK_HOME=C:\Program Files\Java\jdk-17"
set "JFX_HOME=C:\Program Files\Java\javafx-sdk-17.0.10"
set "PROJECT_DIR=%~dp0"
set "OUT_DIR=%PROJECT_DIR%out"

"%JDK_HOME%\bin\java" ^
    --module-path "%JFX_HOME%\lib" ^
    --add-modules javafx.controls,javafx.graphics ^
    -Djava.library.path="%PROJECT_DIR%;%OUT_DIR%" ^
    -cp "%OUT_DIR%\classes" ^
    com.diskanalyzer.Main --gui
