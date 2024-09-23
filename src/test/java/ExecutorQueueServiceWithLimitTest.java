import com.queue.dto.QueueTaskDto;
import com.queue.enums.QueueTaskStatusEnum;
import com.queue.service.ExecutorQueueServiceWithCustomHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutorQueueServiceWithLimitTest {

    private ExecutorQueueServiceWithCustomHandler executorQueueServiceWithCustomHandler;

    @BeforeEach
    public void setUp() {
        executorQueueServiceWithCustomHandler = new ExecutorQueueServiceWithCustomHandler();
    }

    @Test
    public void testSubmitTask() {
        QueueTaskDto task = QueueTaskDto.create("Test Task");
        assertEquals(task.getStatus(), QueueTaskStatusEnum.CREATED);

        String taskId = executorQueueServiceWithCustomHandler.submitTask(task);

        assertNotNull(taskId);
        assertNotNull(executorQueueServiceWithCustomHandler.getTaskStatus(taskId));
        assertEquals(executorQueueServiceWithCustomHandler.getTaskStatus(taskId), QueueTaskStatusEnum.IN_PROGRESS.getMessage());
    }

    @Test
    public void testGetTaskStatus() throws InterruptedException {
        QueueTaskDto task = QueueTaskDto.create("Test Task");

        String taskId = executorQueueServiceWithCustomHandler.submitTask(task);

        String status = executorQueueServiceWithCustomHandler.getTaskStatus(taskId);
        assertEquals("Задача в процессе выполнения.", status);

        Thread.sleep(2000); // Ждем завершения задачи

        status = executorQueueServiceWithCustomHandler.getTaskStatus(taskId);
        assertEquals(QueueTaskStatusEnum.IN_PROGRESS.getMessage(), status);
    }

    @Test
    public void testCancelTask() {
        QueueTaskDto task = QueueTaskDto.create("Test Task");

        String taskId = executorQueueServiceWithCustomHandler.submitTask(task);
        boolean isCancelled = executorQueueServiceWithCustomHandler.cancelTask(taskId);

        assertTrue(isCancelled);
    }

    @Test
    public void testQueueOverflow() {
        QueueTaskDto task1 = QueueTaskDto.create("Task 1");
        QueueTaskDto task2 = QueueTaskDto.create("Task 2");
        QueueTaskDto task3 = QueueTaskDto.create("Task 3");
        QueueTaskDto task4 = QueueTaskDto.create("Task 4");
        QueueTaskDto task5 = QueueTaskDto.create("Task 5");
        QueueTaskDto task6 = QueueTaskDto.create("Task 6");
        QueueTaskDto task7 = QueueTaskDto.create("Task 7");
        QueueTaskDto task8 = QueueTaskDto.create("Task 8");
        QueueTaskDto task9 = QueueTaskDto.create("Task 9");

        executorQueueServiceWithCustomHandler.submitTask(task1);
        executorQueueServiceWithCustomHandler.submitTask(task2);
        executorQueueServiceWithCustomHandler.submitTask(task3);
        executorQueueServiceWithCustomHandler.submitTask(task4);
        executorQueueServiceWithCustomHandler.submitTask(task5);
        executorQueueServiceWithCustomHandler.submitTask(task6);
        executorQueueServiceWithCustomHandler.submitTask(task7);
        executorQueueServiceWithCustomHandler.submitTask(task8);

        // Пятая задача должна вызвать переполнение очереди
        Exception exception = assertThrows(RuntimeException.class, () -> executorQueueServiceWithCustomHandler.submitTask(task9));

        String expectedMessage = "Queue is full";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
