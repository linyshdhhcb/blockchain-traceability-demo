@echo off
chcp 65001 >nul

REM 区块链溯源系统启动脚本 (Windows)
REM 自动加载 .env 文件并启动 Spring Boot 应用

echo 🚀 启动区块链溯源系统...

REM 检查 .env 文件是否存在
if not exist ".env" (
    echo ❌ 未找到 .env 文件
    echo 📝 请按照以下步骤配置：
    echo    1. 复制 .env.example 为 .env
    echo    2. 编辑 .env 文件，填入正确的私钥和合约地址
    echo.
    echo 💡 示例命令：
    echo    copy .env.example .env
    echo    # 然后编辑 .env 文件
    pause
    exit /b 1
)

REM 加载环境变量
echo 📋 加载环境变量...
for /f "usebackq tokens=1,2 delims==" %%a in (".env") do (
    if not "%%a"=="" if not "%%a:~0,1%"=="#" (
        set "%%a=%%b"
    )
)

REM 检查必要的环境变量
if "%BLOCKCHAIN_PRIVATE_KEY%"=="" (
    echo ❌ 未配置 BLOCKCHAIN_PRIVATE_KEY
    echo 请在 .env 文件中设置正确的私钥
    pause
    exit /b 1
)

if "%BLOCKCHAIN_CONTRACT_ADDRESS%"=="" (
    echo ❌ 未配置 BLOCKCHAIN_CONTRACT_ADDRESS
    echo 请在 .env 文件中设置正确的合约地址
    pause
    exit /b 1
)

echo ✅ 环境变量配置完成
echo 🌐 RPC URL: %BLOCKCHAIN_RPC_URL%
echo 📋 合约地址: %BLOCKCHAIN_CONTRACT_ADDRESS%
echo 🔑 私钥: %BLOCKCHAIN_PRIVATE_KEY:~0,10%...
echo 🚪 服务端口: %SERVER_PORT%

REM 启动 Spring Boot 应用
echo.
echo 🚀 启动 Spring Boot 应用...
mvn spring-boot:run

pause
