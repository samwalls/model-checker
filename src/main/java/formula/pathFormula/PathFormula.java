package formula.pathFormula;

public abstract class PathFormula {
    public abstract void writeToBuffer(StringBuilder buffer);

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        writeToBuffer(sb);
        return sb.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof PathFormula && hashCode() == other.hashCode();
    }
}
