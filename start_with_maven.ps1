$ErrorActionPreference = "Stop"

# Use local .mvn structure
$LocalMvnDir = Join-Path $PSScriptRoot ".mvn_local"
$MavenVersion = "3.9.6"
$MavenBinZip = "apache-maven-$MavenVersion-bin.zip"
$MavenUrl = "https://archive.apache.org/dist/maven/maven-3/$MavenVersion/binaries/$MavenBinZip"
$MavenHome = Join-Path $LocalMvnDir "apache-maven-$MavenVersion"
$MvnCmd = Join-Path $MavenHome "bin\mvn.cmd"

# Create .mvn_local directory if it doesn't exist
if (-not (Test-Path $LocalMvnDir)) {
    New-Item -ItemType Directory -Path $LocalMvnDir | Out-Null
}

# Check if Maven is already set up locally
if (-not (Test-Path $MvnCmd)) {
    Write-Host "Maven not found locally. Downloading Apache Maven $MavenVersion..."
    $ZipPath = Join-Path $LocalMvnDir $MavenBinZip
    
    # Download Maven
    Invoke-WebRequest -Uri $MavenUrl -OutFile $ZipPath
    
    Write-Host "Extracting Maven..."
    Expand-Archive -Path $ZipPath -DestinationPath $LocalMvnDir -Force
    
    # Clean up zip
    Remove-Item $ZipPath
    
    Write-Host "Maven set up successfully."
}

# Add Maven to PATH for this session
$env:PATH = "$(Join-Path $MavenHome 'bin');$env:PATH"

# Verify installation
mvn -version

# Run the Spring Boot application
Write-Host "Starting Spring Boot Application..."
mvn spring-boot:run
