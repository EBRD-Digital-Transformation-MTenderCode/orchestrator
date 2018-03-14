package com.procurement.orchestrator.cassandra.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.procurement.orchestrator.domain.entity.OperationEntity;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.domain.entity.StageEntity;
import java.util.Optional;
import org.springframework.stereotype.Service;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

@Service
public class CassandraDaoImpl implements CassandraDao {

    private static final String OPERATION_STEP_TABLE = "orchestrator_operation_step";
    private static final String OPERATION_TABLE = "orchestrator_operation";
    private static final String REQUEST_TABLE = "orchestrator_request";
    private static final String STAGE_TABLE = "orchestrator_stage";
    private static final String REQUEST_DATE = "request_date";
    private static final String OPERATION_ID = "operation_id";
    private static final String JSON_PARAMS = "json_params";
    private static final String REQUEST_ID = "request_id";
    private static final String PROCESS_ID = "process_id";
    private static final String JSON_DATA = "json_data";
    private static final String STEP_DATE = "step_date";
    private static final String TASK_ID = "task_id";
    private static final String COUNTRY = "country";
    private static final String STAGE = "stage";
    private static final String CPID = "cp_id";
    private static final String PMD = "pmd";

    private final Session session;

    public CassandraDaoImpl(final Session session) {
        this.session = session;
    }

    @Override
    public void saveRequest(final RequestEntity entity) {
        final Insert insert = insertInto(REQUEST_TABLE);
        insert
                .value(REQUEST_ID, entity.getRequestId())
                .value(REQUEST_DATE, entity.getRequestDate())
                .value(OPERATION_ID, entity.getOperationId())
                .value(JSON_DATA, entity.getJsonData())
                .value(JSON_PARAMS, entity.getJsonParams());
        session.execute(insert);
    }

    @Override
    public Optional<RequestEntity> getRequestById(final String id) {
        final Statement query = select()
                .all()
                .from(REQUEST_TABLE)
                .where(eq(REQUEST_ID, id))
                .limit(1);

        final ResultSet rows = session.execute(query);

        return Optional.ofNullable(rows.one())
                .map(row -> new RequestEntity(
                        row.getString(REQUEST_ID),
                        row.getTimestamp(REQUEST_DATE),
                        row.getString(OPERATION_ID),
                        row.getString(JSON_DATA),
                        row.getString(JSON_PARAMS)));
    }

    @Override
    public Boolean saveOperationIfNotExist(final OperationEntity entity) {
        final Insert insert = insertInto(OPERATION_TABLE).ifNotExists();
        insert
                .value(OPERATION_ID, entity.getOperationId())
                .value(PROCESS_ID, entity.getProcessId());

        final ResultSet resultSet = session.execute(insert);

        if (!resultSet.wasApplied()) {
            return resultSet.one().getString(PROCESS_ID).equals(entity.getProcessId());
        }
        return true;
    }

    public Boolean isOperationExist(final String operationId) {
        final Statement query = select()
                .from(OPERATION_TABLE)
                .where(eq(OPERATION_ID, operationId));

        final ResultSet rs = session.execute(query);

        return (rs.all().size() > 0) ? true : false;
    }

    @Override
    public void saveOperationStep(final OperationStepEntity entity) {
        final Insert insert = insertInto(OPERATION_STEP_TABLE);
        insert
                .value(PROCESS_ID, entity.getProcessId())
                .value(TASK_ID, entity.getTaskId())
                .value(STEP_DATE, entity.getDate())
                .value(JSON_DATA, entity.getJsonData())
                .value(JSON_PARAMS, entity.getJsonParams());

        session.execute(insert);
    }

    @Override
    public Optional<OperationStepEntity> getOperationStep(final String processId, final String taskId) {
        final Statement query = select()
                .all()
                .from(OPERATION_STEP_TABLE)
                .where(eq(PROCESS_ID, processId))
                .and(eq(TASK_ID, taskId))
                .limit(1);

        final ResultSet rows = session.execute(query);

        return Optional.ofNullable(rows.one())
                .map(row -> new OperationStepEntity(
                        row.getString(PROCESS_ID),
                        row.getString(TASK_ID),
                        row.getTimestamp(STEP_DATE),
                        row.getString(JSON_PARAMS),
                        row.getString(JSON_DATA)));

    }

    @Override
    public void saveStage(final StageEntity entity) {
        final Insert insert = insertInto(STAGE_TABLE);
        insert
                .value(CPID, entity.getCpId())
                .value(STAGE, entity.getStage())
                .value(COUNTRY, entity.getCountry())
                .value(PMD, entity.getPmd());

        session.execute(insert);
    }

    @Override
    public Optional<StageEntity> getStageByCpId(final String cpId) {
        final Statement query = select()
                .all()
                .from(STAGE_TABLE)
                .where(eq(CPID, cpId))
                .limit(1);

        final ResultSet rows = session.execute(query);
        return Optional.ofNullable(rows.one())
                .map(row -> new StageEntity(
                        row.getString(CPID),
                        row.getString(STAGE),
                        row.getString(COUNTRY),
                        row.getString(PMD)));
    }
}
