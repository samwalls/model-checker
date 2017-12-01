package formula.pathFormula;

import formula.FormulaParser;
import formula.stateFormula.*;
import java.util.Set;

public class Next extends PathFormula {
    public final StateFormula stateFormula;
    private Set<String> actions;
    private String actionSetIdentifier;

    public Next(StateFormula stateFormula, String actionSetIdentifier, Set<String> actions) {
        this.stateFormula = stateFormula;
        this.actionSetIdentifier = actionSetIdentifier;
        this.actions = actions;
    }

    public Set<String> getActions() {
        return actions;
    }

    @Override
    public void writeToBuffer(StringBuilder buffer) {
        buffer.append(FormulaParser.NEXT_TOKEN);
        stateFormula.writeToBuffer(buffer);
        if (actionSetIdentifier != null)
            buffer.append(actionSetIdentifier);
    }

    public String getActionSetIdentifier() {
        return actionSetIdentifier;
    }
}
