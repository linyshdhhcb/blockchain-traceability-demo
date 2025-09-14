/**
 * 智能合约部署脚本 (Node.js + Web3.js)
 *
 * 使用方法:
 * 1. 安装依赖: npm install web3 solc
 * 2. 配置私钥: 编辑下面的 PRIVATE_KEY
 * 3. 启动Ganache: 确保运行在 http://127.0.0.1:7545
 * 4. 运行脚本: node deploy-contract.js
 *
 * 注意: 这是一个完整的部署脚本，包含合约编译和部署功能
 */

const { Web3 } = require("web3");
const fs = require("fs");
const path = require("path");
const solc = require("solc");

// ==================== 配置区域 ====================
const RPC_URL = "http://127.0.0.1:7545"; // Ganache RPC地址
const PRIVATE_KEY =
  "0xd9ebef6c1d46bc5cb5c9f95d778fbc303f340319a1e470f447775563442b436c"; // 🔴 替换为你的Ganache私钥
const GAS_PRICE = "20000000000"; // 20 Gwei
const GAS_LIMIT = "6721975"; // Gas限制

// ==================== 合约编译函数 ====================
function compileContract() {
  console.log("📝 开始编译智能合约...");

  // 读取合约源码
  const contractPath = path.join(__dirname, "contracts", "Traceability.sol");
  if (!fs.existsSync(contractPath)) {
    throw new Error(`合约文件不存在: ${contractPath}`);
  }

  const contractSource = fs.readFileSync(contractPath, "utf8");
  console.log("✅ 已读取合约源码");

  // 编译配置
  const input = {
    language: "Solidity",
    sources: {
      "Traceability.sol": {
        content: contractSource,
      },
    },
    settings: {
      optimizer: {
        enabled: true,
        runs: 200,
      },
      evmVersion: "london", // 使用兼容的EVM版本
      outputSelection: {
        "*": {
          "*": ["abi", "evm.bytecode"],
        },
      },
    },
  };

  // 编译合约
  const output = JSON.parse(solc.compile(JSON.stringify(input)));

  // 检查编译错误
  if (output.errors) {
    output.errors.forEach((error) => {
      if (error.severity === "error") {
        throw new Error(`编译错误: ${error.message}`);
      } else {
        console.warn(`编译警告: ${error.message}`);
      }
    });
  }

  const contract = output.contracts["Traceability.sol"]["Traceability"];
  if (!contract) {
    throw new Error("编译失败: 找不到合约");
  }

  console.log("✅ 合约编译成功");
  return {
    abi: contract.abi,
    bytecode: contract.evm.bytecode.object,
  };
}

// ==================== 主部署函数 ====================
async function deployContract() {
  try {
    console.log("🚀 开始部署智能合约...");
    console.log("=".repeat(50));

    // 1. 检查私钥配置
    if (PRIVATE_KEY === "0x你的私钥") {
      console.error("❌ 请先配置私钥！");
      console.log("📝 配置步骤：");
      console.log("   1. 打开 Ganache");
      console.log("   2. 点击账户旁边的钥匙图标");
      console.log("   3. 复制私钥");
      console.log("   4. 替换脚本中的 PRIVATE_KEY 变量");
      return;
    }

    // 2. 连接到区块链
    const web3 = new Web3(RPC_URL);
    console.log("🌐 连接到区块链网络:", RPC_URL);

    // 测试连接
    try {
      const networkId = await web3.eth.net.getId();
      console.log("✅ 网络连接成功，网络ID:", networkId);
    } catch (error) {
      console.error("❌ 无法连接到区块链网络，请检查Ganache是否运行");
      return;
    }

    // 3. 创建账户
    const account = web3.eth.accounts.privateKeyToAccount(PRIVATE_KEY);
    web3.eth.accounts.wallet.add(account);
    console.log("👤 使用账户:", account.address);

    // 4. 检查账户余额
    const balance = await web3.eth.getBalance(account.address);
    const balanceEth = web3.utils.fromWei(balance, "ether");
    console.log("💰 账户余额:", balanceEth, "ETH");

    if (parseFloat(balanceEth) < 0.1) {
      console.error("❌ 账户余额不足，请确保账户有足够的ETH");
      return;
    }

    // 5. 编译合约
    let compiledContract;
    try {
      compiledContract = compileContract();
    } catch (error) {
      console.error("❌ 合约编译失败:", error.message);
      console.log("📝 建议使用 Remix IDE 部署合约：");
      console.log("   1. 打开 https://remix.ethereum.org/");
      console.log("   2. 创建新文件 Traceability.sol");
      console.log("   3. 复制 contracts/Traceability.sol 的内容");
      console.log("   4. 编译并部署合约");
      console.log("   5. 复制合约地址到 application.yml");
      return;
    }

    // 6. 创建合约实例
    const contract = new web3.eth.Contract(compiledContract.abi);
    console.log("📄 合约实例创建成功");

    // 7. 部署合约
    console.log("🚀 开始部署合约...");
    const deployTx = contract.deploy({
      data: "0x" + compiledContract.bytecode,
    });

    // 估算Gas费用
    const gasEstimate = await deployTx.estimateGas({ from: account.address });
    console.log("⛽ 预估Gas费用:", gasEstimate.toString());

    // 发送部署交易
    const deployedContract = await deployTx.send({
      from: account.address,
      gas: Math.min(parseInt(GAS_LIMIT), Number(gasEstimate) * 2), // 使用估算值的2倍作为安全边际
      gasPrice: GAS_PRICE,
    });

    console.log("=".repeat(50));
    console.log("🎉 合约部署成功！");
    console.log("📍 合约地址:", deployedContract.options.address);
    console.log("🔗 交易哈希:", deployedContract.transactionHash);
    console.log("⛽ 实际Gas使用:", deployedContract.gasUsed);
    console.log("=".repeat(50));

    // 8. 保存部署信息
    const networkId = await web3.eth.net.getId();
    const deployInfo = {
      contractAddress: deployedContract.options.address,
      transactionHash: deployedContract.transactionHash || "N/A",
      gasUsed: deployedContract.gasUsed
        ? deployedContract.gasUsed.toString()
        : "N/A",
      deployTime: new Date().toISOString(),
      networkId: networkId.toString(),
      deployer: account.address,
    };

    fs.writeFileSync(
      "contract-deployment.json",
      JSON.stringify(deployInfo, null, 2)
    );
    console.log("💾 部署信息已保存到 contract-deployment.json");

    // 9. 更新配置提示
    console.log("\n📝 下一步操作：");
    console.log("1. 复制合约地址:", deployedContract.options.address);
    console.log(
      "2. 更新 src/main/resources/application.yml 中的 contract-address"
    );
    console.log("3. 启动 SpringBoot 应用: mvn spring-boot:run");
    console.log("4. 测试 API 接口");

    return deployedContract.options.address;
  } catch (error) {
    console.error("❌ 部署失败:", error.message);
    console.error("详细错误:", error);
  }
}

// 如果直接运行此脚本
if (require.main === module) {
  deployContract();
}

module.exports = { deployContract };
