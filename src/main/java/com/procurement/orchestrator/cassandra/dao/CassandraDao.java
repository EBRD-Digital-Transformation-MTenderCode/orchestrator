package com.procurement.orchestrator.cassandra.dao;

import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CassandraDao {

    void saveRequest(RequestEntity entity);

    Optional<RequestEntity> getOneByTxId(String txId);

    void saveOperation(OperationEntity entity);

    Optional<OperationEntity> getLastOperation(String txId);


}
