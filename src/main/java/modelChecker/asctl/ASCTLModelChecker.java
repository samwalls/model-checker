package modelChecker.asctl;

import formula.stateFormula.*;
import model.Model;
import model.Transition;
import modelChecker.ModelChecker;

import java.util.List;

public class ASCTLModelChecker implements ModelChecker {

    private List<Transition> counterexamplePath;

    @Override
    public boolean check(Model model, StateFormula constraint, StateFormula query) {
        ModelWrapper m = new ModelWrapper(model);
        ModelMarker marker = new ModelMarker(m);
        if (!marker.isModelSatisfied(query, constraint)) {
            CounterexampleGenerator generator = new CounterexampleGenerator(marker);
            counterexamplePath = generator.makeCounterexample(query, constraint);
            return false;
        }
        return true;
    }

    @Override
    public String[] getTrace() {
        // TODO turn counterexamplePath into String[]
        return null;
    }
}
