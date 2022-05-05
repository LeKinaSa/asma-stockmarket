package stockmarket;

import org.junit.jupiter.api.Test;
import stockmarket.listeners.messages.DayOverListener;
import stockmarket.utils.Stock;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.Gson;

class AppTest {
    @Test void dependenciesTest() {
        Gson gson = new Gson();

        Stock stock = new Stock();
        String stockStr = gson.toJson(stock);
        
        Stock stockCopy = gson.fromJson(gson.toJson(stock), Stock.class);
        String stockCopyStr = gson.toJson(stockCopy);
        
        assertEquals(stockStr, stockCopyStr);
    }

    @Test void increaseDayTest() {
        DayOverListener listener = new DayOverListener();
        assertEquals(listener.nextDay(), 1);
    }
}
