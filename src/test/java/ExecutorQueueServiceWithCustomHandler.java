import com.queue.dto.QueueTaskDto;
import com.queue.enums.QueueTaskStatusEnum;
import com.queue.service.ExecutorQueueServiceWithLimit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutorQueueServiceWithCustomHandler {

    private ExecutorQueueServiceWithLimit executorQueueServiceWithLimit;

    @BeforeEach
    public void setUp() {
        executorQueueServiceWithLimit = new ExecutorQueueServiceWithLimit();
    }

    @Test
    public void testSubmitTask() {
        QueueTaskDto task = QueueTaskDto.create("Test Task");
        assertEquals(task.getStatus(), QueueTaskStatusEnum.CREATED);

        String taskId = executorQueueServiceWithLimit.submitTask(task);

        assertNotNull(taskId);
        assertNotNull(executorQueueServiceWithLimit.getTaskStatus(taskId));
        assertEquals(executorQueueServiceWithLimit.getTaskStatus(taskId), QueueTaskStatusEnum.IN_PROGRESS.getMessage());
    }

    @Test
    public void testGetTaskStatus() throws InterruptedException {
        QueueTaskDto task = QueueTaskDto.create("Test Task");

        String taskId = executorQueueServiceWithLimit.submitTask(task);

        String status = executorQueueServiceWithLimit.getTaskStatus(taskId);
        assertEquals("Задача в процессе выполнения.", status);

        Thread.sleep(2000); // Ждем завершения задачи

        status = executorQueueServiceWithLimit.getTaskStatus(taskId);
        assertEquals(QueueTaskStatusEnum.IN_PROGRESS.getMessage(), status);
    }

    @Test
    public void testCancelTask() {
        QueueTaskDto task = QueueTaskDto.create("Test Task");

        String taskId = executorQueueServiceWithLimit.submitTask(task);
        boolean isCancelled = executorQueueServiceWithLimit.cancelTask(taskId);

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

        executorQueueServiceWithLimit.submitTask(task1);
        executorQueueServiceWithLimit.submitTask(task2);
        executorQueueServiceWithLimit.submitTask(task3);
        executorQueueServiceWithLimit.submitTask(task4);
        executorQueueServiceWithLimit.submitTask(task5);
        executorQueueServiceWithLimit.submitTask(task6);
        executorQueueServiceWithLimit.submitTask(task7);
        executorQueueServiceWithLimit.submitTask(task8);

        // Пятая задача должна вызвать переполнение очереди
        Exception exception = assertThrows(RuntimeException.class, () -> executorQueueServiceWithLimit.submitTask(task9));

        String expectedMessage = "rejected from";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
