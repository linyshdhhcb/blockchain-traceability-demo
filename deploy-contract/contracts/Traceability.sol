// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract Traceability {
    struct Record {
        uint id;
        string data;
        uint timestamp;
        address creator;
    }

    mapping(uint => Record) public records;
    uint public recordCount;

    event RecordAdded(uint indexed id, string data, uint timestamp, address indexed creator);

    function addRecord(string memory _data) public {
        recordCount++;
        records[recordCount] = Record(recordCount, _data, block.timestamp, msg.sender);
        emit RecordAdded(recordCount, _data, block.timestamp, msg.sender);
    }

    function getRecord(uint _id) public view returns (uint, string memory, uint, address) {
        require(_id > 0 && _id <= recordCount, "Record does not exist");
        Record memory r = records[_id];
        return (r.id, r.data, r.timestamp, r.creator);
    }

    function getAllRecords() public view returns (Record[] memory) {
        Record[] memory allRecords = new Record[](recordCount);
        for (uint i = 1; i <= recordCount; i++) {
            allRecords[i-1] = records[i];
        }
        return allRecords;
    }
}