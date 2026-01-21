package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NutritionValuesTest {

    @Test
    void caloriesPer100gCalculatedCorrectly() {
        NutritionValues nv = new NutritionValues(1, 1, 1);
        assertEquals(17.0, nv.calcKcalPer100g(), 0.001);
    }

    @Test
    void fractionalMacrosProduceFractionalCalories() {
        NutritionValues nv = new NutritionValues(0.5, 0.5, 0.5);
        assertEquals(8.5, nv.calcKcalPer100g(), 0.001);
    }

}
