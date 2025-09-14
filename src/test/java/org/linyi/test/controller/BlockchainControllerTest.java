package org.linyi.test.controller;

import org.junit.jupiter.api.Test;
import org.linyi.test.service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 区块链控制器测试类
 */
@WebMvcTest(BlockchainController.class)
class BlockchainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlockchainService blockchainService;

    @Test
    void testAddRecord() throws Exception {
        // Mock服务层返回
        when(blockchainService.addRecord(anyString()))
                .thenReturn("0x1234567890abcdef1234567890abcdef12345678");

        mockMvc.perform(post("/blockchain/add")
                        .param("data", "测试溯源数据"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("溯源记录添加成功"))
                .andExpect(jsonPath("$.transactionHash").exists())
                .andExpect(jsonPath("$.data").value("测试溯源数据"));
    }

    @Test
    void testGetRecord() throws Exception {
        // Mock服务层返回
        when(blockchainService.getRecord(any(BigInteger.class)))
                .thenReturn("记录ID: 1, 数据: 测试数据, 时间戳: 1694649600, 创建者: 0x123");

        mockMvc.perform(get("/blockchain/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.record").exists())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetRecordCount() throws Exception {
        // Mock服务层返回
        when(blockchainService.getRecordCount())
                .thenReturn(BigInteger.valueOf(5));

        mockMvc.perform(get("/blockchain/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    void testGetStatus() throws Exception {
        // Mock服务层返回
        when(blockchainService.isConnected()).thenReturn(true);

        mockMvc.perform(get("/blockchain/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.connected").value(true))
                .andExpect(jsonPath("$.message").value("区块链连接正常"));
    }

    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/blockchain/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("blockchain-traceability"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testAddRecordWithException() throws Exception {
        // Mock服务层抛出异常
        when(blockchainService.addRecord(anyString()))
                .thenThrow(new RuntimeException("区块链连接失败"));

        mockMvc.perform(post("/blockchain/add")
                        .param("data", "测试数据"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("添加溯源记录失败: 区块链连接失败"));
    }
}