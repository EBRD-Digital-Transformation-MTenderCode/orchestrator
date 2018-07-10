package com.procurement.orchestrator.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.entity.*;
import java.util.Optional;
import org.springframework.stereotype.Service;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

@Service
public class CassandraDaoImpl implements CassandraDao {

    private static final String OPERATION_STEP_TABLE = "orchestrator_operation_step";
    private static final String OPERATION_TABLE = "orchestrator_operation";
    private static final String REQUEST_TABLE = "orchestrator_request";
    private static final String STAGE_TABLE = "orchestrator_stage";
    private static final String STAGE_RULES_TABLE = "orchestrator_stage_rules";
    private static final String RULES_TABLE = "orchestrator_rules";
    private static final String REQUEST_DATE = "request_date";
    private static final String OPERATION_ID = "operation_id";
    private static final String CONTEXT = "context";
    private static final String REQUEST_ID = "request_id";
    private static final String PROCESS_ID = "process_id";
    private static final String JSON_DATA = "json_data";
    private static final String REQUEST_DATA = "request_data";
    private static final String RESPONSE_DATA = "response_data";
    private static final String STEP_DATE = "step_date";
    private static final String TASK_ID = "task_id";
    private static final String CPID = "cp_id";

    private static final String OPERATION_TYPE = "operation_type";
    private static final String PREV_STAGE = "prev_stage";
    private static final String NEW_STAGE = "new_stage";
    private static final String STAGE = "stage";
    private static final String COUNTRY = "country";
    private static final String PHASE = "phase";
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
                .value(CONTEXT, entity.getJsonParams());
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
                        row.getString(CONTEXT)));
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
        return rs.all().size() > 0;
    }

    @Override
    public void saveOperationStep(final OperationStepEntity entity) {
        final Insert insert = insertInto(OPERATION_STEP_TABLE);
        insert
                .value(PROCESS_ID, entity.getProcessId())
                .value(TASK_ID, entity.getTaskId())
                .value(OPERATION_ID, entity.getOperationId())
                .value(STEP_DATE, entity.getDate())
                .value(REQUEST_DATA, entity.getRequestData())
                .value(RESPONSE_DATA, entity.getResponseData())
                .value(CONTEXT, entity.getJsonParams())
                .value(CPID, entity.getCpId());

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
                        row.getString(OPERATION_ID),
                        row.getTimestamp(STEP_DATE),
                        row.getString(CONTEXT),
                        row.getString(REQUEST_DATA),
                        row.getString(RESPONSE_DATA),
                        row.getString(CPID)
                ));
    }

    @Override
    public void saveStage(final StageEntity entity) {
        final Insert insert = insertInto(STAGE_TABLE);
        insert
                .value(CPID, entity.getCpId())
                .value(STAGE, entity.getStage())
                .value(COUNTRY, entity.getCountry())
                .value(PMD, entity.getPmd())
                .value(PHASE, entity.getPhase());

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
                        row.getString(PMD),
                        row.getString(PHASE)));
    }

    @Override
    public Boolean isRulesExist(final Rules rules) {
        final Statement query = select()
                .all()
                .from(RULES_TABLE)
                .where(eq(NEW_STAGE, rules.getNewStage()))
                .and(eq(PREV_STAGE, rules.getStage()))
                .and(eq(COUNTRY, rules.getCountry()))
                .and(eq(PMD, rules.getPmd()))
                .and(eq(PHASE, rules.getPhase()))
                .and(eq(OPERATION_TYPE, rules.getOperationType()))
                .limit(1);
        final ResultSet rs = session.execute(query);
        return rs.all().size() > 0;
    }

    @Override
    public Optional<StageRulesEntity> getStageFromRules(final String country,
                                                        final String pmd,
                                                        final String operationType) {
        final Statement query = select()
                .all()
                .from(STAGE_RULES_TABLE)
                .where(eq(COUNTRY, country))
                .and(eq(PMD, pmd))
                .and(eq(OPERATION_TYPE, operationType))
                .limit(1);

        final ResultSet rows = session.execute(query);
        return Optional.ofNullable(rows.one())
                .map(row -> new StageRulesEntity(
                        row.getString(COUNTRY),
                        row.getString(PMD),
                        row.getString(OPERATION_TYPE),
                        row.getString(STAGE)));
    }
}
