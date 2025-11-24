package commons;

public class InformalAmount extends Amount {
    private String description;

    public InformalAmount(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InformalAmount that = (InformalAmount) obj;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
