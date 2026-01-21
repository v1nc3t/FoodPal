package commons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NutritionValues(double protein, 
    double fat, 
    double carbs) {
    public double calcKcalPer100g() {
        return 4 * protein + 9 * fat + 4 * carbs;
    }


}
