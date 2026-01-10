package commons;

public record NutritionValues(double protein, 
    double fat, 
    double carbs) {
    public int getCalories() {

        return (int) Math.round(
                4 * protein +
                        9 * fat +
                        4 * carbs
        );
    }

}
