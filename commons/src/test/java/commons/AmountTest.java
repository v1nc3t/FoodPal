package commons;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AmountTest {
    Unit unit;
    double quantity;
    String description;
    Amount formalAmount;
    Amount informalAmount;
    Amount fullAmount;

    @BeforeEach
    public void setUp() {
        unit = Unit.CUP;
        quantity = 2.0;
        description = "a cup of sugar";
        formalAmount = new Amount(quantity, unit);
        informalAmount = new Amount(quantity, description);
        fullAmount = new Amount(quantity, unit, description);
    }

    //Constructor Tests
    @Test
    public void testformalAmountCreation() {
        assertNotNull(formalAmount);
    }

    @Test
    public void testinformalAmountCreation() {
        assertNotNull(informalAmount);
    }

    @Test
    public void testFullAmountCreation() {
        assertNotNull(fullAmount);
    }

    //Getter Tests
    @Test
    public void testFormalAmountGetQuantity() {
        assertEquals(quantity, formalAmount.quantity());
    }

    @Test
    public void testInformalAmountGetQuantity() {
        assertEquals(quantity, informalAmount.quantity());
    }

    @Test
    public void testFullAmountGetQuantity() {
        assertEquals(quantity, fullAmount.quantity());
    }

    @Test
    public void testFormalAmountGetUnit () {
        assertEquals(unit, formalAmount.unit());
    }

    @Test
    public void testInformalAmountGetUnit () {
        assertEquals(null, informalAmount.unit());
    }

    @Test
    public void testFullAmountGetUnit () {
        assertEquals(unit, fullAmount.unit());
    }

    @Test
    public void testFormalAmountGetdescription() {
        assertEquals(null, formalAmount.description());
    }

    @Test
    public void testInformalAmountGetdescription() {
        assertEquals(description, informalAmount.description());
    }

    @Test
    public void testFullAmountGetdescription() {
        assertEquals(description, fullAmount.description());
    }

    //toString Tests
    @Test
    public void testFormalAmountToString() {
        String expected = "Amount{quantity=" + quantity + ", unit=" + unit + ", description='null'}";
        assertEquals(expected, formalAmount.toString());
    }

    @Test
    public void testInformalAmountToString() {
        String expected = "Amount{quantity=" + quantity + ", unit=null, description='" + description + "'}";
        assertEquals(expected, informalAmount.toString());
    }

    @Test
    public void testFullAmountToString() {
        String expected = "Amount{quantity=" + quantity + ", unit=" + unit + ", description='" + description + "'}";
        assertEquals(expected, fullAmount.toString());
    }

    //Equals Tests
    @Test
    public void testEqualsSameObjectFormal() {
        assertEquals(formalAmount, formalAmount);
    }

    @Test
    public void testEqualsSameObjectInformal() {
        assertEquals(informalAmount, informalAmount);
    }

    @Test
    public void testEqualsSameObjectFull() {
        assertEquals(fullAmount, fullAmount);
    }

    @Test
    public void testEqualsSameValuesFormal() {
        Amount anotherFormalAmount = new Amount(quantity, unit);
        assertEquals(formalAmount, anotherFormalAmount);
    }

    @Test
    public void testEqualsSameValuesInformal() {
        Amount anotherInformalAmount = new Amount(quantity, description);
        assertEquals(informalAmount, anotherInformalAmount);
    }

    @Test
    public void testEqualsSameValuesFull() {
        Amount anotherFullAmount = new Amount(quantity, unit, description);
        assertEquals(fullAmount, anotherFullAmount);
    }

    @Test
    public void testEqualsDiffQuantityFormal() {
        Amount anotherFormalAmount = new Amount(quantity + 1, unit);
        assertNotEquals(formalAmount, anotherFormalAmount);
    }

    @Test
    public void testEqualsDiffQuantityInformal() {
        Amount anotherInformalAmount = new Amount(quantity + 1, description);
        assertNotEquals(informalAmount, anotherInformalAmount);
    }

    @Test
    public void testEqualsDiffQuantityFull() {
        Amount anotherFullAmount = new Amount(quantity + 1, unit, description);
        assertNotEquals(fullAmount, anotherFullAmount);
    }

    @Test
    public void testEqualsDiffUnitFormal() {
        Amount anotherFormalAmount = new Amount(quantity, Unit.GRAM);
        assertNotEquals(formalAmount, anotherFormalAmount);
    }

    @Test
    public void testEqualsDiffUnitFull() {
        Amount anotherFullAmount = new Amount(quantity, Unit.GRAM, description);
        assertNotEquals(fullAmount, anotherFullAmount);
    }

    @Test
    public void testEqualsDiffDescriptionInformal() {
        Amount anotherInformalAmount = new Amount(quantity, "a pinch of salt");
        assertNotEquals(informalAmount, anotherInformalAmount);
    }

    @Test
    public void testEqualsDiffDescriptionFull() {
        Amount anotherFullAmount = new Amount(quantity, unit, "a pinch of salt");
        assertNotEquals(fullAmount, anotherFullAmount);
    }

    @Test
    public void testEqualsNullFormal() {
        assertNotEquals(formalAmount, null);
    }

    @Test
    public void testEqualsNullInformal() {
        assertNotEquals(informalAmount, null);
    }

    @Test
    public void testEqualsNullFull() {
        assertNotEquals(fullAmount, null);
    }

    @Test
    public void testEqualsDifferentClassFormal() {
        assertNotEquals(formalAmount, "Some String");
    }  

    @Test
    public void testEqualsDifferentClassInformal() {
        assertNotEquals(informalAmount, 123);
    }

    @Test
    public void testEqualsDifferentClassFull() {
        assertNotEquals(fullAmount, 45.67);
    }

    @Test
    public void testEqualsUnitNullVsNonNull() {
        Amount a1 = new Amount(2.0, null, "desc");
        Amount a2 = new Amount(2.0, Unit.GRAM, "desc");
        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsDescriptionNullVsNonNull() {
        Amount a1 = new Amount(2.0, Unit.GRAM, null);
        Amount a2 = new Amount(2.0, Unit.GRAM, "desc");
        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsUnitNonNullVsNull() {
        Amount a1 = new Amount(2.0, Unit.GRAM, "desc");
        Amount a2 = new Amount(2.0, null, "desc");
        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsDescriptionNonNullVsNull() {
        Amount a1 = new Amount(2.0, Unit.GRAM, "desc");
        Amount a2 = new Amount(2.0, Unit.GRAM, null);
        assertNotEquals(a1, a2);
    }

    //hashCode Tests
    @Test
    public void testHashCodeSameValuesFormal() {
        Amount anotherFormalAmount = new Amount(quantity, unit);
        assertEquals(formalAmount.hashCode(), anotherFormalAmount.hashCode());
    }

    @Test
    public void testHashCodeSameValuesInformal() {
        Amount anotherInformalAmount = new Amount(quantity, description);
        assertEquals(informalAmount.hashCode(), anotherInformalAmount.hashCode());
    }

    @Test
    public void testHashCodeSameValuesFull() {
        Amount anotherFullAmount = new Amount(quantity, unit, description);
        assertEquals(fullAmount.hashCode(), anotherFullAmount.hashCode());
    }

    @Test
    public void testHashCodeDiffValuesInformal() {
        Amount anotherInformalAmount = new Amount(quantity + 1, description);
        assertNotEquals(informalAmount.hashCode(), anotherInformalAmount.hashCode());
    }

    @Test
    public void testHashCodeDiffValuesFull() {
        Amount anotherFullAmount = new Amount(quantity + 1, unit, description);
        assertNotEquals(fullAmount.hashCode(), anotherFullAmount.hashCode());
    }



}
