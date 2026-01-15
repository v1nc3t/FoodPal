package commons;

public record NutritionValues(double protein, 
    double fat, 
    double carbs) {
    public double getKcalPer100g() {
        return 4 * protein + 9 * fat + 4 * carbs;
    }


}
