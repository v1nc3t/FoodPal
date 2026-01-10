package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NutritionValuesTest {

    @Test
    void caloriesAreCalculatedCorrectly() {
        NutritionValues nv = new NutritionValues(1, 1, 1);
        assertEquals(17, nv.getCalories());
    }

    @Test
    void zeroMacrosGiveZeroCalories() {
        NutritionValues nv = new NutritionValues(0, 0, 0);
        assertEquals(0, nv.getCalories());
    }

    @Test
    void caloriesAreRoundedCorrectly() {
        NutritionValues nv = new NutritionValues(0.5, 0.5, 0.5);
        assertEquals(9, nv.getCalories()); // 4*0.5 + 9*0.5 + 4*0.5 = 8.5 → rounds to 9
    }
}
