package stockmarket.utils;

public class Action {
    private static final String SEPARATOR = "/";
    private final ActionType action;
    private final String additionalInformation;

    public Action(ActionType action, String additionalInformation) {
        this.action = action;
        this.additionalInformation = additionalInformation;
    }

    public Action(ActionType action) {
        this.action = action;
        this.additionalInformation = "";
    }

    public ActionType getType() {
        return action;
    }

    public String getInformation() {
        return additionalInformation;
    }

    public String getMessage() {
        return action + SEPARATOR + additionalInformation;
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

        return new Action(action, parts[1]);
    }
}
