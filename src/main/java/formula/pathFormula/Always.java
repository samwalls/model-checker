package formula.pathFormula;

import formula.FormulaParser;
import formula.stateFormula.*;
import java.util.*;

public class Always extends PathFormula {
    public final StateFormula stateFormula;
    private Set<String> actions = new HashSet<String>();
    private String actionsIdentifier;

    public Always(StateFormula stateFormula, String actionsIdentifier, Set<String> actions) {
        this.stateFormula = stateFormula;
        this.actionsIdentifier = actionsIdentifier;
        this.actions = actions;
    }

    public Set<String> getActions() {
        return actions;
    }

    @Override
    public void writeToBuffer(StringBuilder buffer) {
        buffer.append(FormulaParser.ALWAYS_TOKEn);
        if (actionsIdentifier != null)
            buffer.append(actionsIdentifier);
        stateFormula.writeToBuffer(buffer);
        ;

    }

    public String getActionsIdentifier() {
        return actionsIdentifier;
    }
}
