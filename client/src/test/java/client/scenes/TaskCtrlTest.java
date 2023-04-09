package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TaskCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    private TaskCtrl taskCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        taskCtrl = new TaskCtrl(serverUtils, mainCtrl, null);
    }

    @Test
    public void testShiftUp() {
        var mockCard = new Card("title", "", null);
        var mockTask1 = new Task("a", mockCard);
        mockCard.tasks.add(mockTask1);
        var mockTask2 = new Task("b", mockCard);
        mockCard.tasks.add(mockTask2);
        taskCtrl.t = mockTask2;
        taskCtrl.shiftUp();
    }

    @Test
    public void testShiftDown() {
        var mockCard = new Card("title", "", null);
        var mockTask1 = new Task("a", mockCard);
        mockCard.tasks.add(mockTask1);
        var mockTask2 = new Task("b", mockCard);
        mockCard.tasks.add(mockTask2);
        taskCtrl.t = mockTask1;
        taskCtrl.shiftDown();
    }

    @Test
    public void testDelete() {
        var mockTask = new Task("a", null);
        mockTask.id = 1;
        taskCtrl.t = mockTask;
        taskCtrl.delete();
    }

}