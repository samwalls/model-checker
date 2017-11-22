package modelChecker.asctl;

import formula.stateFormula.*;
import model.Model;
import modelChecker.ModelChecker;

public class ASCTLModelChecker implements ModelChecker {

    @Override
    public boolean check(Model model, StateFormula constraint, StateFormula query) {
        ModelWrapper m = new ModelWrapper(model);
        ModelMarker marker = new ModelMarker(m);
        marker.mark(query);
        return marker.isModelSatisfied(query);
    }

    @Override
    public String[] getTrace() {
        // TODO Auto-generated method stub
        return null;
    }
}
