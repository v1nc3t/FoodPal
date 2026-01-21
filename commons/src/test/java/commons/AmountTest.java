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
    @Test
    public void testScaleFormalAmount() {
        Amount scaled = formalAmount.scale(2.5);

        assertEquals(5.0, scaled.quantity());
        assertEquals(Unit.CUP, scaled.unit());
    }

    @Test
    public void testScaleAndNormalizeFormalAmount() {
        Amount scaled = formalAmount.scaleAndNormalize(2.5);

        assertEquals(1.2, scaled.quantity());
        assertEquals(Unit.LITER, scaled.unit());
    }

    @Test
    public void testScaleAndNormalizeInformalAmount() {
        Amount scaled = informalAmount.scaleAndNormalize(3.0);

        assertEquals(3.0, scaled.quantity());
        assertEquals("a pinch", scaled.description());
    }

    @Test
    public void testScaleInformalAmount() {
        Amount scaled = informalAmount.scale(3.0);

        assertEquals(3.0, scaled.quantity());
        assertEquals("a pinch", scaled.description());
    }

    @Test
    public void testScaleDoesNotModifyOriginal() {
        formalAmount.scale(10.0);

        assertEquals(2.0, formalAmount.quantity());
    }
    @Test
    void gramConversionWorks() {
        Amount a = new Amount(100, Unit.GRAM);
        assertEquals(100, a.toGrams());
    }

    @Test
    void cupConversionWorks() {
        Amount a = new Amount(1, Unit.CUP);
        assertEquals(240, a.toGrams());
    }

    @Test
    void gramNormalizationWorks() {
        Amount grams = new Amount(100, Unit.GRAM);
        assertEquals(0.1, grams.toNormalizedAmount());
    }

    @Test
    void teaspoonNormalizationWorks() {
        Amount teaspoons = new Amount(50, Unit.TEASPOON);
        assertEquals(0.25, teaspoons.toNormalizedAmount());
    }

    @Test
    void gramUnitNormalizationWorks() {
        Amount grams = new Amount(100, Unit.GRAM);
        assertEquals(Unit.KILOGRAM, grams.toNormalizedUnit());
    }

    @Test
    void cupUnitNormalizationWorks() {
        Amount cups = new Amount(1, Unit.CUP);
        assertEquals(Unit.LITER, cups.toNormalizedUnit());
    }

    @Test
    void testPrettyStringRepresentationFormal() {
        String expected = "2 CUP";
        assertEquals(expected, formalAmount.toPrettyString(), "Expected a different string representation of a formal amount.");
    }

    @Test
    void testPrettyStringRepresentationInformal() {
        String expected = "1 a pinch";
        assertEquals(expected, informalAmount.toPrettyString(), "Expected a different string representation of an informal amount.");
    }

    @Test
    void testNormalizedStringRepresentationFormal() {
        String expected = "0.48 LITER";
        assertEquals(expected, formalAmount.toNormalizedString(), "Expected a different normalization of the formal amount CUP.");
    }

    @Test
    void testNormalizedStringRepresentationInformal() {
        String expected = "1 a pinch";
        assertEquals(expected, informalAmount.toNormalizedString(), "Informal amounts do not need to be normalized.");
    }
    @Test
    void informalAmountToGramsIsZero() {
        Amount informal = new Amount(5.0, "a pinch");
        assertEquals(0.0, informal.toGrams(),
                "Informal amounts should be ignored in kcal calculations");
    }

}
