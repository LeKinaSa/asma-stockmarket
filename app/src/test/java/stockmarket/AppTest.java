package stockmarket;

import org.junit.jupiter.api.Test;

import stockmarket.listeners.messages.DayOverListener;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test void test() {
        DayOverListener listener = new DayOverListener();
        assertEquals(listener.nextDay(), 1);
    }
}
