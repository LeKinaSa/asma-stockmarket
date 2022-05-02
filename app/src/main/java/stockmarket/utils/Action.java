package stockmarket.utils;

public class Action {
    private static final String SEPARATOR = "/";
    private final ActionType action;
    private final int day;

    public Action(ActionType action, int day) {
        this.action = action;
        this.day = day;
    }

    public Action(ActionType action) {
        this.action = action;
        this.day = 0;
    }

    public ActionType getType() {
        return this.action;
    }

    public int getDay() {
        return this.day;
    }

    public String getMessage() {
        return action + SEPARATOR + day;
    }

    public static Action toAction(String messageContent) {
        if (!messageContent.contains(SEPARATOR)) {
            return null;
        }
        String[] parts = messageContent.split(SEPARATOR);

        ActionType action = null;
        try {
            action = ActionType.valueOf(parts[0]);
        }
        catch (IllegalArgumentException e) {
            return null;
        }

        int day = 0;
        try {
            day = Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException e) {
            return null;
        }

        if (action == null) {
            return null;
        }
        return new Action(action, day);
    }
}
