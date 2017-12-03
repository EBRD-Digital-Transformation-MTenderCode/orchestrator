package com.procurement.orchestrator.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends CassandraRepository<OperationEntity, String> {
    @Query(value = "select * from orchestrator_operation where transaction_id=?0 LIMIT 1")
    OperationEntity getByTransactionId(String transactionId);
}
