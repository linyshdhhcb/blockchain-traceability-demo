package org.linyi.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * 应用启动测试
 */
@SpringBootTest
@TestPropertySource(properties = {
        "blockchain.rpc-url=http://localhost:7545",
        "blockchain.contract-address=0x0000000000000000000000000000000000000000",
        "blockchain.private-key=0x0000000000000000000000000000000000000000000000000000000000000000"
})
class BlockchainDemoApplicationTest {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
        // 由于没有真实的区块链环境，这里只测试应用能否启动
    }
}