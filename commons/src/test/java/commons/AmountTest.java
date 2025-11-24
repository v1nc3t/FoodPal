package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AmountTest {
    // Tests for FormalAmount subclass
    @Test
    public void testAmountSubclassCreation() {
        Amount amount = new FormalAmount(100, Unit.GRAM);
        assertNotNull(amount);
    }

    @Test
    public void testGetQuantity() {
        FormalAmount amount = new FormalAmount(100, Unit.GRAM);
        assertEquals(100, amount.getQuantity());
    }

    @Test
    public void testGetUnit() {
        FormalAmount amount = new FormalAmount(100, Unit.GRAM);
        assertEquals(Unit.GRAM, amount.getUnit());
    }

    @Test
    public void testToString() {
        FormalAmount amount = new FormalAmount(100, Unit.GRAM);
        assertEquals("100.0 GRAM", amount.toString());
    }

    @Test
    public void testEqualsSameObject() {
        FormalAmount amount1 = new FormalAmount(100, Unit.GRAM);
        assertEquals(amount1, amount1);
    }

    @Test
    public void testEqualsNull() {
        FormalAmount amount1 = new FormalAmount(100, Unit.GRAM);
        assertFalse(amount1.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        FormalAmount amount1 = new FormalAmount(100, Unit.GRAM);
        String notAnAmount = "Not an Amount";
        assertNotEquals(amount1, notAnAmount);
    }

    @Test
    public void testEqualsSameValues() {
        FormalAmount amount1 = new FormalAmount(100, Unit.GRAM);
        FormalAmount amount2 = new FormalAmount(100, Unit.GRAM);
        assertEquals(amount1, amount2);
    }

    @Test
    public void testNotEqualsDifferentValues() {
        FormalAmount amount1 = new FormalAmount(100, Unit.GRAM);
        FormalAmount amount2 = new FormalAmount(200, Unit.GRAM);
        assertNotEquals(amount1, amount2);
    }

    @Test
    public void testNotEqualsDifferentUnits() {
        FormalAmount amount1 = new FormalAmount(100, Unit.GRAM);
        FormalAmount amount2 = new FormalAmount(100, Unit.MILLILITER);
        assertNotEquals(amount1, amount2);
    }

    @Test
    public void testHashCode() {
        FormalAmount amount1 = new FormalAmount(100, Unit.GRAM);
        FormalAmount amount2 = new FormalAmount(100, Unit.GRAM);
        assertEquals(amount1.hashCode(), amount2.hashCode());
    }

    // Tests for InformalAmount subclass
    @Test
    public void testAmountSubclassCreation2() {
        Amount amount = new InformalAmount("a pinch");
        assertNotNull(amount);
    }

    @Test
    public void testGetDescription() {
        InformalAmount amount = new InformalAmount("a pinch");
        assertEquals("a pinch", amount.getDescription());
    }

    @Test
    public void testToString2() {
        InformalAmount amount = new InformalAmount("a pinch");
        assertEquals("a pinch", amount.toString());
    } 

    @Test
    public void testEqualsSameObject2() {
        InformalAmount amount1 = new InformalAmount("a pinch");
        assertEquals(amount1, amount1);
    }

    @Test
    public void testEqualsNull2() {
        InformalAmount amount1 = new InformalAmount("a pinch");
        assertFalse(amount1.equals(null));
    }

    @Test
    public void testEqualsDifferentClass2() {
        InformalAmount amount1 = new InformalAmount("a pinch");
        String notAnAmount = "Not an Amount";
        assertNotEquals(amount1, notAnAmount);
    }

    @Test
    public void testEqualsSameValues2() {
        InformalAmount amount1 = new InformalAmount("a pinch");
        InformalAmount amount2 = new InformalAmount("a pinch");
        assertEquals(amount1, amount2);
    }

    @Test
    public void testNotEqualsDifferentValues2() {
        InformalAmount amount1 = new InformalAmount("a pinch");
        InformalAmount amount2 = new InformalAmount("a dash");
        assertNotEquals(amount1, amount2);
    }

    @Test
    public void testHashCode2() {
        InformalAmount amount1 = new InformalAmount("a pinch");
        InformalAmount amount2 = new InformalAmount("a pinch");
        assertEquals(amount1.hashCode(), amount2.hashCode());
    }    
}
