package org.linyi.test.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.linyi.test.service.BlockchainService;
import org.linyi.test.service.BlockchainService.AddRecordResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * 区块链溯源控制器
 * 提供HTTP接口用于添加和查询溯源记录
 */
@Slf4j
@RestController
@RequestMapping("/blockchain")
@RequiredArgsConstructor
public class BlockchainController {

    private final BlockchainService blockchainService;

    /**
     * 添加溯源记录
     * @param data 溯源数据
     * @return 交易哈希、记录ID和状态信息
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addRecord(@RequestParam String data) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("接收到添加溯源记录请求: {}", data);

            AddRecordResult result = blockchainService.addRecord(data);

            response.put("success", true);
            response.put("message", "溯源记录添加成功");
            response.put("transactionHash", result.getTransactionHash());
            response.put("recordId", result.getRecordId());
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("添加溯源记录失败", e);

            response.put("success", false);
            response.put("message", "添加溯源记录失败: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 查询溯源记录
     * @param id 记录ID
     * @return 记录详细信息
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Map<String, Object>> getRecord(@PathVariable BigInteger id) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("接收到查询溯源记录请求，ID: {}", id);

            String record = blockchainService.getRecord(id);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("record", record);
            response.put("id", id);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("查询溯源记录失败，ID: {}", id, e);

            response.put("success", false);
            response.put("message", "查询溯源记录失败: " + e.getMessage());
            response.put("id", id);

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 获取记录总数
     * @return 记录总数
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getRecordCount() {
        Map<String, Object> response = new HashMap<>();

        try {
            BigInteger count = blockchainService.getRecordCount();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("count", count);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("查询记录总数失败", e);

            response.put("success", false);
            response.put("message", "查询记录总数失败: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 检查区块链连接状态
     * @return 连接状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();

        boolean connected = blockchainService.isConnected();

        response.put("success", true);
        response.put("connected", connected);
        response.put("message", connected ? "区块链连接正常" : "区块链连接异常");

        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查接口
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "UP");
        response.put("service", "blockchain-traceability");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}