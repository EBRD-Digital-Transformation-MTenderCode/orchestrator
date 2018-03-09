package com.procurement.orchestrator.domain.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationStepEntity {

    private String processId;

    private String taskId;

    private Date date;

    private String jsonParams;

    private String jsonData;
}
