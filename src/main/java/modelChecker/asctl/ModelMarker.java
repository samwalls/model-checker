package modelChecker.asctl;

import formula.pathFormula.Always;
import formula.pathFormula.Eventually;
import formula.pathFormula.Next;
import formula.pathFormula.Until;
import formula.stateFormula.*;
import model.State;

import java.util.*;
import java.util.stream.Collectors;

public class ModelMarker {

    private ModelWrapper model;

    /**
     * A mapping from state name to a set of state formulae which are satisfied on the state.
     */
    private Map<String, Set<StateFormula>> marked;

    public ModelMarker(ModelWrapper model) {
        this.model = model;
        marked = new HashMap<>();
    }

    public boolean isModelSatisfied(StateFormula f, StateFormula constraint) {
        marked = new HashMap<>();
        StateFormula normalizedF = normalize(f);
        StateFormula normalizedConstraint = normalize(constraint);
        mark(normalizedF);
        mark(normalizedConstraint);
        // return true if all initial states are marked (under the constraint) as satisfied for the given formula
        Set<State> initialStates = model.getStates().stream().filter(State::isInit).collect(Collectors.toSet());
        boolean allMatching = true;
        for (State s : initialStates)
            allMatching = allMatching && isSatisfied(s, normalizedF);
        return initialStates.size() > 0 && allMatching;
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
        model.getStates().forEach(s -> setSatisfied(s, f, Arrays.asList(s.getLabel()).contains(f.label)));
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
            Next next = ((Next)f.pathFormula);
            StateFormula psi = next.stateFormula;
            markHelper(psi);
            model.getTransitions().stream()
                    .filter(t -> isSatisfied(t.getTarget(), psi) && !Collections.disjoint(next.getActions(), new HashSet<>(Arrays.asList(t.getActions()))))
                    .forEach(t -> setSatisfied(t.getSource(), f, true));
        } else if (f.pathFormula instanceof Until) {
            // of the form f = E(left U right)
            Until until = (Until)f.pathFormula;
            markHelper(until.left);
            markHelper(until.right);
            Set<String> seen = new HashSet<>();
            // all states in toProcess are those deemed to satisfy the whole until statement
            Set<State> toProcess = model.getStates().stream().filter(s -> isSatisfied(s, until.right)).collect(Collectors.toSet());
            while (toProcess.size() > 0) {
                State s = toProcess.iterator().next();
                toProcess.remove(s);
                setSatisfied(s, f, true);
                model.getTransitions().stream()
                        // if we've not seen this predecessor before
                        .filter(t -> !seen.contains(t.getSource()))
                        .forEach(t -> {
                            String pre = t.getSource();
                            seen.add(pre);
                            // add the predecessor to the list of states to be processed as long as it satisfies the
                            // LHS of the until statement
                            if (isSatisfied(pre, until.left))
                                toProcess.add(model.getState(pre));
                        });
            }
        } else
            throw new IllegalArgumentException("reduction incomplete in formula: " + f.toString() + " - only forms EX p and E(p U q) are accepted");
    }

    private void markFor(ForAll f) {
        if (f.pathFormula instanceof Until) {
            // of the form f = A(left U right)
            Until until = (Until)f.pathFormula;
            markHelper(until.left);
            markHelper(until.right);
            Map<String, Long> nSuccessors = new HashMap<>();
            model.getStates().forEach(s -> nSuccessors.put(s.getName(), successorCount(s)));
            // all states in toProcess are those deemed to satisfy the whole until statement
            Set<State> toProcess = model.getStates().stream()
                    .filter(s -> isSatisfied(s, until.right))
                    .filter(s -> {
                        // only process states in which the RHS can be satisfied via a an action from the right action set
                        return model.getTransitions().stream().anyMatch(t -> t.getTarget().equals(s.getName()) && !Collections.disjoint(until.getRightActions(), Arrays.asList(t.getActions())));
                    })
                    .collect(Collectors.toSet());
            while (toProcess.size() > 0) {
                State s = toProcess.iterator().next();
                toProcess.remove(s);
                setSatisfied(s, f, true);
                model.getTransitions().forEach(t -> {
                    // decrement the number of successors to process
                    nSuccessors.put(s.getName(), nSuccessors.get(s.getName()) - 1);
                    String pre = t.getSource();
                    boolean successorReachableViaLeftActions = t.getTarget().equals(s.getName()) && !Collections.disjoint(until.getLeftActions(), Arrays.asList(t.getActions()));
                    if (nSuccessors.get(s.getName()) <= 0 && isSatisfied(pre, until.left) && !isSatisfied(pre, f))
                        toProcess.add(model.getState(pre));
                });
            }
        } else
            throw new IllegalArgumentException("reduction incomplete on formula: " + f.toString() + " - only form A(p U q) is accepted");
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
        // TODO create proper action sets for reductions
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
            StateFormula p = e.stateFormula;
            return new ThereExists(new Until(new BoolProp(true), p, e.getLeftActionsIdentifier(), e.getLeftActions(), e.getRightActionsIdentifier(), e.getRightActions()));
        } else if (f.pathFormula instanceof Always) {
            // EG p = -AF(-p)
            Always g = (Always)f.pathFormula;
            StateFormula p = normalize(g.stateFormula);
            return new Not(normalize(new ForAll(new Eventually(new Not(p), g.getActionsIdentifier(), g.getActions(), g.getActionsIdentifier(), g.getActions()))));
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

    private StateFormula normalize(ForAll f) {
        if (f.pathFormula instanceof Eventually) {
            // AF p = A(T U p)
            Eventually e = (Eventually)f.pathFormula;
            StateFormula p = normalize(e.stateFormula);
            return new ForAll(new Until(new BoolProp(true), p, e.getLeftActionsIdentifier(), e.getLeftActions(), e.getRightActionsIdentifier(), e.getRightActions()));
        } else if (f.pathFormula instanceof Always) {
            // AG p = -EF(-p)
            Always g = (Always)f.pathFormula;
            StateFormula p = normalize(g.stateFormula);
            return new Not(normalize(new ThereExists(new Eventually(new Not(p), g.getActionsIdentifier(), g.getActions(), g.getActionsIdentifier(), g.getActions()))));
        } else if (f.pathFormula instanceof Until) {
            // normalize the left and right
            Until u = (Until)f.pathFormula;
            StateFormula p = normalize(u.left);
            StateFormula q = normalize(u.right);
            return new ForAll(new Until(p, q, u.getLeftActionsIdentifier(), u.getLeftActions(), u.getRightActionsIdentifier(), u.getRightActions()));
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
