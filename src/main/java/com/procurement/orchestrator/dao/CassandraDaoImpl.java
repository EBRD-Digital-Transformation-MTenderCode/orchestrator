package com.procurement.orchestrator.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Update;
import com.procurement.orchestrator.domain.Rule;
import com.procurement.orchestrator.domain.TypeOfProcess;
import com.procurement.orchestrator.domain.entity.ContextEntity;
import com.procurement.orchestrator.domain.entity.OperationEntity;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

@Service
public class CassandraDaoImpl implements CassandraDao {


    private static final String OPERATION_STEP_TABLE = "orchestrator_operation_step";
    private static final String PROCESS_TYPE_TABLE = "orchestrator_process_type";
    private static final String OPERATION_TABLE = "orchestrator_operation";
    private static final String REQUEST_TABLE = "orchestrator_request";
    private static final String CONTEXT_TABLE = "orchestrator_context";
    private static final String RULES_TABLE = "orchestrator_rules";
    private static final String CHECK_TABLE = "orchestrator_check_active";
    private static final String ACTIVE = "active";
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
    private static final String ID = "id";

    private static final String OPERATION_TYPE = "operation_type";
    private static final String PROCESS_TYPE = "process_type";
    private static final String PREV_STAGE = "prev_stage";
    private static final String NEW_STAGE = "new_stage";
    private static final String PREV_PHASE = "prev_phase";
    private static final String NEW_PHASE = "new_phase";
    private static final String COUNTRY = "country";
    private static final String PROCESS = "process";
    private static final String PMD = "pmd";

    private final Session session;

    public CassandraDaoImpl(final Session session) {
        this.session = session;
    }

    @Override
    public Boolean setActiveTrue(final String id) {
        final Insert insert = insertInto(CHECK_TABLE).ifNotExists();
        insert.value(ID, id).value(ACTIVE, true);
        final ResultSet resultSet = session.execute(insert);
        if (!resultSet.wasApplied()) {
            final Update update = update(CHECK_TABLE);
            update.with(set(ACTIVE, true)).where(eq(ID, id)).onlyIf(eq(ACTIVE, false));
            return session.execute(update).wasApplied();
        }
        return true;
    }


    @Override
    public void saveRequest(final RequestEntity entity) {
        final Insert insert = insertInto(REQUEST_TABLE);
        insert
                .value(REQUEST_ID, entity.getRequestId())
                .value(REQUEST_DATE, entity.getRequestDate())
                .value(OPERATION_ID, entity.getOperationId())
                .value(JSON_DATA, entity.getJsonData())
                .value(CONTEXT, entity.getContext());
        session.execute(insert);
    }

    @Override
    public Optional<RequestEntity> getRequestById(final String id) {
        final Statement query = select()
                .column(REQUEST_ID)
                .column(REQUEST_DATE)
                .column(OPERATION_ID)
                .column(JSON_DATA)
                .column(CONTEXT)
                .writeTime("OPERATION_ID")
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
                                row.getString(CONTEXT)
                        )
                );
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
                .value(CONTEXT, entity.getContext())
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
                        row.getString(CPID)));
    }

    @Override
    public void saveContext(final ContextEntity entity) {
        final Insert insert = insertInto(CONTEXT_TABLE);
        insert
                .value(CPID, entity.getCpId())
                .value(CONTEXT, entity.getContext());
        session.execute(insert);
    }

    @Override
    public Optional<ContextEntity> getContextByCpId(final String cpId) {
        final Statement query = select()
                .all()
                .from(CONTEXT_TABLE)
                .where(eq(CPID, cpId))
                .limit(1);

        final ResultSet rows = session.execute(query);
        return Optional.ofNullable(rows.one())
                .map(row -> new ContextEntity(
                        row.getString(CPID),
                        row.getString(CONTEXT)));
    }

    @Override
    public Optional<TypeOfProcess> getProcess(String country, String pmd, String process) {
        final Statement query = select()
                .all()
                .from(PROCESS_TYPE_TABLE)
                .where(eq(COUNTRY, country))
                .and(eq(PMD, pmd))
                .and(eq(PROCESS, process))
                .limit(1);
        final ResultSet rows = session.execute(query);
        return Optional.ofNullable(rows.one())
                .map(row -> new TypeOfProcess(
                        row.getString(COUNTRY),
                        row.getString(PMD),
                        row.getString(PROCESS),
                        row.getString(PROCESS_TYPE)));
    }

    @Override
    public Optional<Rule> getRule(final String country,
                                  final String pmd,
                                  final String processType) {
        final Statement query = select()
                .all()
                .from(RULES_TABLE)
                .where(eq(COUNTRY, country))
                .and(eq(PMD, pmd))
                .and(eq(PROCESS_TYPE, processType))
                .limit(1);
        final ResultSet rows = session.execute(query);
        return Optional.ofNullable(rows.one())
                .map(row -> new Rule(
                        row.getString(COUNTRY),
                        row.getString(PMD),
                        row.getString(PROCESS_TYPE),
                        row.getString(PREV_STAGE),
                        row.getString(NEW_STAGE),
                        row.getString(PREV_PHASE),
                        row.getString(NEW_PHASE),
                        row.getString(OPERATION_TYPE)));
    }

    @Override
    public List<Rule> getRules(final String country,
                               final String pmd,
                               final String processType) {

        final Statement query = select()
                .all()
                .from(RULES_TABLE)
                .where(eq(COUNTRY, country))
                .and(eq(PMD, pmd))
                .and(eq(PROCESS_TYPE, processType));
        final ResultSet rows = session.execute(query);
        final List<Rule> entities = new ArrayList<>();
        for (final Row row : rows) {
            entities.add(new Rule(
                    row.getString(COUNTRY),
                    row.getString(PMD),
                    row.getString(PROCESS_TYPE),
                    row.getString(PREV_STAGE),
                    row.getString(NEW_STAGE),
                    row.getString(PREV_PHASE),
                    row.getString(NEW_PHASE),
                    row.getString(OPERATION_TYPE)));
        }
        return entities;
    }
}
