package commons;

import java.util.List;

public class Recipe {

    private int id;
    private String title;
    private List<RecipeIngredient> ingredients;
    private String steps;
    private int servingSize;
    static int recipeCount = 0;
    
    public Recipe(String title, List<RecipeIngredient> ingredients, String steps, int servingSize) {
        this.id = recipeCount++;
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servingSize = servingSize;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public String getSteps() {
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
        return java.util.Objects.hash(id, title, ingredients, steps, servingSize);
    }
}
