package model;

import modelChecker.asctl.ModelWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * */
public class State {
    private boolean init;
    private String name;
    private String [] label;

    public Set<State> directPredecessors(ModelWrapper m) {
        HashSet<State> predecessors = new HashSet<>();
        // add all states for which this state is the target
        for (Transition t : m.getTransitions()) {
            if (t.getTarget().equals(name))
                predecessors.add(m.getState(t.getSource()));
        }
        return predecessors;
    }

    public Set<State> allPredecessors(ModelWrapper m) {
        // adapted from B. Berard, M. Bidoit, A. Finkel, F. Laroussinie, A. Petit, L. Petrucci,
        // and P. Schnoebelen. 2010. Systems and Software Verification: Model-Checking Techniques and Tools (1st ed.).
        // Springer Publishing Company, Incorporated.
        // section 4.1 - "Computation of Pre*(S)"
        Set<State> all = new HashSet<>();
        Set<State> pre = null;
        // while there are still predecessor states to add to the set...
        while (pre == null || pre.size() > 0) {
            // pre = the predecessors of the current set of all predecessors
            pre = all.stream().flatMap(s -> s.directPredecessors(m).stream()).collect(Collectors.toSet());
            // all = the union of the last all set, and the set of predecessors for it
            all.addAll(pre);
        }
        return all;
    }

    /**
     * Is state an initial state
     * @return boolean init 
     * */
    public boolean isInit() {
	    return init;
    }
	
    /**
     * Returns the name of the state
     * @return String name 
     * */
    public String getName() {
	    return name;
    }
	
    /**
     * Returns the labels of the state
     * @return Array of string labels
     * */
    public String[] getLabel() {
	    return label;
    }
}
