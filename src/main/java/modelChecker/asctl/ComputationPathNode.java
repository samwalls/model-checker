package modelChecker.asctl;

import model.State;

import java.util.*;

public class ComputationPathNode {

    private String state;

    private ComputationPathNode parent;

    private Map<Set<String>, ComputationPathNode> children;

    public ComputationPathNode(String state) {
        this.state = state;
        children = new HashMap<>();
    }

    public ComputationPathNode addChild(Set<String> actions, String next) {
        ComputationPathNode newNode = new ComputationPathNode(next);
        newNode.parent = this;
        children.put(actions, newNode);
        return newNode;
    }

    public ComputationPathNode addChild(String[] actions, String next) {
        return addChild(new HashSet<>(Arrays.asList(actions)), next);
    }

    public ComputationPathNode getChild(Set<String> actions) {
        return children.get(actions);
    }

    public ComputationPathNode getChild(String[] actions) {
        return getChild(new HashSet<>(Arrays.asList(actions)));
    }

    public ComputationPathNode getParent() {
        return parent;
    }

    public String getState() {
        return state;
    }
}
