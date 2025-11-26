package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
public class Ingredient {
    @Id
    public UUID id;
    public String name;
    @Embedded
    public NutritionValues nutritionValues;

    /**
     * Constructor for Ingredient class. With an already specified id.
     * @param id the id of the ingredient
     * @param name the name of the ingredient
     * @param nutritionValues the nutrition values of the ingredient
     */
    @JsonCreator
    public Ingredient(@JsonProperty("id") UUID id,
                      @JsonProperty("name") String name,
                      @JsonProperty("nutritionValues") NutritionValues nutritionValues) {
        this.id = id;
        this.name = name;
        this.nutritionValues = nutritionValues;
    }

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
     * Constructor for Ingredient that is used by db-to-object mapping
     */
    public Ingredient() {
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
