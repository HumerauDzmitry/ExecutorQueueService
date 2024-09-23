package com.queue.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueTaskStatusEnum {

    CREATED("Задача создана."),
    IN_PROGRESS("Задача в процессе выполнения."),
    COMPLETED("Задача завершена."),
    DELETED(""),
    FAILED("");

    private final String message;

}
