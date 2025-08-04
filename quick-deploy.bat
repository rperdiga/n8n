@echo off
echo === Quick Build and Deploy n8n to Mendix ===
echo.

cd /d "C:\Extensions\n8n JavaAction"

echo Building JAR...
call gradlew copyJarToMendix

if %ERRORLEVEL% == 0 (
    echo.
    echo ✅ SUCCESS: JAR deployed to C:\Mendix Projects\Sample\userlib\
    echo ✅ Ready to use in Mendix Studio Pro
    echo.
    echo Remember to refresh your Mendix project!
) else (
    echo.
    echo ❌ Build failed. Check the output above for errors.
)

pause
