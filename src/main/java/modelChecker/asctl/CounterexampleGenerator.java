package modelChecker.asctl;

import formula.stateFormula.StateFormula;
import model.Transition;

import java.util.List;

public class CounterexampleGenerator {

    private ModelMarker marker;

    public CounterexampleGenerator(ModelMarker marker) {
        this.marker = marker;
    }

    public List<Transition> makeCounterexample(StateFormula formula, StateFormula constraint) {
        StateFormula normalizedF = marker.normalize(formula);
        StateFormula normalizedConstraint = marker.normalize(constraint);
        // TODO
        return null;
    }
}
