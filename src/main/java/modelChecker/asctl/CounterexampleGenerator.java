package modelChecker.asctl;

import formula.pathFormula.Always;
import formula.pathFormula.Next;
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
        List<String> path;
        for (State s : initialStates) {
             path = search(s.getName(), normalizedF, normalizedConstraint, new ComputationPathNode(s.getName()));
            if (path != null)
                return path;
        }
        return null;
    }

    /**
     *
     * @param state
     * @param f
     * @param constraint
     * @param node
     * @return list of actions to a counterexample
     */
    private List<String> search(String state, StateFormula f, StateFormula constraint, ComputationPathNode node) {
        if (marker.isSatisfied(state, f)) {
            // this is not a counterexample
            return null;
        } else if (f instanceof ThereExists) {
            if (((ThereExists) f).pathFormula instanceof Next)
                return searchThereExistsNext(state, f, constraint, node);
            else if (((ThereExists) f).pathFormula instanceof Always)
                return searchThereExistsAlways(state, f, constraint, node);
            else if (((ThereExists) f).pathFormula instanceof Until)
                return searchThereExistsUntil(state, f, constraint, node);
            return searchReturn(node);
        } else {
            // counterexample found
            return searchReturn(node);
        }
    }

    private List<String> searchThereExistsNext(String state, StateFormula f, StateFormula constraint, ComputationPathNode node) {
        if (!marker.isSatisfied(state, f)) {
            // this is added to the counterexample path if the formula does not hold
            Set<Transition> next = marker.getModel().getTransitions().stream()
                    .filter(t -> t.getSource().equals(state))
                    .filter(t -> !marker.isSatisfied(t.getTarget(), ((Next)((ThereExists)f).pathFormula).stateFormula))
                    .filter(t -> marker.isSatisfied(t.getTarget(), constraint))
                    .collect(Collectors.toSet());
            return firstExample(next, state, f, constraint, node);
        }
        // this is definitely not a counterexample path
        return null;
    }

    private List<String> searchThereExistsAlways(String state, StateFormula f, StateFormula constraint, ComputationPathNode node) {
        if (!marker.isSatisfied(state, f)) {
            // this is added to the counterexample path if the formula does not hold
            Set<Transition> next = marker.getModel().getTransitions().stream()
                    .filter(t -> t.getSource().equals(state))
                    .filter(t -> !marker.isSatisfied(t.getTarget(), f))
                    .filter(t -> marker.isSatisfied(t.getTarget(), constraint))
                    .collect(Collectors.toSet());
            return firstExample(next, state, f, constraint, node);
        }
        // this is definitely not a counterexample path
        return null;
    }

    private List<String> searchThereExistsUntil(String state, StateFormula f, StateFormula constraint, ComputationPathNode node) {
        Until until = f instanceof ForAll ? (Until)(((ForAll)f).pathFormula) : (Until)(((ThereExists)f).pathFormula);
        boolean leftSatisfied = marker.isSatisfied(state, until.left);
        boolean rightSatisfied = marker.isSatisfied(state, until.right);
        if (!leftSatisfied && !rightSatisfied) {
            // if left and right both do not hold, this is the counterexample
            return searchReturn(node);
        } else if (leftSatisfied && !rightSatisfied) {
            // left _does_ hold, call search on all children which do not satisfy the formula (and _do_ satisfy the constraint)
            Set<Transition> next = marker.getModel().getTransitions().stream()
                    .filter(t -> t.getSource().equals(state))
                    .filter(t -> !marker.isSatisfied(t.getTarget(), f))
                    .filter(t -> marker.isSatisfied(t.getTarget(), constraint))
                    .collect(Collectors.toSet());
            return firstExample(next, state, f, constraint, node);
        }
        // this is definitely not a counterexample path
        return null;
    }

    private List<String> firstExample(Set<Transition> transitions, String state, StateFormula f, StateFormula constraint, ComputationPathNode node) {
        if (transitions.size() <= 0)
            return searchReturn(node);
        for (Transition t : transitions) {
            ComputationPathNode child = node.addChild(t.getActions(), t.getTarget());
            List<String> retValue = search(t.getTarget(), f, constraint, child);
            // the first child path that returns a non-null value is the counterexample path
            if (retValue != null)
                return retValue;
        }
        // if we reach here, no child could provide a counterexample, this is not a counterexample
        return null;
    }

    /**
     * Called when the recursive searching has ended, constructs a list of states based on the given ComputationPathNode tree
     * @param node
     * @return
     */
    private List<String> searchReturn(ComputationPathNode node) {
        List<String> statePath = new ArrayList<>();
        while (node != null) {
            statePath.add(node.getState());
            node = node.getParent();
        }
        Collections.reverse(statePath);
        return statePath;
    }
}
