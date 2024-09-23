package com.queue.service;


import com.queue.dto.QueueTaskDto;
import com.queue.enums.QueueTaskStatusEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для выполнения задач в очереди с ограничением по количеству потоков.
 *
 * <p>При переполнении очереди падает ошибка.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorQueueServiceWithLimit {

    // количество потоков, которые нужно хранить в пуле, даже если они простаивают
    private static final Integer CORE_POOL_SIZE = 0;
    // максимальное количество потоков, которые могут быть созданы в пуле
    private static final Integer MAX_POOL_SIZE = 4;
    // время простоя потока, после которого он будет удален из пула
    private static final Long KEEP_ALIVE_TIME = 0L;
    // единица измерения времени простоя потока
    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;
    // размер очереди, в которую будут добавляться задачи
    private static final Integer QUEUE_SIZE = 4;

    private final ExecutorService executorService;
    @Getter
    private final Map<String, Future<?>> generateExcelTaskMap;

    public ExecutorQueueServiceWithLimit() {
        this.executorService = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                UNIT,
                new LinkedBlockingQueue<>(QUEUE_SIZE)
        );
        this.generateExcelTaskMap = new ConcurrentHashMap<>();
    }

    public String submitTask(QueueTaskDto request) {
        Future<?> future = executorService.submit(() -> {
            try {
                generateExcel(request);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        generateExcelTaskMap.put(request.getId(), future);
        return request.getId();
    }

    private void generateExcel(QueueTaskDto queueTask) throws InterruptedException {
        queueTask.setStatus(QueueTaskStatusEnum.IN_PROGRESS);
        log.info("Начата генерация Excel для: " + queueTask.getDescription());
        // Симуляция выполнения задачи
        Thread.sleep(5000);
        log.info("Завершена генерация Excel для: " + queueTask.getDescription());
        queueTask.setStatus(QueueTaskStatusEnum.COMPLETED);
        generateExcelTaskMap.remove(queueTask.getId());
    }

    public String getTaskStatus(String taskId) {
        Future<?> future = generateExcelTaskMap.get(taskId);
        if (future == null) {
            return "Задача с ID " + taskId + " не найдена.";
        }
        if (future.isCancelled()) {
            return "Задача отменена.";
        }
        if (future.isDone()) {
            return "Задача завершена.";
        }
        return "Задача в процессе выполнения.";
    }

    public boolean cancelTask(String taskId) {
        Future<?> future = generateExcelTaskMap.get(taskId);
        if (future != null && !future.isDone()) {
            return future.cancel(true); // Отмена задачи
        }
        return false;
    }

}
