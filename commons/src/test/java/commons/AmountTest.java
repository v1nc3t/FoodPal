package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AmountTest {
    Amount formalAmount;
    Amount informalAmount;

    @BeforeEach
    public void setUp() {
        formalAmount = new Amount(2.0, Unit.CUP);
        informalAmount = new Amount(1.0, "a pinch");
    }

    @Test
    public void testformalAmountCreation() {
        assertNotNull(formalAmount);
    }

    @Test
    public void testinformalAmountCreation() {
        assertNotNull(informalAmount);
    }

    @Test
    public void testGetQuantity() {
        assertEquals(2.0, formalAmount.quantity());
        assertEquals(1.0, informalAmount.quantity());
    }

    @Test
    public void testGetUnit() {
        assertEquals(Unit.CUP, formalAmount.unit());
        assertNull(informalAmount.unit());
    }

    @Test
    public void testGetDescription() {
        assertNull(formalAmount.description());
        assertEquals("a pinch", informalAmount.description());
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals(formalAmount, formalAmount);
        assertEquals(informalAmount, informalAmount);
    }  
}
