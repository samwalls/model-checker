package formula.pathFormula;

import formula.FormulaParser;
import formula.stateFormula.*;
import java.util.*;

public class Eventually extends PathFormula {
    public final StateFormula stateFormula;
    private Set<String> leftActions;
    private Set<String> rightActions;
    private String leftActionsIdentifier, rightActionsIdentifier;

    public Eventually(StateFormula stateFormula, String leftActionsIdentifier, Set<String> leftActions, String rightActionsIdentifier, Set<String> rightActions) {
        super();
        this.stateFormula = stateFormula;
        this.leftActionsIdentifier = leftActionsIdentifier;
        this.leftActions = leftActions;
        this.rightActionsIdentifier = rightActionsIdentifier;
        this.rightActions = rightActions;
    }

    public Set<String> getLeftActions() {
        return leftActions;
    }

    public Set<String> getRightActions() {
        return rightActions;
    }

    @Override
    public void writeToBuffer(StringBuilder buffer) {
        if (leftActionsIdentifier != null)
            buffer.append(leftActionsIdentifier);
        buffer.append(FormulaParser.EVENTUALLY_TOKEN);
        if (rightActionsIdentifier != null)
            buffer.append(rightActionsIdentifier);
        stateFormula.writeToBuffer(buffer);
        ;
    }

    public String getLeftActionsIdentifier() {
        return leftActionsIdentifier;
    }

    public String getRightActionsIdentifier() {
        return rightActionsIdentifier;
    }
}
