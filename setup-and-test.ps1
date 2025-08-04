# n8n Webhook Java Action Setup Script
# This script sets up if (!$SkipTests) {
    Write-Host "`nStep 4: Run tests..." -ForegroundColor Cyan
    & .\gradlew test
    if ($LASTEXITCODE -eq 0) {
        Write-Host "All tests passed" -ForegroundColor Green
    } else {
        Write-Host "Some tests failed - check output above" -ForegroundColor Yellow
    }
    
    Write-Host "`nStep 5: Run example..." -ForegroundColor Cyan
    try {
        & java -cp "build\classes\java\main" com.company.mendix.n8n.examples.N8nExample
    } catch {
        Write-Host "Example run completed (connection errors expected)" -ForegroundColor Yellow
    }
} else {
    Write-Host "`nStep 4: Skipping tests..." -ForegroundColor Yellow
}

Write-Host "`nStep 6: Build JAR..." -ForegroundColor Cyanhe development environment and builds the JAR

param(
    [string]$MendixProjectPath = "C:\Mendix Projects\Sample"
)

Write-Host "=== n8n Webhook Java Action Setup ===" -ForegroundColor Green
Write-Host "Target Mendix Project: $MendixProjectPath" -ForegroundColor Yellow

# Set working directory
$ProjectPath = "C:\Extensions\n8n JavaAction"
Set-Location $ProjectPath

Write-Host "`nStep 1: Environment Check..." -ForegroundColor Cyan

# Check Java version
$javaVersion = & java -version 2>&1 | Select-String "version"
Write-Host "Java: $javaVersion" -ForegroundColor White

# Check Gradle
try {
    $gradleVersion = & .\gradlew --version 2>&1 | Select-String "Gradle"
    Write-Host "Gradle: $gradleVersion" -ForegroundColor White
} catch {
    Write-Host "Gradle wrapper not found, will use system gradle" -ForegroundColor Yellow
}

Write-Host "`nStep 2: Clean previous builds..." -ForegroundColor Cyan
if (Test-Path "build") {
    Remove-Item "build" -Recurse -Force
    Write-Host "Build directory cleaned" -ForegroundColor Green
}

Write-Host "`nStep 3: Compile sources..." -ForegroundColor Cyan
& .\gradlew compileJava
if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful" -ForegroundColor Green
} else {
    Write-Host "Compilation failed" -ForegroundColor Red
    exit 1
}

Write-Host "`nStep 4: Build JAR..." -ForegroundColor Cyan
& .\gradlew shadowJar
if ($LASTEXITCODE -eq 0) {
    Write-Host "JAR build successful" -ForegroundColor Green
} else {
    Write-Host "JAR build failed" -ForegroundColor Red
    exit 1
}

Write-Host "`nStep 5: Deploy to Mendix..." -ForegroundColor Cyan
$mendixUserLib = Join-Path $MendixProjectPath "userlib"
if (!(Test-Path $mendixUserLib)) {
    Write-Host "Creating Mendix userlib directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $mendixUserLib -Force | Out-Null
}

$sourceJar = "build\libs\n8n-1.0.0-mendix.jar"
$targetJar = Join-Path $mendixUserLib "n8n-1.0.0-mendix.jar"

if (Test-Path $sourceJar) {
    Copy-Item $sourceJar $targetJar -Force
    Write-Host "JAR deployed to Mendix project" -ForegroundColor Green
    
    $jarInfo = Get-Item $targetJar
    Write-Host "Size: $($jarInfo.Length) bytes" -ForegroundColor White
} else {
    Write-Host "JAR file not found" -ForegroundColor Red
    exit 1
}

Write-Host "`nStep 6: Validation..." -ForegroundColor Cyan

# Check JAR contents
Write-Host "JAR Contents:" -ForegroundColor White
& jar -tf $targetJar | Where-Object { $_ -like "*.class" } | ForEach-Object { 
    Write-Host "  $_" -ForegroundColor Gray 
}

Write-Host "`n=== Setup Complete! ===" -ForegroundColor Green

Write-Host "`n=== Next Steps ===" -ForegroundColor Cyan
Write-Host "1. Open Mendix Studio Pro" -ForegroundColor White
Write-Host "2. Refresh your project to recognize the new JAR" -ForegroundColor White
Write-Host "3. Create a Java Action with these parameters:" -ForegroundColor White
Write-Host "   - ApiKey (String) - optional for public webhooks" -ForegroundColor Yellow
Write-Host "   - WebhookEndpoint (String)" -ForegroundColor Yellow
Write-Host "   - InputData (String)" -ForegroundColor Yellow
Write-Host "   - SessionId (String) - required for n8n Simple Memory" -ForegroundColor Yellow
Write-Host "4. Return type: String" -ForegroundColor White
Write-Host "5. Java code:" -ForegroundColor White
Write-Host "   return com.company.mendix.n8n.N8nAction.execute(ApiKey, WebhookEndpoint, InputData, SessionId);" -ForegroundColor Cyan

Write-Host "`n=== Example n8n Webhook URLs ===" -ForegroundColor Cyan
Write-Host "Production: https://your-n8n.company.com/webhook/workflow-name" -ForegroundColor White
Write-Host "Testing: https://webhook.site/#!/unique-id (for testing)" -ForegroundColor White
Write-Host "Local: http://localhost:5678/webhook/test-workflow" -ForegroundColor White

Write-Host "`n=== Documentation ===" -ForegroundColor Cyan
Write-Host "README.md - Complete usage guide" -ForegroundColor White
Write-Host "MENDIX_IMPLEMENTATION_GUIDE.md - Step-by-step Mendix setup" -ForegroundColor White
Write-Host "TIMEOUT_CONFIGURATION_GUIDE.md - Timeout configuration help" -ForegroundColor White

Write-Host "`n=== Ready to use! ===" -ForegroundColor Green
