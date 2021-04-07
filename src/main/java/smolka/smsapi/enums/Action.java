package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Action {

    ORDER("order"),
    COST("cost"),
    ACTIVATION_HISTORY("activationHistory"),
    USER_INFO("userInf"),
    CURR_ACTIVATION("activationInf"),
    CURR_ACTIVATIONS("activationsInf");

    private final String actionName;

    public static Action getAction(String name) {
        for (Action action : values()) {
            if (action.getActionName().equals(name)) {
                return action;
            }
        }
        return null;
    }
}
