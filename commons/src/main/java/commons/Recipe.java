package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Recipe entity shared between client and server.
 */
@Entity
public class Recipe {
    @Id
    public UUID id;
    public String title;

    @ElementCollection(fetch = FetchType.EAGER)
    public List<RecipeIngredient> ingredients;

    public List<String> steps;
    public int portions;
    public Language language;

    public Recipe(String title, List<RecipeIngredient> ingredients,
                  List<String> steps, int portions, Language language) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.portions = portions;
        this.language = language;
    }

    /**
     * Constructor for Recipe class with an already specified id.
     *
     * @param id          the id of the recipe
     * @param title       the name of the recipe
     * @param ingredients the ingredients of the recipe
     * @param steps       the steps of the recipe
     * @param portions    the portions of the recipe
     * @param language    the language of the recipe
     */
    @JsonCreator
    public Recipe(@JsonProperty("id") UUID id,
                  @JsonProperty("title") String title,
                  @JsonProperty("ingredients") List<RecipeIngredient> ingredients,
                  @JsonProperty("steps") List<String> steps,
                  @JsonProperty("portions") int portions,
                  @JsonProperty("language") Language language) {
        this.id = id;
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.portions = portions;
        this.language = language;
    }

    // an empty constructor for object mapper
    public Recipe() {
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

    public int getPortions() {
        return portions;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", ingredients=" + ingredients +
                ", steps='" + steps + '\'' +
                ", portions=" + portions +
                ", language=" + language.proper() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recipe that = (Recipe) obj;
        return id.equals(that.id) &&
                portions == that.portions &&
                title.equals(that.title) &&
                Objects.equals(ingredients, that.ingredients) &&
                Objects.equals(steps, that.steps) &&
                Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, ingredients, steps, portions, language);
    }

    /**
     * Create a clone of this recipe.
     * <p>
     * - Generates a new UUID for the clone.
     * - Copies all {@link RecipeIngredient} wrappers
     * - Copies the steps list.
     *
     * @return a new {@link Recipe} instance that is a clone of this recipe
     */
    public Recipe cloneRecipe() {

        List<RecipeIngredient> ingrCopy = null;
        if (this.ingredients != null) {
            ingrCopy = this.ingredients.stream()
                    .map(RecipeIngredient::new)
                    .collect(Collectors.toList());
        }

        List<String> stepsCopy = (this.steps == null)
                ? null
                : new ArrayList<>(this.steps);

        return new Recipe(
                UUID.randomUUID(),
                this.title,
                ingrCopy,
                stepsCopy,
                this.portions,
                this.language
        );
    }


    /**
     * Clone this recipe and set a new title on the clone.
     *
     * @param newTitle the title to set on the cloned recipe
     * @return a cloned {@link Recipe} with {@code newTitle}
     */
    public Recipe cloneWithTitle(String newTitle) {
        Recipe cloned = this.cloneRecipe();

        cloned.title = newTitle;
        return cloned;
    }

    public Recipe scaleToPortions(int newPortions) {
        if (newPortions <= 0 || this.portions <= 0) {
            return this;
        }

        double factor = (double) newPortions / this.portions;

        List<RecipeIngredient> scaledIngredients = ingredients.stream()
                .map(ri -> ri.scale(factor))
                .collect(Collectors.toList());

        return new Recipe(
                this.id,
                this.title,
                scaledIngredients,
                new ArrayList<>(this.steps),
                newPortions,
                this.language
        );
    }
    public double calcTotalCalories(java.util.function.Function<UUID, Ingredient> ingredientResolver) {
        double total = 0.0;

        if (ingredients == null) return 0.0;

        for (RecipeIngredient ri : ingredients) {
            Ingredient ingredient = ingredientResolver.apply(ri.getIngredientRef());
            if (ingredient == null) continue;

            double grams = ri.getGrams();
            double kcalPer100g = ingredient.getNutritionValues().calcKcalPer100g();

            total += (grams / 100.0) * kcalPer100g;
        }

        return total;
    }

    public double calcCaloriesPerPortion(java.util.function.Function<UUID, Ingredient> ingredientResolver) {
        if (portions <= 0) return 0.0;
        return calcTotalCalories(ingredientResolver) / portions;
    }
    public double calcTotalWeightInGrams() {
        if (ingredients == null) return 0.0;

        return ingredients.stream()
                .mapToDouble(ri -> ri.getAmount().toGrams())
                .sum();
    }
    public double calcKcalPer100g(Function<UUID, Ingredient> ingredientResolver) {
        double totalGrams = calcTotalWeightInGrams();
        if (totalGrams <= 0) return 0.0;

        double totalKcal = calcTotalCalories(ingredientResolver);
        return (totalKcal / totalGrams) * 100.0;
    }



}
