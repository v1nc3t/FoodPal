package commons;

import java.util.Objects;

public class FormalAmount extends Amount {
    private double quantity;
    private Unit unit;
    
    public FormalAmount(double quantity, Unit unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return quantity + " " + unit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FormalAmount that = (FormalAmount) obj;
        return Double.compare(that.quantity, quantity) == 0 && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, unit);
    }
}
