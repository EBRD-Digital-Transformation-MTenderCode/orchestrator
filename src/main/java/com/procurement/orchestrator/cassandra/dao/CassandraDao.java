package com.procurement.orchestrator.cassandra.dao;

import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface CassandraDao {

    void saveRequest(RequestEntity entity);

    Optional<RequestEntity> getOneByTxId(String txId);

    void saveOperation(OperationEntity entity);

    Optional<OperationEntity> getLastOperation(String txId);


}
