# Build and Deploy Langflow JAR to Mendix Project
# This script builds the Langflow Java Action and copies it to your Mendix project

param(
    [string]$MendixProjectPath = "C:\Mendix Projects\Sample"
)

Write-Host "=== Langflow JAR Build and Deploy Script ===" -ForegroundColor Green
Write-Host "Target Mendix Project: $MendixProjectPath" -ForegroundColor Yellow

# Set the working directory to the Langflow project
$LangflowProjectPath = "C:\Extensions\Langflow"
Set-Location $LangflowProjectPath

Write-Host "`nStep 1: Cleaning previous builds..." -ForegroundColor Cyan
if (Test-Path "build\libs") {
    Remove-Item "build\libs\*" -Force
    Write-Host "Previous build artifacts cleaned"
}

Write-Host "`nStep 2: Compiling Java sources..." -ForegroundColor Cyan
$javaFiles = Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java"
$classesDir = "build\classes"
if (!(Test-Path $classesDir)) {
    New-Item -ItemType Directory -Path $classesDir -Force | Out-Null
}

# Compile Java files
javac -cp "src/main/java" -d $classesDir $javaFiles.FullName
if ($LASTEXITCODE -eq 0) {
    Write-Host "Java compilation successful" -ForegroundColor Green
} else {
    Write-Host "Java compilation failed" -ForegroundColor Red
    exit 1
}

Write-Host "`nStep 3: Creating JAR file..." -ForegroundColor Cyan
$libsDir = "build\libs"
if (!(Test-Path $libsDir)) {
    New-Item -ItemType Directory -Path $libsDir -Force | Out-Null
}

jar -cvf "$libsDir\Langflow-1.0.0-mendix.jar" -C $classesDir .
if ($LASTEXITCODE -eq 0) {
    Write-Host "JAR creation successful" -ForegroundColor Green
} else {
    Write-Host "JAR creation failed" -ForegroundColor Red
    exit 1
}

Write-Host "`nStep 4: Preparing Mendix project directory..." -ForegroundColor Cyan
$mendixUserLib = Join-Path $MendixProjectPath "userlib"
if (!(Test-Path $mendixUserLib)) {
    Write-Host "Creating userlib directory: $mendixUserLib"
    New-Item -ItemType Directory -Path $mendixUserLib -Force | Out-Null
} else {
    Write-Host "Mendix userlib directory exists: $mendixUserLib"
}

Write-Host "`nStep 5: Copying JAR to Mendix project..." -ForegroundColor Cyan
$sourceJar = "$libsDir\Langflow-1.0.0-mendix.jar"
$targetJar = Join-Path $mendixUserLib "Langflow-1.0.0-mendix.jar"

if (Test-Path $sourceJar) {
    Copy-Item $sourceJar $targetJar -Force
    Write-Host "Successfully copied JAR to: $targetJar" -ForegroundColor Green
    
    # Show file information
    $jarInfo = Get-Item $targetJar
    Write-Host "JAR Size: $($jarInfo.Length) bytes" -ForegroundColor Yellow
    Write-Host "JAR Modified: $($jarInfo.LastWriteTime)" -ForegroundColor Yellow
} else {
    Write-Host "Source JAR not found: $sourceJar" -ForegroundColor Red
    exit 1
}

Write-Host "`nStep 6: Verification..." -ForegroundColor Cyan
if (Test-Path $targetJar) {
    Write-Host "✅ JAR successfully deployed to Mendix project" -ForegroundColor Green
    Write-Host "✅ Ready to use in Mendix Studio Pro" -ForegroundColor Green
} else {
    Write-Host "❌ JAR deployment failed" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== Next Steps for Mendix Studio Pro ===" -ForegroundColor Green
Write-Host "1. Refresh your Mendix project in Studio Pro" -ForegroundColor White
Write-Host "2. Create a new Java Action with these parameters:" -ForegroundColor White
Write-Host "   - apiKey (String)" -ForegroundColor Yellow
Write-Host "   - apiEndpoint (String)" -ForegroundColor Yellow
Write-Host "   - userPrompt (String)" -ForegroundColor Yellow
Write-Host "3. Set return type to: String" -ForegroundColor White
Write-Host "4. Add this code to the Java Action:" -ForegroundColor White
Write-Host "   return com.company.mendix.langflow.LangflowAction.execute(apiKey, apiEndpoint, userPrompt);" -ForegroundColor Cyan

Write-Host "`n=== Build and Deploy Complete! ===" -ForegroundColor Green
