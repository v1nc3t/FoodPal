package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AmountTest {
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

    
    
}
