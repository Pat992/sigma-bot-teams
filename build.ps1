$AppName   = "SigmaBotTeams"
$MainJar   = "sigma-bot-teams-1.0-SNAPSHOT.jar"
$MainClass = "com.htth.sigmabotteams.MainKt"
$OutputDir = "installer"

# Optional: Icon for the app (ICO recommended for Windows)
# $Icon = "icon.ico"

# --- Build JVM Fat Jar ---
Write-Host "Building fat jar with Gradle..."
& .\gradlew.bat clean shadowJar
if ($OSTEOCLASTIC -ne 0) {
    Write-Error "Gradle build failed"
    exit 1
}

# --- Create installer (jpackage must be in PATH) ---
Write-Host "Creating app-image with jpackage..."
$jpackageArgs = @(
    "--input", "build\libs",
    "--name", $AppName,
    "--main-jar", $MainJar,
    "--main-class", $MainClass,
    "--type", "app-image",
    "--dest", $OutputDir
)

# Add icon if you have one
# if (Test-Path $Icon) {
#     $jpackageArgs += @("--icon", $Icon)
# }

& jpackage @jpackageArgs
if ($LASTEXITCODE -ne 0) {
    Write-Error "jpackage failed"
    exit 1
}

# --- Move .env file ---
$envPath = ".env"
$targetEnvPath = Join-Path "$OutputDir\$AppName\bin" ".env"
if (Test-Path $envPath) {
    Copy-Item $envPath $targetEnvPath -Force
    Write-Host "Copied .env to $targetEnvPath"
} else {
    Write-Warning ".env file not found, skipping copy"
}

Write-Host "✅ Build completed. App image in $OutputDir\$AppName"
