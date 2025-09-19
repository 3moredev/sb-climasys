# Test script for login API
Write-Host "Testing Climasys Login API..." -ForegroundColor Green

# Test 1: Database connection
Write-Host "`n1. Testing database connection..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/test/database" -Method GET
    Write-Host "✅ Database connection: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "❌ Database connection failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Function availability
Write-Host "`n2. Testing function availability..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/test/function" -Method GET
    Write-Host "✅ Function test: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "❌ Function test failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Login API with correct credentials
Write-Host "`n3. Testing login API with correct credentials..." -ForegroundColor Yellow
$loginBody = @{
    loginId = "test_user"
    password = "test_password"
    todaysDay = "Monday"
    languageId = 1
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "✅ Login successful: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody" -ForegroundColor Red
    }
}

# Test 4: Login API with incorrect credentials
Write-Host "`n4. Testing login API with incorrect credentials..." -ForegroundColor Yellow
$wrongLoginBody = @{
    loginId = "wrong_user"
    password = "wrong_password"
    todaysDay = "Monday"
    languageId = 1
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $wrongLoginBody -ContentType "application/json"
    Write-Host "✅ Login test (should fail): $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "❌ Login test failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody" -ForegroundColor Red
    }
}

Write-Host "`nTest completed!" -ForegroundColor Green
