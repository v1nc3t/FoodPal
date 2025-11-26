package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Recipe {

    private UUID id;
    private String title;
    private List<RecipeIngredient> ingredients;
    private List<String> steps;
    private int servingSize;
    
    public Recipe(String title, List<RecipeIngredient> ingredients, List<String> steps, int servingSize) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servingSize = servingSize;
    }

    /**
     * Constructor for Recipe class. With an already specified id.
     * @param id the id of the recipe
     * @param title the name of the recipe
     * @param ingredients the ingredients of the recipe
     * @param steps the steps of the recipe
     * @param servingSize the serving size of the recipe
     */
    @JsonCreator
    public Recipe(@JsonProperty("id") UUID id,
                  @JsonProperty("title") String title,
                  @JsonProperty("ingredients") List<RecipeIngredient> ingredients,
                  @JsonProperty("steps") List<String> steps,
                  @JsonProperty("servingSize") int servingSize) {
        this.id = id;
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servingSize = servingSize;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public int getServingSize() {
        return servingSize;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", ingredients=" + ingredients +
                ", steps='" + steps + '\'' +
                ", servingSize=" + servingSize +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recipe that = (Recipe) obj;
        return id == that.id &&
               servingSize == that.servingSize &&
               title.equals(that.title) &&
               ingredients.equals(that.ingredients) &&
               steps.equals(that.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, ingredients, steps, servingSize);
    }
}
