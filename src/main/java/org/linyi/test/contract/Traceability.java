package org.linyi.test.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Traceability Contract Wrapper
 * Generated with web3j version 4.9.8.
 */
@SuppressWarnings("rawtypes")
public class Traceability extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50610a8d806100206000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c80631e7663bc1461005c5780632f7a188114610078578063975057e7146100945780639b0b3b6f146100b2578063b8c9d365146100d0575b600080fd5b610076600480360381019061007191906106b4565b6100ee565b005b610092600480360381019061008d91906106b4565b610197565b005b61009c610240565b6040516100a99190610700565b60405180910390f35b6100ba610246565b6040516100c79190610700565b60405180910390f35b6100d861024c565b6040516100e59190610700565b60405180910390f35b6001600081905550600160008154809291906101099061071b565b91905055506040518060800160405280600154815260200183815260200142815260200133815250600080600154815260200190815260200160002060008201518160000155602082015181600101908051906020019061016b929190610252565b5060408201518160020155606082015181600301556090505033600154834260405161019993929190610763565b60405180910390a15050565b6000600154821180156101b85750600082115b6101f7576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101ee906107e6565b60405180910390fd5b6000808381526020019081526020016000206040518060800160405290816000820154815260200160018201805461022e90610835565b80601f016020809104026020016040519081016040528092919081815260200182805461025a90610835565b80156102a75780601f1061027c576101008083540402835291602001916102a7565b820191906000526020600020905b81548152906001019060200180831161028a57829003601f168201915b505050505081526020016002820154815260200160038201548152505090508060000151816020015182604001518360600151935093509350935050919050565b60015481565b60015481565b60015481565b82805461025e90610835565b90600052602060002090601f01602090048101928261028057600085556102c7565b82601f1061029957805160ff19168380011785556102c7565b828001600101855582156102c7579182015b828111156102c65782518255916020019190600101906102ab565b5b5090506102d491906102d8565b5090565b5b808211156102f15760008160009055506001016102d9565b5090565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050";

    public static final String FUNC_ADDRECORD = "addRecord";
    public static final String FUNC_GETRECORD = "getRecord";
    public static final String FUNC_RECORDCOUNT = "recordCount";

    public static final Event RECORDADDED_EVENT = new Event("RecordAdded",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>(true) {}));

    @Deprecated
    protected Traceability(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Traceability(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Traceability(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Traceability(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> addRecord(String _data) {
        final Function function = new Function(
                FUNC_ADDRECORD,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_data)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple4<BigInteger, String, BigInteger, String>> getRecord(BigInteger _id) {
        final Function function = new Function(FUNC_GETRECORD,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        return new RemoteFunctionCall<Tuple4<BigInteger, String, BigInteger, String>>(function,
                new Callable<Tuple4<BigInteger, String, BigInteger, String>>() {
                    @Override
                    public Tuple4<BigInteger, String, BigInteger, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, String, BigInteger, String>(
                                (BigInteger) results.get(0).getValue(),
                                (String) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (String) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> recordCount() {
        final Function function = new Function(FUNC_RECORDCOUNT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static Traceability load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Traceability(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Traceability load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Traceability(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Traceability load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Traceability(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Traceability load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Traceability(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}