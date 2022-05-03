package stockmarket.utils;

public class Action {
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

    public static Action toAction(String ontology, String content) {
        ActionType action;
        try {
            action = ActionType.valueOf(ontology);
        }
        catch (IllegalArgumentException e) {
            return null;
        }

        return new Action(action, content);
    }
}
