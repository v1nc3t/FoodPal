package commons;

import java.util.Objects;
import java.util.UUID;

public class Ingredient {

    private UUID id;
    private String name;
    private NutritionValues nutritionValues;

    /**
     * Constructor for Ingredient class.
     * The nutrition values are provided as separate parameters. And they are all for 100g.
     * @param name the name of the ingredient
     * @param nutritionValues the nutrition values of the ingredient
     */
    public Ingredient(String name, NutritionValues nutritionValues) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.nutritionValues = nutritionValues;
    }


    /**
     * Don't use this constructor it's just for testing purposes!!!!!
     * Constructor for Ingredient class. With id. 
     * @param id the id of the ingredient
     * @param name the name of the ingredient
     * @param nutritionValues the nutrition values of the ingredient
     */
    public Ingredient(UUID id, String name, NutritionValues nutritionValues) {
        this.id = id;
        this.name = name;
        this.nutritionValues = nutritionValues;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public NutritionValues getNutritionValues() {
        return nutritionValues;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ingredient that = (Ingredient) obj;
        return id == that.id && name.equals(that.name) && nutritionValues.equals(that.nutritionValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, nutritionValues);
    }
}
