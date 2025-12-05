package commons;

import jakarta.persistence.Embeddable;

@Embeddable
public record Amount( double quantity,
                      Unit unit,
                      String description) {

    /**
     * Constructor for InformalAmount
     * @param quantity the quantity
     * @param description the description (F.e. "a pinch")
     */
    public Amount(double quantity, String description) {
        this(quantity, null, description);
    }

    /**
     * Constructor for FormalAmount
     * @param quantity the quantity
     * @param unit the unit (Enum)
     */
    public Amount(double quantity, Unit unit) {
        this(quantity, unit, null);
    }

    @Override
    public String toString() {
        return "Amount{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                ", description='" + description + '\'' +
                '}';
    }

    public String toPrettyString() {
        if (unit != null && description == null)
            return quantity + " " + unit.name();
        else
            return quantity + " " + description();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Amount that = (Amount) obj;
        return quantity == that.quantity &&
               ((unit == null && that.unit == null) || (unit != null && unit.equals(that.unit))) &&
               ((description == null && that.description == null) || (description != null && description.equals(that.description)));
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(quantity);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }   
}