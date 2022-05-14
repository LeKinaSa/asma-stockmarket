package stockmarket.agents;

import jade.core.Agent;
import stockmarket.utils.Utils;

public class MyAgent extends Agent {
    public void finish() {
        this.save();
        Utils.info(this, "Goodbye");
    }

    public void save() {}
}
