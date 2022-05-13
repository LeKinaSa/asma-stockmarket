package stockmarket;

import org.junit.jupiter.api.Test;

import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.managers.messages.DayOverListener;
import stockmarket.behaviours.managers.messages.MessageListener;
import stockmarket.behaviours.managers.messages.NewDayListener;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

class AppTest {
    @Test void dependenciesTest() {
        MoneyTransfer transfer = new MoneyTransfer(null, 0);
        String transferStr = transfer.toString();
        
        MoneyTransfer transferCopy = Utils.gson.fromJson(transfer.toString(), MoneyTransfer.class);
        String transferCopyStr = transferCopy.toString();
        
        assertEquals(transferCopyStr, transferStr);
    }

    void modifyList(List<String> list) {
        list.add("Index 1");
        list.add("Index 2");
    }

    @Test void listTest() {
        List<String> list = new ArrayList<>();
        list.add("Index 0");
        modifyList(list);
        assertEquals(3, list.size());
    }

    @Test void increaseDayTest() {
        DayOverListener listener = new DayOverListener();
        assertEquals(1, listener.nextDay());
    }

    @Test void actionTest() {
        Action action;

        action = new Action(ActionType.DAY_OVER, "5");
        assertEquals(action.getType().toString(), ActionType.DAY_OVER.toString());

        action = Action.toAction("NEW_DAY", "2");
        assertEquals(action.getType(), ActionType.NEW_DAY);
        assertEquals(action.getInformation(), "2");

        action = Action.toAction("messageContent", "1");
        assertNull(action);

        action = Action.toAction("A", "3");
        assertNull(action);
    }

    @Test void newDayMessageTest() {
        MessageListener listener = new NewDayListener(null);
        ACLMessage message = Utils.createNewDayMessage(null, 1);
        System.out.println(message.getOntology());
        assertTrue(listener.getTemplate().match(message));
    }
}
