package formula.pathFormula;

import formula.*;
import formula.stateFormula.*;
import java.util.Set;

public class Until extends PathFormula {
    public final StateFormula left;
    public final StateFormula right;
    private Set<String> leftActions;
    private Set<String> rightActions;
    private String leftActionsIdentifier;
    private String rightActionsIdentifier;

    public Until(StateFormula left, StateFormula right, String leftActionsIdentifier, Set<String> leftActions, String rightActionsIdentifier, Set<String> rightActions) {
        super();
        this.left = left;
        this.right = right;
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
        buffer.append("(");
        left.writeToBuffer(buffer);
        buffer.append(" ");
        if (leftActionsIdentifier != null)
            buffer.append(leftActionsIdentifier);
        buffer.append(FormulaParser.UNTIL_TOKEN);
        if (rightActionsIdentifier != null)
            buffer.append(rightActionsIdentifier);
        buffer.append(" ");
        right.writeToBuffer(buffer);
        buffer.append(")");
    }

    public String getLeftActionsIdentifier() {
        return leftActionsIdentifier;
    }

    public String getRightActionsIdentifier() {
        return rightActionsIdentifier;
    }
}
