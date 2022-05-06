package stockmarket;

import org.junit.jupiter.api.Test;

import stockmarket.behaviours.managers.messages.DayOverListener;
import stockmarket.utils.MoneyTransfer;
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
}
