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
        assertEquals(2.0, formalAmount.getQuantity());
        assertEquals(1.0, informalAmount.getQuantity());
    }

    @Test
    public void testGetUnit() {
        assertEquals(Unit.CUP, formalAmount.getUnit());
        assertNull(informalAmount.getUnit());
    }

    @Test
    public void testGetDescription() {
        assertNull(formalAmount.getDescription());
        assertEquals("a pinch", informalAmount.getDescription());
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals(formalAmount, formalAmount);
        assertEquals(informalAmount, informalAmount);
    }  
}
