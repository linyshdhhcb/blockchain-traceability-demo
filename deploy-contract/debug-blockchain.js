const { Web3 } = require("web3");

// 连接到Ganache
const web3 = new Web3("http://127.0.0.1:7545");

async function debugBlockchain() {
  try {
    console.log("🔍 调试区块链连接...\n");

    // 1. 检查网络连接
    const networkId = await web3.eth.net.getId();
    console.log(`📡 网络ID: ${networkId}`);

    // 2. 获取所有账户
    const accounts = await web3.eth.getAccounts();
    console.log(`👥 可用账户数量: ${accounts.length}`);
    accounts.forEach((account, index) => {
      console.log(`   账户${index}: ${account}`);
    });

    // 3. 检查私钥对应的账户
    const privateKey =
      "0xd9ebef6c1d46bc5cb5c9f95d778fbc303f340319a1e470f447775563442b436c";
    try {
      const account = web3.eth.accounts.privateKeyToAccount(privateKey);
      console.log(`\n🔑 私钥对应账户: ${account.address}`);

      // 检查账户是否在Ganache中
      const isInGanache =
        accounts.includes(account.address.toLowerCase()) ||
        accounts.some(
          (acc) => acc.toLowerCase() === account.address.toLowerCase()
        );
      console.log(`✅ 账户在Ganache中: ${isInGanache}`);

      if (isInGanache) {
        const balance = await web3.eth.getBalance(account.address);
        console.log(`💰 账户余额: ${web3.utils.fromWei(balance, "ether")} ETH`);
      }
    } catch (error) {
      console.log(`❌ 私钥格式错误: ${error.message}`);
    }

    // 4. 检查合约地址
    const contractAddress = "0xeeF94Af98249d7aD4169af0b0Bd5297e7929bC3F";
    console.log(`\n📋 检查合约地址: ${contractAddress}`);

    const code = await web3.eth.getCode(contractAddress);
    console.log(`📝 合约代码长度: ${code.length} 字符`);
    console.log(`✅ 合约已部署: ${code !== "0x"}`);

    if (code !== "0x") {
      // 5. 尝试调用合约的recordCount方法
      console.log("\n🔧 尝试调用合约方法...");

      // recordCount() 方法的ABI编码
      const methodSignature =
        web3.eth.abi.encodeFunctionSignature("recordCount()");
      console.log(`方法签名: ${methodSignature}`);

      try {
        const result = await web3.eth.call({
          to: contractAddress,
          data: methodSignature,
        });
        console.log(`📊 recordCount 调用结果: ${result}`);

        if (result && result !== "0x") {
          const count = web3.utils.hexToNumber(result);
          console.log(`📈 记录总数: ${count}`);
        } else {
          console.log("⚠️  调用返回空结果");
        }
      } catch (error) {
        console.log(`❌ 调用失败: ${error.message}`);
      }
    }
  } catch (error) {
    console.error("❌ 调试过程中出错:", error.message);
  }
}

// 运行调试
debugBlockchain()
  .then(() => {
    console.log("\n🎉 调试完成");
    process.exit(0);
  })
  .catch((error) => {
    console.error("💥 调试失败:", error);
    process.exit(1);
  });
