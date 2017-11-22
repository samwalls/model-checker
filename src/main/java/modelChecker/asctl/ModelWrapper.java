package modelChecker.asctl;

import model.Model;
import model.State;
import model.Transition;

import java.util.*;

/**
 * A wrapper adding extra features for the POJO representation of a model used in parsing.
 */
public class ModelWrapper {

    private Map<String, State> states;

    private Collection<Transition> transitions;

    public ModelWrapper(Model model) {
        states = new HashMap<>();
        for (State s : model.getStates())
            states.put(s.getName(), s);
        transitions = Arrays.asList(model.getTransitions());
    }

    public State getState(String name) {
        return states.get(name);
    }

    public Collection<Transition> getTransitions() {
        return transitions;
    }

    public Collection<State> getStates() {
        return states.values();
    }
}
