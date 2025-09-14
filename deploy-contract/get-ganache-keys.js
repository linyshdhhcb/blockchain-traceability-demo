const { Web3 } = require("web3");

// 连接到Ganache
const web3 = new Web3("http://127.0.0.1:7545");

async function getGanacheInfo() {
    try {
        console.log('🔍 获取Ganache账户信息...\n');

        // 获取所有账户
        const accounts = await web3.eth.getAccounts();
        console.log(`👥 可用账户数量: ${accounts.length}\n`);

        // 显示每个账户的信息
        for (let i = 0; i < Math.min(accounts.length, 3); i++) {
            const account = accounts[i];
            const balance = await web3.eth.getBalance(account);
            console.log(`账户${i}:`);
            console.log(`  地址: ${account}`);
            console.log(`  余额: ${web3.utils.fromWei(balance, 'ether')} ETH`);
            console.log('');
        }

        // 测试当前配置的私钥
        const currentPrivateKey = '0xd9ebef6c1d46bc5cb5c9f95d778fbc303f340319a1e470f447775563442b436c';
        console.log('🔑 测试当前配置的私钥...');
        try {
            const account = web3.eth.accounts.privateKeyToAccount(currentPrivateKey);
            console.log(`私钥对应地址: ${account.address}`);
            
            const isInGanache = accounts.some(acc => acc.toLowerCase() === account.address.toLowerCase());
            console.log(`是否在Ganache中: ${isInGanache}`);
            
            if (!isInGanache) {
                console.log('❌ 当前私钥不对应Ganache中的任何账户！');
                console.log('💡 建议使用Ganache界面查看正确的私钥，或者重新启动Ganache');
            }
        } catch (error) {
            console.log(`❌ 私钥格式错误: ${error.message}`);
        }

        // 提供一些常见的Ganache默认私钥（仅用于开发）
        console.log('\n📋 常见的Ganache默认私钥（仅供参考）:');
        const commonKeys = [
            '0x4f3edf983ac636a65a842ce7c78d9aa706d3b113bce9c46f30d7d21715b23b1d',
            '0x6cbed15c793ce57650b9877cf6fa156fbef513c4e6134f022a85b1ffdd59b2a1',
            '0x6370fd033278c143179d81c5526140625662b8daa446c22ee2d73db3707e620c'
        ];
        
        for (let i = 0; i < commonKeys.length; i++) {
            try {
                const account = web3.eth.accounts.privateKeyToAccount(commonKeys[i]);
                const isInGanache = accounts.some(acc => acc.toLowerCase() === account.address.toLowerCase());
                console.log(`  私钥${i + 1}: ${commonKeys[i]}`);
                console.log(`    对应地址: ${account.address}`);
                console.log(`    在当前Ganache中: ${isInGanache}`);
                console.log('');
            } catch (error) {
                console.log(`  私钥${i + 1}: 格式错误`);
            }
        }

    } catch (error) {
        console.error('❌ 获取信息失败:', error.message);
    }
}

// 运行
getGanacheInfo().then(() => {
    console.log('🎉 完成');
    process.exit(0);
}).catch(error => {
    console.error('💥 失败:', error);
    process.exit(1);
});
