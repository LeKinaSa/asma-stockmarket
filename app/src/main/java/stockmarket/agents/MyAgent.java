package stockmarket.agents;

import jade.core.Agent;
import stockmarket.utils.Utils;

public class MyAgent extends Agent {
    public void finish() {
        this.save();
        Utils.info(this, "Goodbye");
    }

    public void save() {}

    protected int setIntegerVarFromArgument(Object[] args, int index, int defaultValue, String variableText) {
        int var = defaultValue;
        boolean set = false;
        if (args != null && args.length > index) {
            try {
                var = Integer.parseInt((String) args[index]);
                set = true;
            }
            catch (NumberFormatException ignored) {}
        }
        if (!set) {
            Utils.info(this, "Using Default " + variableText + " : " + var);
        }
        return var;
    }

    protected double setDoubleVarFromArgument(Object[] args, int index, double defaultValue, String variableText) {
        double var = defaultValue;
        boolean set = false;
        if (args != null && args.length > index) {
            try {
                var = Double.parseDouble((String) args[index]);
                set = true;
            }
            catch (NumberFormatException ignored) {}
        }
        if (!set) {
            Utils.info(this, "Using Default " + variableText + " : " + var);
        }
        return var;
    }
}
