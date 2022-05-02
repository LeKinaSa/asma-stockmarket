package stockmarket.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test void test() {
        Action action;

        action = new Action(ActionType.DAY_OVER, 5);
        assertEquals(action.getMessage(), "DAY_OVER/5");

        action = Action.toAction("NEW_DAY/2");
        assertEquals(action.getMessage(), "NEW_DAY/2");

        action = Action.toAction("messageContent");
        assertNull(action);

        action = Action.toAction("A/3");
        assertNull(action);

        action = Action.toAction("START/a");
        assertNull(action);
    }
}