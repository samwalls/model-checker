package formula.stateFormula;

public abstract class StateFormula {
    public abstract void writeToBuffer(StringBuilder buffer);

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        writeToBuffer(buffer);
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        writeToBuffer(sb);
        return sb.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof StateFormula && hashCode() == other.hashCode();
    }
}
