package com.queue.dto;

import com.queue.enums.QueueTaskStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class QueueTaskDto {

    private final String id;
    private final String description;
    private QueueTaskStatusEnum status;

    public QueueTaskDto(String description) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.status = QueueTaskStatusEnum.CREATED;
    }
    public static QueueTaskDto create(String description) {
        return new QueueTaskDto(description);
    }

    public void updateStatus(QueueTaskStatusEnum status) {
        this.status = status;
    }
}
