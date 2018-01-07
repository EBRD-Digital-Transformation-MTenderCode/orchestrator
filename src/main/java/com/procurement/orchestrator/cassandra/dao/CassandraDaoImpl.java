package com.procurement.orchestrator.cassandra.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.exception.OperationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

@Service
public class CassandraDaoImpl implements CassandraDao {

    private final Session session;

    public CassandraDaoImpl(final Session session) {
        this.session = session;
    }

    @Override
    public void saveRequest(final RequestEntity entity) {

        final Insert insert = insertInto("orchestrator_request");
        insert
                .value("tx_id", entity.getTxId())
                .value("request_date", entity.getRequestDate())
                .value("json_data", entity.getJsonData())
                .value("json_params", entity.getJsonParams());
        session.execute(insert);

    }

    @Override
    public Optional<RequestEntity> getOneByTxId(String txId) {
        final Statement query = select()
                .all()
                .from("orchestrator_request")
                .where(eq("tx_id", txId))
                .limit(1);

        final ResultSet rows = session.execute(query);

        return Optional.ofNullable(rows.one())
                .map(row -> {
                    final String id = row.getString("tx_id");
                    final Date requestDate = row.getTimestamp("request_date");
                    final String jsonData = row.getString("json_data");
                    final String jsonParams = row.getString("json_params");
                    return new RequestEntity(
                            id,
                            requestDate,
                            jsonData,
                            jsonParams);
                });
    }


    @Override
    public void saveOperation(final OperationEntity entity) {
        final Insert insert = insertInto("orchestrator_operation");
        insert
                .value("tx_id", entity.getTxId())
                .value("operation_date", entity.getDate())
                .value("process_id", entity.getProcessId())
                .value("task_id", entity.getTaskId())
                .value("json_data", entity.getJsonData())
                .value("json_params", entity.getJsonParams())
                .value("request_date", entity.getRequestDate());
        session.execute(insert).wasApplied();
    }

    @Override
    public Optional<OperationEntity> getLastOperation(final String txId) {
        final Statement query = select()
                .all()
                .from("orchestrator_operation")
                .where(eq("tx_id", txId))
                .limit(1);

        final ResultSet rows = session.execute(query);

        return Optional.ofNullable(rows.one())
                .map(row -> {
                    final String id = row.getString("tx_id");
                    final Date operationDate = row.getTimestamp("operation_date");
                    final String processId = row.getString("process_id");
                    final String taskId = row.getString("task_id");
                    final String jsonData = row.getString("json_data");
                    final String jsonParams = row.getString("json_params");
                    final Date requestDate = row.getTimestamp("request_date");
                       return new OperationEntity(
                               id,
                               operationDate,
                               processId,
                               taskId,
                               jsonData,
                               jsonParams,
                               requestDate);
                });
    }
}
