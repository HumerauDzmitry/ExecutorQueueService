package com.queue.service;


import com.queue.dto.QueueTaskDto;
import com.queue.enums.QueueTaskStatusEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для выполнения задач в очереди с без ограничений по количеству потоков.
 *
 * <p>Этот код использует SynchronousQueue для передачи задач между потоками и резервную очередь LinkedBlockingQueue для хранения задач,
 * которые не могут быть выполнены сразу. Обработчик отказов добавляет задачи в резервную очередь при переполнении,
 * и отдельный поток обрабатывает задачи из резервной очереди.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorQueueServiceWithoutLimit {

    // количество потоков, которые нужно хранить в пуле, даже если они простаивают
    private static final Integer CORE_POOL_SIZE = 0;
    // максимальное количество потоков, которые могут быть созданы в пуле
    private static final Integer MAX_POOL_SIZE = 4;
    // время простоя потока, после которого он будет удален из пула
    private static final Long KEEP_ALIVE_TIME = 0L;
    // единица измерения времени простоя потока
    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;

    private final ExecutorService executorService;
    @Getter
    // Очередь задач на выполнение.
    private final BlockingQueue<Runnable> generateExcelRunnableQueue;
    @Getter
    private final Map<String, Future<?>> generateExcelTaskMap;

    public ExecutorQueueServiceWithoutLimit() {
        this.generateExcelRunnableQueue = new LinkedBlockingQueue<>();
        RejectedExecutionHandler handler = (r, executor) -> {
            try {
                generateExcelRunnableQueue.put(r);
//                log.info("Задача добавлена в RunnableQueue." + r);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        this.executorService = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                UNIT,
                new SynchronousQueue<>(),
                handler
        );
        this.generateExcelTaskMap = new ConcurrentHashMap<>();
        startBackupQueueProcessor();
    }

    private void startBackupQueueProcessor() {
        new Thread(() -> {
            while (true) {
                try {
                    Runnable task = generateExcelRunnableQueue.take();
                    executorService.execute(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
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
