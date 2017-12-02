package com.procurement.orchestrator.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends TypedIdCassandraRepository<OperationEntity, String> {
}
