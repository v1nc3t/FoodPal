package commons;


/** 
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = InformalAmount.class, name = "informalAmount"),
    @JsonSubTypes.Type(value = FormalAmount.class, name = "formalAmount")
})
        */

public class Amount {
    private double quantity;
    private Unit unit;
    private String description;

    public Amount(double quantity, Unit unit, String description) {
        this.quantity = quantity;
        this.unit = unit;
        this.description = description;
    }

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

    public double getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Amount{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                ", description='" + description + '\'' +
                '}';
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