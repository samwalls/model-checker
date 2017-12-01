package formula.pathFormula;

import formula.FormulaParser;
import formula.stateFormula.*;
import java.util.*;

public class Always extends PathFormula {
    public final StateFormula stateFormula;

    private Set<String> actionSet1 = new HashSet<>();
    private Set<String> actionSet2 = new HashSet<>();
    private String actionSetIdentifier1;
    private String actionSetIdentifier2;

    /**
     * An always statement with the action set restrictions as a union of two others.
     * @param stateFormula
     * @param actionSetIdentifier1
     * @param actionSet1
     * @param actionSetIdentifier2
     * @param actionSet2
     */
    public Always(StateFormula stateFormula, String actionSetIdentifier1, Set<String> actionSet1, String actionSetIdentifier2, Set<String> actionSet2) {
        this.stateFormula = stateFormula;
        this.actionSet1 = actionSet1;
        this.actionSetIdentifier1 = actionSetIdentifier1;
        this.actionSet2 = actionSet2;
        this.actionSetIdentifier2 = actionSetIdentifier2;
    }

    /**
     * An always statement with a single action set restriction.
     * @param formula
     * @param actionSetIdentifier1
     * @param actionSet1
     */
    public Always(StateFormula formula, String actionSetIdentifier1, Set<String> actionSet1) {
        this(formula, actionSetIdentifier1, actionSet1, null, new HashSet<>());
    }

    public Set<String> getActions() {
        if (actionSet1 != null && actionSet2 != null) {
            Set<String> union = new HashSet<>(actionSet1);
            union.addAll(actionSet2);
            return union;
        }
        if (actionSet1 != null)
            return actionSet1;
        if (actionSet2 != null)
            return actionSet2;
        return null;
    }

    public Set<String> getActionSet1() {
        return actionSet1;
    }

    public Set<String> getActionSet2() {
        return actionSet2;
    }

    @Override
    public void writeToBuffer(StringBuilder buffer) {
        buffer.append(FormulaParser.ALWAYS_TOKEn);
        if (actionSetIdentifier1 != null && actionSetIdentifier2 != null)
            buffer.append(actionSetIdentifier1).append("\\/").append(actionSetIdentifier2);
        else if (actionSetIdentifier1 != null)
            buffer.append(actionSetIdentifier1);
        else if (actionSetIdentifier2 != null)
            buffer.append(actionSetIdentifier2);
        stateFormula.writeToBuffer(buffer);
    }

    public String getActionSetIdentifier1() {
        return actionSetIdentifier1;
    }

    public String getActionSetIdentifier2() {
        return actionSetIdentifier2;
    }
}
