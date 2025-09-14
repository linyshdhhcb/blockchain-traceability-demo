package org.linyi.test.service;

import lombok.extern.slf4j.Slf4j;
import org.linyi.test.contract.Traceability;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 区块链服务类
 * 封装与智能合约的交互逻辑
 */
@Slf4j
@Service
public class BlockchainService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final String contractAddress;
    private final StaticGasProvider gasProvider;
    private final RawTransactionManager transactionManager;

    public BlockchainService(
            @Value("${blockchain.rpc-url}") String rpcUrl,
            @Value("${blockchain.private-key}") String privateKey,
            @Value("${blockchain.contract-address}") String contractAddress) {

        log.info("初始化区块链服务 - RPC URL: {}, 合约地址: {}", rpcUrl, contractAddress);

        // 连接到区块链网络
        HttpService httpService = new HttpService(rpcUrl);
        this.web3j = Web3j.build(httpService);

        // 创建凭证
        this.credentials = Credentials.create(privateKey);
        log.info("使用账户地址: {}", credentials.getAddress());

        // 保存合约地址
        this.contractAddress = contractAddress.startsWith("0x") ? contractAddress : "0x" + contractAddress;

        // 设置Gas参数
        this.gasProvider = new StaticGasProvider(
                BigInteger.valueOf(20000000000L), // gasPrice: 20 Gwei
                BigInteger.valueOf(6721975)       // gasLimit
        );

        // 创建交易管理器
        this.transactionManager = new RawTransactionManager(web3j, credentials);

        log.info("区块链服务初始化完成，使用直接ABI调用方式");
    }

    /**
     * 调用合约的只读方法
     */
    private List<Type> callContractFunction(String functionName, List<Type> inputParameters, List<TypeReference<?>> outputParameters) throws Exception {
        Function function = new Function(functionName, inputParameters, outputParameters);
        String encodedFunction = FunctionEncoder.encode(function);

        log.debug("调用合约方法: {}, 编码数据: {}", functionName, encodedFunction);

        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(credentials.getAddress(), contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send();

        log.debug("合约调用响应: {}", response.getValue());

        if (response.hasError()) {
            throw new RuntimeException("合约调用失败: " + response.getError().getMessage());
        }

        List<Type> result = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        log.debug("解码结果数量: {}", result.size());

        return result;
    }

    /**
     * 发送交易到合约
     */
    private String sendContractTransaction(String functionName, List<Type> inputParameters) throws Exception {
        Function function = new Function(functionName, inputParameters, Collections.emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        EthSendTransaction response = transactionManager.sendTransaction(
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit(),
                contractAddress,
                encodedFunction,
                BigInteger.ZERO
        );

        if (response.hasError()) {
            throw new RuntimeException("交易发送失败: " + response.getError().getMessage());
        }

        return response.getTransactionHash();
    }

    /**
     * 添加溯源记录
     * @param data 溯源数据
     * @return 交易哈希
     */
    public String addRecord(String data) throws Exception {
        log.info("添加溯源记录: {}", data);

        List<Type> inputParameters = Arrays.asList(new Utf8String(data));
        String transactionHash = sendContractTransaction("addRecord", inputParameters);

        log.info("溯源记录添加成功，交易哈希: {}", transactionHash);
        return transactionHash;
    }

    /**
     * 获取溯源记录
     * @param id 记录ID
     * @return 格式化的记录信息
     */
    public String getRecord(BigInteger id) throws Exception {
        log.info("查询溯源记录，ID: {}", id);

        List<Type> inputParameters = Arrays.asList(new Uint256(id));
        List<TypeReference<?>> outputParameters = Arrays.asList(
                new TypeReference<Uint256>() {},
                new TypeReference<Utf8String>() {},
                new TypeReference<Uint256>() {},
                new TypeReference<Address>() {}
        );

        List<Type> result = callContractFunction("getRecord", inputParameters, outputParameters);

        if (result.isEmpty() || result.size() < 4) {
            log.warn("合约调用返回结果不完整，结果数量: {}", result.size());
            return "记录不存在或查询失败";
        }

        String formattedResult = String.format(
                "记录ID: %s, 数据: %s, 时间戳: %s, 创建者: %s",
                result.get(0).getValue(),
                result.get(1).getValue(),
                result.get(2).getValue(),
                result.get(3).getValue()
        );

        log.info("查询结果: {}", formattedResult);
        return formattedResult;
    }

    /**
     * 获取记录总数
     * @return 记录总数
     */
    public BigInteger getRecordCount() throws Exception {
        List<Type> inputParameters = Collections.emptyList();
        List<TypeReference<?>> outputParameters = Arrays.asList(new TypeReference<Uint256>() {});

        List<Type> result = callContractFunction("recordCount", inputParameters, outputParameters);

        if (result.isEmpty()) {
            log.warn("合约调用返回空结果，可能是合约方法不存在或调用失败");
            return BigInteger.ZERO;
        }

        BigInteger count = (BigInteger) result.get(0).getValue();
        log.info("当前记录总数: {}", count);
        return count;
    }

    /**
     * 检查区块链连接状态
     * @return 连接状态
     */
    public boolean isConnected() {
        try {
            web3j.web3ClientVersion().send();
            return true;
        } catch (Exception e) {
            log.error("区块链连接检查失败", e);
            return false;
        }
    }
}