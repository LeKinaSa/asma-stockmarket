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
import com.google.gson.Gson;

class AppTest {
    @Test void dependenciesTest() {
        Gson gson = new Gson();

        MoneyTransfer transfer = new MoneyTransfer();
        String transferStr = gson.toJson(transfer);
        
        MoneyTransfer transferCopy = gson.fromJson(gson.toJson(transfer), MoneyTransfer.class);
        String transferCopyStr = gson.toJson(transferCopy);
        
        assertEquals(transferStr, transferCopyStr);
    }

    @Test void increaseDayTest() {
        DayOverListener listener = new DayOverListener();
        assertEquals(listener.nextDay(), 1);
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
        MessageListener listener = new NewDayListener();
        ACLMessage message = Utils.createNewDayMessage(null, 1);
        System.out.println(message.getOntology());
        assertTrue(listener.getTemplate().match(message));
    }
}
