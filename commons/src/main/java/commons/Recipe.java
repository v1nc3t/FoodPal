package commons;

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
     * Don't use this constructor it's just for testing purposes!!!!!
     * Constructor for Recipe class. With id. 
     * @param id the id of the recipe
     * @param title the name of the recipe
     * @param ingredients the ingredients of the recipe
     * @param steps the steps of the recipe
     * @param servingSize the serving size of the recipe
     */
    public Recipe(UUID id, String title, List<RecipeIngredient> ingredients, List<String> steps, int servingSize) {
        this.id = id;
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servingSize = servingSize;
    }


    public UUID getId() {
        return id;
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
