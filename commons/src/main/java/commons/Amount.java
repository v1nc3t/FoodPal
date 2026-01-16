package commons;

import jakarta.persistence.Embeddable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
        DecimalFormat df = new DecimalFormat("0.###",
                DecimalFormatSymbols.getInstance(Locale.ROOT));
        if (unit != null && description == null)
            return df.format(quantity) + " " + unit.name();
        else
            return df.format(quantity) + " " + description;
    }

    /**
     * Returns a pretty string representation of the amount, normalized in kilograms or liters.
     * @return normalized (kg or L) human-readable string of the amount
     */
    public String toNormalizedString() {
        DecimalFormat df = new DecimalFormat("0.###",
                DecimalFormatSymbols.getInstance(Locale.ROOT));
        if (toNormalizedUnit() != null && description == null)
            return df.format(toNormalizedAmount()) + " " + toNormalizedUnit().name();
        else
            return df.format(quantity) + " " + description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Amount that = (Amount) obj;
        return quantity == that.quantity &&
               ((unit == null && that.unit == null) || (unit != null && unit.equals(that.unit))) &&
               ((description == null && that.description == null)
                       || (description != null && description.equals(that.description)));
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(quantity);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
    public Amount scale(double factor) {
        return new Amount(quantity * factor, unit, description);
    }
    public Amount scaleAndNormalize(double factor) {
        return new Amount(toNormalizedAmount() * factor, toNormalizedUnit(), description);
    }
    public double toGrams() {
        if (unit == null) {
            return quantity;
        }

        return switch (unit) {
            case GRAM -> quantity;
            case LITER -> quantity * 1000;
            case MILLILITER -> quantity;
            case TABLESPOON -> quantity * 15;
            case TEASPOON -> quantity * 5;
            case CUP -> quantity * 240;
            case KILOGRAM -> quantity * 1000;
        };
    }

    /**
     * Returns the amount in normalized (kilograms or liters) units.
     * @return the amount in kilograms or liters
     */
    public double toNormalizedAmount() {
        if (unit == null) {
            return quantity;
        }

        return switch (unit) {
            case GRAM -> quantity / 1000;
            case LITER -> quantity;
            case MILLILITER -> quantity / 1000;
            case TABLESPOON -> (quantity * 15) / 1000;
            case TEASPOON -> (quantity * 5) / 1000;
            case CUP -> (quantity * 240) / 1000;
            case KILOGRAM -> quantity;
        };
    }

    /**
     * Returns the normalized unit (kilograms or liters) of the amount unit.
     * @return {@link Unit#KILOGRAM} if the unit is {@link Unit#GRAM} or {@link Unit#KILOGRAM},
     * {@link Unit#LITER} otherwise.
     */
    public Unit toNormalizedUnit() {
        if (unit == null) return null;
        if (unit == Unit.GRAM || unit == Unit.KILOGRAM) return Unit.KILOGRAM;
        return Unit.LITER;
    }
}