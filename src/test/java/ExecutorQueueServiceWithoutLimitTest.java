import com.queue.dto.QueueTaskDto;
import com.queue.enums.QueueTaskStatusEnum;
import com.queue.service.ExecutorQueueServiceWithoutLimit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutorQueueServiceWithoutLimitTest {

    private ExecutorQueueServiceWithoutLimit executorQueueServiceWithoutLimit;

    @BeforeEach
    public void setUp() {
        executorQueueServiceWithoutLimit = new ExecutorQueueServiceWithoutLimit();
    }

    @Test
    public void testSubmitTask() {
        QueueTaskDto task = QueueTaskDto.create("Test Task");
        assertEquals(task.getStatus(), QueueTaskStatusEnum.CREATED);

        String taskId = executorQueueServiceWithoutLimit.submitTask(task);

        assertNotNull(taskId);
        assertNotNull(executorQueueServiceWithoutLimit.getTaskStatus(taskId));
        assertEquals(executorQueueServiceWithoutLimit.getTaskStatus(taskId), QueueTaskStatusEnum.IN_PROGRESS.getMessage());
    }

    @Test
    public void testCancelTask() {
        QueueTaskDto task = QueueTaskDto.create("Test Task");

        String taskId = executorQueueServiceWithoutLimit.submitTask(task);
        boolean isCancelled = executorQueueServiceWithoutLimit.cancelTask(taskId);

        assertTrue(isCancelled);
    }

    @Test
    public void testQueueOverflow() throws InterruptedException {
        QueueTaskDto task1 = QueueTaskDto.create("Task 1");
        QueueTaskDto task2 = QueueTaskDto.create("Task 2");
        QueueTaskDto task3 = QueueTaskDto.create("Task 3");
        QueueTaskDto task4 = QueueTaskDto.create("Task 4");
        QueueTaskDto task5 = QueueTaskDto.create("Task 5");
        QueueTaskDto task6 = QueueTaskDto.create("Task 6");
        QueueTaskDto task7 = QueueTaskDto.create("Task 7");
        QueueTaskDto task8 = QueueTaskDto.create("Task 8");
        QueueTaskDto task9 = QueueTaskDto.create("Task 9");
        QueueTaskDto task10 = QueueTaskDto.create("Task 10");

        QueueTaskDto task11 = QueueTaskDto.create("Task 11");
        QueueTaskDto task12 = QueueTaskDto.create("Task 12");
        QueueTaskDto task13 = QueueTaskDto.create("Task 13");
        QueueTaskDto task14 = QueueTaskDto.create("Task 14");
        QueueTaskDto task15 = QueueTaskDto.create("Task 15");
        QueueTaskDto task16 = QueueTaskDto.create("Task 16");
        QueueTaskDto task17 = QueueTaskDto.create("Task 17");
        QueueTaskDto task18 = QueueTaskDto.create("Task 18");
        QueueTaskDto task19 = QueueTaskDto.create("Task 19");
        QueueTaskDto task20 = QueueTaskDto.create("Task 20");

        executorQueueServiceWithoutLimit.submitTask(task1);
        executorQueueServiceWithoutLimit.submitTask(task2);
        executorQueueServiceWithoutLimit.submitTask(task3);
        executorQueueServiceWithoutLimit.submitTask(task4);
        executorQueueServiceWithoutLimit.submitTask(task5);
        executorQueueServiceWithoutLimit.submitTask(task6);
        executorQueueServiceWithoutLimit.submitTask(task7);
        executorQueueServiceWithoutLimit.submitTask(task8);
        executorQueueServiceWithoutLimit.submitTask(task9);
        executorQueueServiceWithoutLimit.submitTask(task10);

        executorQueueServiceWithoutLimit.submitTask(task11);
        executorQueueServiceWithoutLimit.submitTask(task12);
        executorQueueServiceWithoutLimit.submitTask(task13);
        executorQueueServiceWithoutLimit.submitTask(task14);
        executorQueueServiceWithoutLimit.submitTask(task15);
        executorQueueServiceWithoutLimit.submitTask(task16);
        executorQueueServiceWithoutLimit.submitTask(task17);
        executorQueueServiceWithoutLimit.submitTask(task18);
        executorQueueServiceWithoutLimit.submitTask(task19);
        executorQueueServiceWithoutLimit.submitTask(task20);

        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelTaskMap().size(), 20);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelRunnableQueue().size(), 15, 1);

        Thread.sleep(7000);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelTaskMap().size(), 16);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelRunnableQueue().size(), 12, 1);

        Thread.sleep(7000);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelTaskMap().size(), 12);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelRunnableQueue().size(), 10, 3);

        Thread.sleep(7000);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelTaskMap().size(), 4);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelRunnableQueue().size(), 0);

        Thread.sleep(7000);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelTaskMap().size(), 0);
        assertEquals(executorQueueServiceWithoutLimit.getGenerateExcelRunnableQueue().size(), 0);
    }

}
