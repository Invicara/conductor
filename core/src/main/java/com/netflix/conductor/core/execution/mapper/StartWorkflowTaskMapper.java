/*
 * Copyright 2022 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.core.execution.mapper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.conductor.common.metadata.tasks.TaskType;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.core.exception.TerminateWorkflowException;
import com.netflix.conductor.model.TaskModel;
import com.netflix.conductor.model.WorkflowModel;

import static com.netflix.conductor.common.metadata.tasks.TaskType.START_WORKFLOW;
import static com.netflix.conductor.common.metadata.tasks.TaskType.TASK_TYPE_START_WORKFLOW;

@Component
public class StartWorkflowTaskMapper implements TaskMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartWorkflowTaskMapper.class);

    @Override
    public TaskType getTaskType() {
        return START_WORKFLOW;
    }

    @Override
    public List<TaskModel> getMappedTasks(TaskMapperContext taskMapperContext)
            throws TerminateWorkflowException {
        WorkflowTask taskToSchedule = taskMapperContext.getTaskToSchedule();
        WorkflowModel workflowInstance = taskMapperContext.getWorkflowInstance();
        String taskId = taskMapperContext.getTaskId();

        TaskModel startWorkflowTask = new TaskModel();
        startWorkflowTask.setTaskType(TASK_TYPE_START_WORKFLOW);
        startWorkflowTask.setReferenceTaskName(taskToSchedule.getTaskReferenceName());
        startWorkflowTask.setWorkflowInstanceId(workflowInstance.getWorkflowId());
        startWorkflowTask.setWorkflowType(workflowInstance.getWorkflowName());
        startWorkflowTask.setCorrelationId(workflowInstance.getCorrelationId());
        startWorkflowTask.setScheduledTime(System.currentTimeMillis());
        startWorkflowTask.setTaskId(taskId);
        startWorkflowTask.addInput(taskMapperContext.getTaskInput());
        startWorkflowTask.setStatus(TaskModel.Status.SCHEDULED);
        startWorkflowTask.setWorkflowTask(taskToSchedule);
        startWorkflowTask.setWorkflowPriority(workflowInstance.getPriority());
        startWorkflowTask.setCallbackAfterSeconds(taskToSchedule.getStartDelay());
        LOGGER.debug("{} created", startWorkflowTask);
        return List.of(startWorkflowTask);
    }
}