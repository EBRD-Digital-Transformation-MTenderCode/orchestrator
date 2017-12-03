package com.procurement.orchestrator.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationRepository extends CassandraRepository<OperationEntity, String> {
    @Query(value = "select * from orchestrator_operation where transaction_id=?0 LIMIT 1")
    Optional<OperationEntity> getOneById(String transactionId);

    @Query(value = "select * from orchestrator_operation where transaction_id=?0 AND operation_step=?1 LIMIT 1")
    Optional<OperationEntity> getOneByStep(String transactionId, Integer step);
}
