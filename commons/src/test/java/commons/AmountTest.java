package commons;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(unit, informalAmount.unit());
    }

    @Test
    public void testFullAmountGetUnit () {
        assertEquals(unit, fullAmount.unit());
    }

    @Test
    public void testFormalAmountGetdescription() {
        assertEquals(description, formalAmount.description());
    }

    @Test
    public void testInformalAmountGetdescription() {
        assertEquals(description, informalAmount.description());
    }

    @Test
    public void testFullAmountGetdescription() {
        assertEquals(description, fullAmount.description());
    }


    @Test
    public void testEqualsSameObject() {
        assertEquals(formalAmount, formalAmount);
        assertEquals(informalAmount, informalAmount);
    }  
}
