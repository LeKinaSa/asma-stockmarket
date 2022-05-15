package stockmarket.agents;

import jade.core.Agent;
import stockmarket.utils.Utils;

public class MyAgent extends Agent {
    protected static boolean log = false;

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

    protected boolean setBooleanVarFromArgument(Object[] args, int index, boolean defaultValue, String variableText) {
        boolean var = defaultValue;
        int varInt = -1;
        if (args != null && args.length > index) {
            try {
                varInt = Integer.parseInt((String) args[index]);
            }
            catch (NumberFormatException ignored) {}
        }
        
        if (varInt == 0) {
            var = false;
        }
        if (varInt == 1) {
            var = true;
        }
        else {
            Utils.info(this, "Using Default " + variableText + " : " + var);
        }
        return var;
    }

    public static boolean isLogging() {
        return log;
    }
}
