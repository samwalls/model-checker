package modelChecker.asctl;

import formula.pathFormula.Always;
import formula.pathFormula.Eventually;
import formula.pathFormula.Next;
import formula.pathFormula.Until;
import formula.stateFormula.*;
import model.State;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ModelMarker {

    private ModelWrapper model;

    /**
     * A mapping from state name to a set of state formulae which are satisfied on the state.
     */
    private Map<String, Set<StateFormula>> marked;

    /**
     * A normalized version of the constraint being applied
     */
    private StateFormula constraint;

    public ModelMarker(ModelWrapper model) {
        this.model = model;
        marked = new HashMap<>();
    }

    public boolean isModelSatisfied(StateFormula f, StateFormula constraint) {
        marked = new HashMap<>();
        StateFormula normalizedF = normalize(f);
        this.constraint = normalize(constraint);
        mark(this.constraint);
        mark(normalizedF);
        // return true if all initial states are marked (under the constraint) as satisfied for the given formula
        Set<State> initialStates = model.getStates().stream().filter(State::isInit).collect(Collectors.toSet());
        boolean allMatching = true;
        for (State s : initialStates)
            allMatching = allMatching && isSatisfied(s, normalizedF);
        return initialStates.size() > 0 && allMatching;
    }

    public ModelWrapper getModel() {
        return model;
    }

    //******** STATE MARKING ********//

    /**
     * Mark on all states whether or not the formula and it's sub-formulae are satisfied.
     * @param f the formula to mark states with
     */
    private void mark(StateFormula f) {
        markHelper(f);
    }

    private void markHelper(StateFormula f) {
        // match formula types to marking sequences
        if (f instanceof BoolProp) markFor((BoolProp)f);
        else if (f instanceof AtomicProp) markFor((AtomicProp)f);
        else if (f instanceof Not) markFor((Not)f);
        else if (f instanceof And) markFor((And)f);
        else if (f instanceof Or) markFor((Or)f);
        else if (f instanceof ThereExists) markFor((ThereExists)f);
        else if (f instanceof ForAll) markFor((ForAll)f);
    }

    private void markFor(BoolProp f) {
        model.getStates().forEach(s -> setSatisfied(s, f, f.value));
    }

    private void markFor(AtomicProp f) {
        model.getStates().forEach(s -> setSatisfied(s, f, asList(s.getLabel()).contains(f.label)));
    }

    private void markFor(Not f) {
        markHelper(f.stateFormula);
        model.getStates().forEach(s -> setSatisfied(s, f, !isSatisfied(s, f.stateFormula)));
    }

    private void markFor(And f) {
        markHelper(f.left);
        markHelper(f.right);
        model.getStates().forEach(s -> setSatisfied(s, f, isSatisfied(s, f.left) && isSatisfied(s, f.right)));
    }

    private void markFor(Or f) {
        markHelper(f.left);
        markHelper(f.right);
        model.getStates().forEach(s -> setSatisfied(s, f, isSatisfied(s, f.left) || isSatisfied(s, f.right)));
    }

    private void markFor(ThereExists f) {
        if (f.pathFormula instanceof Next) {
            // of the form f = EX psi
            Next next = ((Next) f.pathFormula);
            StateFormula psi = next.stateFormula;
            markHelper(psi);
            model.getTransitions().stream()
                    // only set states as satisfied in which the subformula holds, and can be reached via the action set
                    .filter(t -> isSatisfied(t.getTarget(), psi) && (next.getActionSetIdentifier() == null || !Collections.disjoint(next.getActions(), new HashSet<>(asList(t.getActions())))))
                    .filter(t -> isSatisfied(t.getTarget(), constraint))
                    .forEach(t -> setSatisfied(t.getSource(), f, true));
        } else if (f.pathFormula instanceof Always) {
            markForThereExistsAlways(f);
        } else if (f.pathFormula instanceof Until) {
            markForThereExistsUntil(f);
        } else
            throw new IllegalArgumentException("reduction incomplete in formula: " + f.toString() + " - only forms EX p and E(p U q) are accepted");
    }

    private void markFor(ForAll f) {
        throw new IllegalArgumentException("reduction incomplete on formula: " + f.toString() + " formulae of the form A(p U q) not accepted");
    }

    private void markForThereExistsAlways(ThereExists f) {
        Always a = (Always)f.pathFormula;
        StateFormula psi = a.stateFormula;
        markHelper(psi);
        Set<State> toProcess = new HashSet<>();
        Set<String> processed = new HashSet<>();
        // add states which are reachable via the specified action sets, and satisfy psi
        model.getStates().stream()
                .filter(s -> isSatisfied(s, psi))
                .filter(s -> isSatisfied(s, constraint))
                .filter(s -> model.getTransitions().stream().anyMatch(t -> t.getTarget().equals(s.getName()) && (a.getActionsIdentifier() == null || !Collections.disjoint(a.getActions(), asList(t.getActions())))))
                .forEach(s -> {
                    setSatisfied(s, f, true);
                    processed.add(s.getName());
                    // add all predecessors which reach this state via the action sets and satisfy psi
                    Set<State> predecessors = model.getTransitions().stream()
                            .filter(t -> t.getTarget().equals(s.getName()))
                            .filter(t -> !processed.contains(t.getSource()))
                            .filter(t -> a.getActionsIdentifier() == null || !Collections.disjoint(Arrays.asList(t.getActions()), a.getActions()))
                            .map(t -> model.getState(t.getSource()))
                            .filter(predecessorState -> isSatisfied(predecessorState, constraint))
                            .filter(predecessorState -> isSatisfied(predecessorState, psi))
                            .collect(Collectors.toSet());
                    toProcess.addAll(predecessors);
                });
        while (toProcess.size() > 0) {
            State s = toProcess.iterator().next();
            toProcess.remove(s);
            setSatisfied(s, f, true);
            processed.add(s.getName());
            // add all predecessors which reach this state via the action sets and satisfy psi
            Set<State> predecessors = model.getTransitions().stream()
                    .filter(t -> t.getTarget().equals(s.getName()))
                    .filter(t -> !processed.contains(t.getSource()))
                    .filter(t -> a.getActionsIdentifier() == null || !Collections.disjoint(Arrays.asList(t.getActions()), a.getActions()))
                    .map(t -> model.getState(t.getSource()))
                    .filter(predecessorState -> isSatisfied(predecessorState, constraint))
                    .filter(predecessorState -> isSatisfied(predecessorState, psi))
                    .collect(Collectors.toSet());
            toProcess.addAll(predecessors);
        }
    }

    private void markForThereExistsUntil(ThereExists f) {
        // of the form f = E(left U right)
        Until until = (Until)f.pathFormula;
        markHelper(until.left);
        markHelper(until.right);
        Set<State> toProcess = new HashSet<>();
        Set<String> processed = new HashSet<>();
        // all states in toProcess are those deemed to satisfy the whole until statement
        model.getStates().stream()
                // only process states in which the RHS can be satisfied via an action from the right action set
                .filter(s -> isSatisfied(s, until.right))
                .filter(s -> model.getTransitions().stream().anyMatch(t -> t.getTarget().equals(s.getName()) && (until.getRightActionsIdentifier() == null || !Collections.disjoint(until.getRightActions(), asList(t.getActions())))))
                .forEach(s -> {
                    setSatisfied(s, f, true);
                    processed.add(s.getName());
                    // add all predecessors of this state (which can be accessed via actions from the right action set) which have not already been processed
                    Set<State> predecessors = model.getTransitions().stream()
                            .filter(t -> t.getTarget().equals(s.getName()))
                            .filter(t -> !processed.contains(t.getSource()))
                            .filter(t -> until.getRightActionsIdentifier() == null || !Collections.disjoint(Arrays.asList(t.getActions()), until.getRightActions()))
                            .map(t -> model.getState(t.getSource()))
                            .filter(predecessorState -> isSatisfied(predecessorState, constraint))
                            .filter(predecessorState -> isSatisfied(predecessorState, until.left))
                            .collect(Collectors.toSet());
                    toProcess.addAll(predecessors);
                });
        while (toProcess.size() > 0) {
            State s = toProcess.iterator().next();
            toProcess.remove(s);
            setSatisfied(s, f, true);
            processed.add(s.getName());
            // add all predecessors of this state (which can be accessed via actions from the left action set) which have not already been processed
            Set<State> predecessors = model.getTransitions().stream()
                    .filter(t -> t.getTarget().equals(s.getName()))
                    .filter(t -> !processed.contains(t.getSource()))
                    .filter(t -> until.getLeftActionsIdentifier() == null || !Collections.disjoint(asList(t.getActions()), until.getLeftActions()))
                    .map(t -> model.getState(t.getSource()))
                    .filter(predecessorState -> isSatisfied(predecessorState, constraint))
                    .filter(predecessorState -> isSatisfied(predecessorState, until.left))
                    .collect(Collectors.toSet());
            toProcess.addAll(predecessors);
        }
    }

    private long successorCount(State s) {
        return model.getTransitions().stream().filter(t -> t.getSource().equals(s.getName())).count();
    }

    //******** FORMULA NORMALIZATION ********//

    /**
     * Get an equivalent formula of f, in a normal form for marking purposes.
     * @param f the formula to get a normal form for
     */
    public StateFormula normalize(StateFormula f) {
        if (f instanceof BoolProp || f instanceof AtomicProp) return f;
        else if (f instanceof And) return normalize((And)f);
        else if (f instanceof Or) return normalize((Or)f);
        else if (f instanceof Not) return normalize((Not)f);
        else if (f instanceof ForAll) return normalize((ForAll)f);
        else if (f instanceof ThereExists) return normalize((ThereExists)f);
        // assume no reductions required if we reach here
        return f;
    }

    private StateFormula normalize(And f) {
        return new And(normalize(f.left), normalize(f.right));
    }

    private StateFormula normalize(Or f) {
        return new Or(normalize(f.left), normalize(f.right));
    }

    private StateFormula normalize(Not f) {
        return new Not(normalize(f.stateFormula));
    }

    private StateFormula normalize(ThereExists f) {
        if (f.pathFormula instanceof Eventually) {
            // EF p = E(T U p)
            Eventually e = (Eventually)f.pathFormula;
            return normalize(new ThereExists(new Until(new BoolProp(true), e.stateFormula, e.getLeftActionsIdentifier(), e.getLeftActions(), e.getRightActionsIdentifier(), e.getRightActions())));
        } else if (f.pathFormula instanceof Always) {
            // EG p is a minimal operator, simply normalize the parameter
            Always g = (Always)f.pathFormula;
            StateFormula p = normalize(g.stateFormula);
            return new ThereExists(new Always(p, g.getActionsIdentifier(), g.getActions()));
        } else if (f.pathFormula instanceof Until) {
            // E(p U q) is a minimal operator, simply normalize the left and right
            Until u = (Until)f.pathFormula;
            StateFormula p = normalize(u.left);
            StateFormula q = normalize(u.right);
            return new ThereExists(new Until(p, q, u.getLeftActionsIdentifier(), u.getLeftActions(), u.getRightActionsIdentifier(), u.getRightActions()));
        }
        return f;
    }

    private StateFormula normalize(ForAll f) {
        if (f.pathFormula instanceof Next) {
            // AX p = -EX -p
            Next n = (Next)f.pathFormula;
            StateFormula p = normalize(n.stateFormula);
            return new Not(new ThereExists(new Next(new Not(p), n.getActionSetIdentifier(), n.getActions())));
        } else if (f.pathFormula instanceof Eventually) {
            // AF p = -EG -p
            Eventually e = (Eventually)f.pathFormula;
            StateFormula p = normalize(e.stateFormula);
            // further reduce this
            return new Not(new ThereExists(new Always(new Not(p), e.getRightActionsIdentifier(), e.getRightActions())));
        } else if (f.pathFormula instanceof Always) {
            // AG p = -E(T U -p)
            Always g = (Always)f.pathFormula;
            StateFormula p = normalize(g.stateFormula);
            return new Not(new ThereExists(new Until(new BoolProp(true), new Not(p), g.getActionsIdentifier(), g.getActions(), g.getActionsIdentifier(), g.getActions())));
        } else if (f.pathFormula instanceof Until) {
            // A(p U q) = -(E(-q U (-p && -q)) || EG -q)
            Until until = (Until)f.pathFormula;
            StateFormula p = normalize(until.left);
            StateFormula q = normalize(until.right);
            return new Not(new Or(
                    new ThereExists(new Until(new Not(q), new And(new Not(p), new Not(q)), until.getLeftActionsIdentifier(), until.getLeftActions(), until.getRightActionsIdentifier(), until.getRightActions())),
                    normalize(new ThereExists(new Always(new Not(q), until.getRightActionsIdentifier(), until.getRightActions())))
            ));
        }
        return f;
    }

    //******** PROPERTY ACCESSORS ********//

    public boolean isSatisfied(String state, StateFormula f) {
        Set<StateFormula> satisfied = marked.get(state);
        return satisfied != null && satisfied.contains(f);
    }

    public boolean isSatisfied(State state, StateFormula f) {
        return isSatisfied(state.getName(), f);
    }

    private void setSatisfied(String state, StateFormula f, boolean satisfied) {
        marked.computeIfAbsent(state, k -> new HashSet<>());
        if (satisfied)
            marked.get(state).add(f);
        else
            marked.get(state).remove(f);
    }

    private void setSatisfied(State state, StateFormula f, boolean satisfied) {
        setSatisfied(state.getName(), f, satisfied);
    }
}
