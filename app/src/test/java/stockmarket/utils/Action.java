package stockmarket.utils;

import org.junit.jupiter.api.Test;

import jade.lang.acl.ACLMessage;
import stockmarket.agents.Listener;
import stockmarket.agents.NewDayListener;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {
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
        Listener listener = new NewDayListener();
        ACLMessage message = Utils.createNewDayMessage(1);
        System.out.println(message.getOntology());
        assertTrue(listener.getTemplate().match(message));
    }
}