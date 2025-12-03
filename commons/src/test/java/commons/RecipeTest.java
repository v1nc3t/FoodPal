package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecipeTest {
    private Recipe recipe;
    private Recipe recipeWithId;
    private List<RecipeIngredient> ingredients;
    private List<String> steps;
    private UUID id;

    @BeforeEach
    public void setup() {
        NutritionValues nutritionValues1 = new NutritionValues(0.0, 0.0, 76.0);
        NutritionValues nutritionValues2 = new NutritionValues(13.0, 11.0, 1.1);   
        Ingredient ingredient1 = new Ingredient("Flour", nutritionValues1);
        Ingredient ingredient2 = new Ingredient("Eggs", nutritionValues2); 
        Amount formalAmount = new Amount(2.0, Unit.CUP);
        Amount informalAmount = new Amount(3.0, "large");
        RecipeIngredient RecIngredient = new RecipeIngredient(ingredient1.getId(), formalAmount);
        RecipeIngredient RecIngredient2 = new RecipeIngredient(ingredient2.getId(), informalAmount);
        ingredients = List.of(RecIngredient, RecIngredient2);

        String step1 = "Mix ingredients.";
        String step2 = "Bake at 350 degrees for 30 minutes.";
        steps = List.of(step1, step2);

        int servingSize = 4;

        recipe = new Recipe("Cake", ingredients, steps, servingSize);

        id = recipe.getId();

        recipeWithId = new Recipe(id, "Cake", ingredients, steps, servingSize);
    }

    // Constructor tests
    @Test
    void testEmptyConstructor() {
        Recipe r = new Recipe();
        assertNotNull(r);
        assertNull(r.getId());
        assertNull(r.getTitle());
        assertNull(r.getIngredients());
        assertNull(r.getSteps());
        assertEquals(0, r.getServingSize());
    }

    @Test
    void testConstructorWithoutId() {
        assertNotNull(recipe);
        assertNotNull(recipe.getId());
        assertEquals("Cake", recipe.getTitle());
        assertEquals(ingredients, recipe.getIngredients());
        assertEquals(steps, recipe.getSteps());
        assertEquals(4, recipe.getServingSize());
    }

    @Test
    void testConstructorWithId() {
        assertEquals(id, recipeWithId.getId());
        assertEquals("Cake", recipeWithId.getTitle());
        assertEquals(ingredients, recipeWithId.getIngredients());
        assertEquals(steps, recipeWithId.getSteps());
        assertEquals(4, recipeWithId.getServingSize());
    }

    // Getter tests
    @Test
    public void testGetId() {
        assertEquals(id, recipe.getId());
    }

    @Test
    public void testGetTitle() {
        assertEquals("Cake", recipe.getTitle());
    }

    @Test
    public void testGetIngredients() {
        assertEquals(ingredients, recipe.getIngredients());
    }

    @Test
    public void testGetSteps() {
        assertEquals(steps, recipe.getSteps());
    }

    @Test
    public void testGetServingSize() {
        assertEquals(4, recipe.getServingSize());
    }

    //toString Test
    @Test
    public void testToString() {
        String expectedString = "Recipe{" +
                "id=" + id +
                ", title='Cake'" +
                ", ingredients=" + ingredients.toString() +
                ", steps='" + steps.toString() + "\'" +
                ", servingSize=4" +
                '}';
        assertEquals(expectedString, recipe.toString());
    }

    //Equals Test
    @Test
    public void testEqualsSameObject() {
        assertEquals(recipe, recipe);
    }   

    @Test
    public void testEqualsNull() {
        assertNotEquals(recipe, null);
    }

    @Test
    public void testEqualsDifferentClass() {
        String notARecipe = "Not a Recipe";
        assertNotEquals(recipe, notARecipe);
    }

    @Test
    public void testEqualsSameValues() {
        Recipe anotherRecipe = new Recipe(id, "Cake", ingredients, steps, 4);
        assertEquals(recipeWithId, anotherRecipe);
    }

    @Test
    public void testNotEqualsDifferentId() {   
        Recipe anotherRecipe = new Recipe(UUID.randomUUID(), "Cake", ingredients, steps, 4);
        assertNotEquals(recipeWithId, anotherRecipe);
    }

    @Test
    public void testNotEqualsDifferentTitle() {
        Recipe anotherRecipe = new Recipe(id, "Pie", ingredients, steps, 4);
        assertNotEquals(recipeWithId, anotherRecipe);
    }

    @Test
    public void testNotEqualsDifferentIngredients() {
        Recipe anotherRecipe = new Recipe(id, "Cake", List.of(), steps, 4);
        assertNotEquals(recipeWithId, anotherRecipe);
    }

    @Test
    public void testNotEqualsDifferentSteps() {
        Recipe anotherRecipe = new Recipe(id, "Cake", ingredients, List.of(), 4);
        assertNotEquals(recipeWithId, anotherRecipe);
    }   

    @Test
    public void testNotEqualsDifferentServingSize() {
        Recipe anotherRecipe = new Recipe(id, "Cake", ingredients, steps, 2);
        assertNotEquals(recipeWithId, anotherRecipe);
    }

    //hashCode Test
    @Test
    public void testHashCode() {
        Recipe anotherRecipe = new Recipe(id, "Cake", ingredients, steps, 4);
        assertEquals(recipeWithId.hashCode(), anotherRecipe.hashCode());
    }

    


}
