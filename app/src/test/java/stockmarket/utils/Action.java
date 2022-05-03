package stockmarket.utils;

import org.junit.jupiter.api.Test;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test void actionTest() {
        Action action;

        action = new Action(ActionType.DAY_OVER, "5");
        assertEquals(action.getMessage(), "DAY_OVER/5");

        action = Action.toAction("NEW_DAY/2");
        assertEquals(action.getMessage(), "NEW_DAY/2");

        action = Action.toAction("messageContent");
        assertNull(action);

        action = Action.toAction("A/3");
        assertNull(action);
    }

    @Test void messageTemplateTest() {
        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchOntology(ActionType.NEW_DAY.toString())
        );
        ACLMessage message = Utils.createMessage(
            null, ACLMessage.INFORM,
            ActionType.NEW_DAY.toString(), "1",
            null, null
        );
        assertTrue(template.match(message));
    }
}