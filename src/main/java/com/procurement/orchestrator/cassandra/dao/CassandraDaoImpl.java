package com.procurement.orchestrator.cassandra.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import java.util.Optional;
import org.springframework.stereotype.Service;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

@Service
public class CassandraDaoImpl implements CassandraDao {

    private static final String REQUEST_TABLE = "orchestrator_request";
    private static final String OPERATION_TABLE = "orchestrator_operation";
    private static final String OPERATION_STEP_TABLE = "orchestrator_operation_step";

    private final Session session;

    public CassandraDaoImpl(final Session session) {
        this.session = session;
    }

    @Override
    public void saveRequest(final RequestEntity entity) {

        final Insert insert = insertInto(REQUEST_TABLE);
        insert
                .value("request_id", entity.getRequestId())
                .value("request_date", entity.getRequestDate())
                .value("operation_id", entity.getOperationId())
                .value("json_data", entity.getJsonData())
                .value("json_params", entity.getJsonParams());
        session.execute(insert);

    }

    @Override
    public Optional<RequestEntity> getRequestById(String id) {
        final Statement query = select()
                .all()
                .from(REQUEST_TABLE)
                .where(eq("request_id", id))
                .limit(1);

        final ResultSet rows = session.execute(query);

        return Optional.ofNullable(rows.one())
                .map(row -> new RequestEntity(
                        row.getString("request_id"),
                        row.getTimestamp("request_date"),
                        row.getString("operation_id"),
                        row.getString("json_data"),
                        row.getString("json_params")));
    }

    @Override
    public Boolean saveOperationIfNotExist(OperationEntity entity) {
        final Insert insert = insertInto(OPERATION_TABLE).ifNotExists();
        insert
                .value("operation_id", entity.getOperationId())
                .value("process_id", entity.getProcessId());
        final ResultSet resultSet = session.execute(insert);
        if (!resultSet.wasApplied()) {
            return resultSet.one().getString("process_id").equals(entity.getProcessId());
        }
        return true;
    }

    public Boolean isOperationExist(String operationId) {
        final Statement query = select()
                .from(OPERATION_TABLE)
                .where(eq("operation_id", operationId));
        final ResultSet rs = session.execute(query);
        return (rs.all().size() > 0) ? true : false;
    }

    @Override
    public void saveOperationStep(final OperationStepEntity entity) {
        final Insert insert = insertInto(OPERATION_STEP_TABLE);
        insert
                .value("process_id", entity.getProcessId())
                .value("task_id", entity.getTaskId())
                .value("step_date", entity.getDate())
                .value("json_data", entity.getJsonData())
                .value("json_params", entity.getJsonParams());
        session.execute(insert).wasApplied();
    }

    @Override
    public Optional<OperationStepEntity> getOperationStep(final String processId, String taskId) {
        final Statement query = select()
                .all()
                .from(OPERATION_STEP_TABLE)
                .where(eq("process_id", processId))
                .and(eq("task_id", taskId))
                .limit(1);

        final ResultSet rows = session.execute(query);

        return Optional.ofNullable(rows.one())
                .map(row -> new OperationStepEntity(
                        row.getString("process_id"),
                        row.getString("task_id"),
                        row.getTimestamp("step_date"),
                        row.getString("json_params"),
                        row.getString("json_data")));
    }
}
