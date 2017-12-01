package modelChecker.asctl;

import formula.pathFormula.Until;
import formula.stateFormula.ForAll;
import formula.stateFormula.StateFormula;
import formula.stateFormula.ThereExists;
import model.State;
import model.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CounterexampleGenerator {

    private ModelMarker marker;

    public CounterexampleGenerator(ModelMarker marker) {
        this.marker = marker;
    }

    public List<String> makeCounterexample(StateFormula formula, StateFormula constraint) {
        StateFormula normalizedF = marker.normalize(formula);
        StateFormula normalizedConstraint = marker.normalize(constraint);
        Set<State> initialStates = marker.getModel().getStates().stream().filter(State::isInit).collect(Collectors.toSet());
        List<String> path = null;
        for (State s : initialStates) {
             path = search(s.getName(), normalizedF, normalizedConstraint, new ComputationPathNode(s.getName()), null);
            if (path != null)
                break;
        }
        if (path == null)
            return null;
        return path;
    }

    /**
     *
     * @param state
     * @param f
     * @param constraint
     * @param node
     * @return list of actions to a counterexample
     */
    private List<String> search(String state, StateFormula f, StateFormula constraint, ComputationPathNode node, String[] lastActions) {
        if (marker.isSatisfied(state, f)) {
            return null;
        } else if (f instanceof ForAll || f instanceof ThereExists) {
            return searchUntil(state, f, constraint, node, lastActions);
        } else {
            // counterexample found
            return searchReturn(node);
        }
    }

    private List<String> searchUntil(String state, StateFormula f, StateFormula constraint, ComputationPathNode node, String[] lastActions) {
        Until until = f instanceof ForAll ? (Until)(((ForAll)f).pathFormula) : (Until)(((ThereExists)f).pathFormula);
        if (!marker.isSatisfied(state, until.right) || !marker.isSatisfied(state, until.left)) {
            if (until.right instanceof ForAll || until.right instanceof ThereExists)
                return searchUntil(state, until.right, constraint, node, lastActions);
            // if left or right doesn't hold, this is the counterexample
            return searchReturn(node);
        } else if (marker.isSatisfied(state, until.left)) {
            // if left does hold, call search on all children which do not satisfy the formula
            Set<Transition> next = marker.getModel().getTransitions().stream()
                    .filter(t -> t.getSource().equals(state))
                    .filter(t -> !marker.isSatisfied(t.getTarget(), f))
                    .collect(Collectors.toSet());
            for (Transition t : next) {
                node.addChild(t.getActions(), t.getTarget());
                List<String> retValue = searchUntil(t.getTarget(), f, constraint, node.getChild(t.getActions()), t.getActions());
                // the first child path that returns a non-null value is the counterexample path
                if (retValue != null)
                    return retValue;
            }
        }
        // this is definitely not a counterexample path
        return null;
    }

    private List<String> searchReturn(ComputationPathNode node) {
        List<String> statePath = new ArrayList<>();
        while (node.getParent() != null) {
            statePath.add(node.getState());
            node = node.getParent();
        }
        Collections.reverse(statePath);
        return statePath;
    }
}
