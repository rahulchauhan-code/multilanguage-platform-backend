# Test PostgreSQL Connection to Render
Write-Host "Testing PostgreSQL Connection to Render..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$host = "dpg-d5ns5afgi27c738itcsg-a"
$port = 5432
$database = "blogplatform_tuvn"
$username = "blogplatform_tuvn_user"
$password = "Mb5sw979LEl9s4nXQeQrhmWdTyahIPZN"

Write-Host "`nConnection Details:" -ForegroundColor Yellow
Write-Host "  Host: $host"
Write-Host "  Port: $port"
Write-Host "  Database: $database"
Write-Host "  Username: $username"
Write-Host ""

# Test network connectivity first
Write-Host "1. Testing network connectivity..." -ForegroundColor Yellow
try {
    $result = Test-NetConnection -ComputerName $host -Port $port -InformationLevel Quiet -WarningAction SilentlyContinue
    if ($result) {
        Write-Host "   ✓ Network connection successful" -ForegroundColor Green
    } else {
        Write-Host "   ✗ Cannot reach host:port" -ForegroundColor Red
        Write-Host "   Note: Render databases are only accessible from:" -ForegroundColor Yellow
        Write-Host "     - Other Render services (internal network)" -ForegroundColor Yellow
        Write-Host "     - External hostname (if external access enabled)" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "   Try external hostname instead:" -ForegroundColor Cyan
        Write-Host "   dpg-d5ns5afgi27c738itcsg-a.oregon-postgres.render.com" -ForegroundColor White
    }
} catch {
    Write-Host "   ✗ Network test failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "2. JDBC URL format:" -ForegroundColor Yellow
Write-Host "   jdbc:postgresql://${host}:${port}/${database}" -ForegroundColor White

Write-Host ""
Write-Host "Note: Render PostgreSQL instances require:" -ForegroundColor Cyan
Write-Host "  - SSL connection (add ?sslmode=require for external)" -ForegroundColor White
Write-Host "  - Access from Render services or whitelisted IPs" -ForegroundColor White
Write-Host ""
