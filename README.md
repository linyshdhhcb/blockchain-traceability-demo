# SpringBoot3 + Web3j + Ganache 区块链溯源 Demo

[![GitHub stars](https://img.shields.io/github/stars/linyshdhhcb/blockchain-traceability-demo?style=social)](https://github.com/linyshdhhcb/blockchain-traceability-demo/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/linyshdhhcb/blockchain-traceability-demo?style=social)](https://github.com/linyshdhhcb/blockchain-traceability-demo/fork)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

一个简单的区块链溯源系统演示 demo，基于 **Spring Boot 3**、**Web3j** 和 **Ganache** 构建，实现可信溯源上链。

> **项目地址**: https://github.com/linyshdhhcb/blockchain-traceability-demo.git  
> **作者邮箱**: jingshuihuayue@qq.com

## 🎯 核心特性

| 特性                  | 说明                                                                                       |
| :-------------------- | :----------------------------------------------------------------------------------------- |
| ✅ **全自动合约部署** | 集成 `deploy-contract.js` 脚本，一键自动编译、部署 Solidity 合约到 Ganache，无需手动操作。 |
| ✅ **智能环境检测**   | `test-deployment.js` 工具可自动检查 Node.js 依赖、私钥格式、网络连接、合约文件完整性。     |
| ✅ **后端服务**       | 基于 Spring Boot 3 的 Java 后端，提供稳定、安全的 RESTful API 接口。                       |
| ✅ **简单 API 接口**  | 提供 `add`, `get`, `count`, `status`, `health` 等标准接口，易于前端集成。                  |
| ✅ **配置即用**       | 所有配置清晰明了，`contract-deployment.json` 自动记录部署详情，避免手动复制粘贴错误。      |

## 🛠️ 技术栈

| 技术               | 版本              | 说明                             |
| :----------------- | :---------------- | :------------------------------- |
| **Java / JVM**     | JDK 17            | 稳定的运行时环境                 |
| **Web Framework**  | Spring Boot 3.2.5 | 企业级微服务框架                 |
| **Blockchain SDK** | Web3j 4.8.7       | _降级至稳定版以兼容 Ganache_     |
| **本地测试链**     | Ganache CLI / GUI | 最新版，模拟以太坊网络           |
| **构建工具**       | Maven 3.6+        | 项目依赖管理和打包               |
| **合约语言**       | Solidity ^0.8.0   | 编写 `Traceability.sol` 智能合约 |
| **测试框架**       | JUnit 5           | 单元与集成测试                   |
| **代码生成**       | Lombok            | 减少样板代码                     |

## 🚀 快速开始 (推荐使用自动化脚本)

本项目的核心亮点是**自动化部署**。我强烈推荐您使用 `deploy-contract.js` 脚本，而非手动通过 Remix IDE。

### 1️⃣ 环境准备

**安装 JDK 17**

```bash
java -version
# 应输出: openjdk version "17.x.x"
```

**安装并启动 Ganache**

- **推荐方式 (GUI)**: [下载 Ganache](https://trufflesuite.com/ganache/) 并启动。确保 RPC 服务器为 `http://127.0.0.1:7545`。
- **命令行方式**:

  ```bash
  npm install -g ganache-cli
  ganache-cli -p 7545 -h 0.0.0.0
  ```

  > 💡 **注意**: 默认账户拥有 100 ETH 测试币，确保其可用。

### 2️⃣ 安装 Node.js 依赖 & 测试环境

#### 方法一：脚本

在项目根目录执行：

```bash
# 进入脚本目录
cd ./deploy-contract

# 安装部署脚本所需依赖
npm install web3 solc
```

### 3️⃣ 配置环境变量 (核心步骤!)

**创建 .env 配置文件**
项目现在使用 `.env` 文件来统一管理所有配置，避免在多个文件中重复配置私钥。


**获取 Ganache 私钥并配置**:

1.  打开 Ganache GUI。
2.  点击任意账户右侧的 🔑 图标。
3.  复制显示的私钥（格式：`0x123abc...`）。
4.  编辑 `.env` 文件，设置 `BLOCKCHAIN_PRIVATE_KEY`：

```bash
# .env 文件内容示例
BLOCKCHAIN_PRIVATE_KEY=0xd9ebef6c1d46bc5cb5c9f95d778fbc303f340319a1e470f447775563442b436c
BLOCKCHAIN_RPC_URL=http://127.0.0.1:7545
BLOCKCHAIN_CONTRACT_ADDRESS=0xeeF94Af98249d7aD4169af0b0Bd5297e7929bC3F
BLOCKCHAIN_GAS_PRICE=20000000000
BLOCKCHAIN_GAS_LIMIT=6721975
SERVER_PORT=8088
```


**运行部署脚本**

```bash
# 直接运行
node deploy-contract.js
```

**🎉 部署成功！**
您将看到类似如下输出：

```
🚀 开始部署智能合约...
==================================================
🌐 连接到区块链网络: http://127.0.0.1:7545
✅ 网络连接成功，网络ID: 5777
👤 使用账户: 0x742d35Cc6634C0532925a3b8D404d3aABe5475cc
💰 账户余额: 99.99 ETH
📝 开始编译智能合约...
✅ 合约编译成功
🚀 开始部署合约...
⛽ 预估Gas费用: 572258
==================================================
🎉 合约部署成功！
📍 合约地址: 0x3bD5D66147a59F8F4f5832dAbcf64aaD05424a26
🔗 交易哈希: 0xabcdef1234567890abcdef1234567890abcdef12
⛽ 实际Gas使用: 572258
==================================================
💾 部署信息已保存到 contract-deployment.json
内容：
{
  "contractAddress": "0x1234567890123456789012345678901234567890",
  "transactionHash": "0xabcdef1234567890abcdef1234567890abcdef12",
  "gasUsed": 1234567,
  "deployTime": "2023-09-14T10:00:00.000Z",
  "networkId": "5777",
  "deployer": "0x742d35Cc6634C0532925a3b8D404d3aABe5475cc"
}
```

> ✅ **关键信息**: **请务必复制 `合约地址` (`0x3bD5D66147a59F8F4f5832dAbcf64aaD05424a26`) 和 `私钥`**。

#### 方法二：Remix IDE

##### 打开 Remix IDE

访问：https://remix.ethereum.org/

##### 创建合约文件

1. 点击左侧文件管理器的 "+" 按钮
2. 创建新文件：`Traceability.sol`
3. 复制 `contracts/Traceability.sol` 的完整内容到新文件

##### 编译合约

1. 点击左侧的 "Solidity Compiler" 图标
2. 选择编译器版本：`0.8.0+`
3. 点击 "Compile Traceability.sol" 按钮
4. 确认编译成功（绿色勾选）

##### 连接 Ganache

1. 点击左侧的 "Deploy & Run Transactions" 图标
2. Environment 选择：`Web3 Provider`
3. 在弹出框中输入：`http://127.0.0.1:7545`
4. 点击 "OK" 确认连接

##### 部署合约

1. 确认 Contract 选择为：`Traceability`
2. 点击 "Deploy" 按钮
3. 在 Ganache 中确认交易
4. 复制部署后的合约地址

### 4️⃣ 更新合约地址

部署成功后，需要更新 `.env` 文件中的合约地址：

```bash
# 编辑 .env 文件，更新合约地址
BLOCKCHAIN_CONTRACT_ADDRESS=0x你的新合约地址
```

> 💡 **提示**: 现在所有配置都通过 `.env` 文件管理，无需再手动编辑 `application.yml` 文件。

### 5️⃣ 启动 SpringBoot 应用

**方式一：使用启动脚本（推荐）**

```bash
# Windows
start.bat
```

启动脚本会自动：

- 检查 `.env` 文件是否存在
- 验证必要的环境变量
- 加载环境变量并启动应用

**方式二：手动启动**

```bash
# 清理并编译
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 6️⃣ 测试 API 接口

应用启动后，即可通过以下命令测试：

**检查连接状态** (最常用)

```bash
curl "http://localhost:8088/blockchain/status"
# 响应示例: {"connected":true,"success":true,"message":"区块链连接正常"}
```

**添加一条溯源记录**

```bash
curl -X POST "http://localhost:8088/blockchain/add?data=A生产信息"
```

**查询特定记录**

```bash
curl "http://localhost:8088/blockchain/get/1"
```

**获取记录总数**

```bash
curl "http://localhost:8088/blockchain/count"
```

## ⚠️ 故障排除 (常见问题)

| 问题                                                         | 原因                                               | 解决方案                                                                                                |
| :----------------------------------------------------------- | :------------------------------------------------- | :------------------------------------------------------------------------------------------------------ |
| **ENS 解析错误** (`Unable to determine sync status of node`) | Web3j 尝试解析 Ganache 不支持的 ENS 名称。         | ✅ **已解决**：项目已降级 Web3j 至 4.8.7 并实现延迟加载机制。应用仍可正常启动。                         |
| **"请先配置私钥"**                                           | `deploy-contract.js` 中的 `PRIVATE_KEY` 未被修改。 | 按照第 3 步，正确填写 Ganache 私钥。                                                                    |
| **"无法连接到区块链网络"**                                   | Ganache 未运行，或端口不匹配。                     | 启动 Ganache，确认 `RPC_URL` 为 `http://127.0.0.1:7545`。使用 `curl http://127.0.0.1:7545` 测试连通性。 |
| **"账户余额不足"**                                           | 选择的账户没有足够的测试 ETH。                     | 在 Ganache 中选择一个余额充足的账户，或重启 Ganache 获取新账户。                                        |
| **"合约编译失败"**                                           | `contracts/Traceability.sol` 文件损坏或语法错误。  | 检查该文件是否存在且内容完整。也可尝试使用 Remix IDE 作为备选方案。                                     |
| **"Out of gas"**                                             | Gas 限制设置过低。                                 | 在 `deploy-contract.js` 中增加 `GAS_LIMIT` 值，如 `8000000`。                                           |
| **中文乱码 (PowerShell)**                                    | PowerShell 默认编码问题。                          | 使用英文版本的错误信息，或切换至 CMD/WSL。                                                              |

## 总结

此为**演示项目**，仅供学习和研究之用。

**如有任何问题，欢迎联系作者：jingshuihuayue@qq.com**
