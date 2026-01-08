package shoppingList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.UUID;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.shoppingList.ShoppingListItem;

public class ShoppingListItemTest {

    private Recipe recipe;
    private List<RecipeIngredient> ingredients;
    private List<String> steps;
    private ShoppingListItem shopListItem;
    private RecipeIngredient recIngredient;
    private Amount formalAmount;

    @BeforeEach
    public void setup() {
        NutritionValues nutritionValues1 = new NutritionValues(0.0, 0.0, 76.0);
        NutritionValues nutritionValues2 = new NutritionValues(13.0, 11.0, 1.1);
        Ingredient ingredient1 = new Ingredient("Flour", nutritionValues1);
        Ingredient ingredient2 = new Ingredient("Eggs", nutritionValues2);
        formalAmount = new Amount(2.0, Unit.CUP);
        Amount informalAmount = new Amount(3.0, "large");
        recIngredient = new RecipeIngredient(ingredient1.getId(), formalAmount);
        RecipeIngredient recIngredient2 = new RecipeIngredient(ingredient2.getId(), informalAmount);
        ingredients = List.of(recIngredient, recIngredient2);

        String step1 = "Mix ingredients.";
        String step2 = "Bake at 350 degrees for 30 minutes.";
        steps = List.of(step1, step2);

        int portions = 4;

        recipe = new Recipe("Cake", ingredients, steps, portions, Language.EN);

        shopListItem = new ShoppingListItem(recIngredient.getIngredientRef(), formalAmount, recipe.getId());
    }

    @Test
    public void ListItemCreation() {
        assertNotNull(shopListItem);
    }

    @Test
    public void ListItemCreation2() {
        ShoppingListItem shopListItem2 = new ShoppingListItem(recIngredient.getIngredientRef(), formalAmount);
        assertNotNull(shopListItem2);
    }

    @Test
    public void ListItemCreation3() {
        ShoppingListItem shopListItem2 = new ShoppingListItem();
        assertNotNull(shopListItem2);
    }

    @Test
    public void getIngredientIdTest() {
        assertEquals(recIngredient.getIngredientRef(), shopListItem.getIngredientId());
    }

    @Test
    public void getAmountTest() {
        assertEquals(formalAmount, shopListItem.getAmount());
    }

    @Test
    public void getSourceRecTest() {
        assertEquals(recipe.getId(), shopListItem.getSourceRecipeId());
    }

    @Test
    public void setAmount() {
        Amount amount2 = new Amount(3.0, "large");
        shopListItem.setAmount(amount2);
        assertEquals(amount2, shopListItem.getAmount());
    }

    @Test
    public void toStringTest() {
        assertEquals("ShoppingListItem{ingredientId=" + recIngredient.getIngredientRef() +
                ", customName='null'" +
                ", amount=Amount{quantity=2.0, unit=CUP, description='null'}, sourceRecipeId=" +
                recipe.getId() + "}", shopListItem.toString());
    }

    @Test
    public void equalsSameObject() {
        assertEquals(shopListItem, shopListItem);
    }

    @Test
    public void equalsNull() {
        assertFalse(shopListItem.equals(null));
    }

    @Test
    public void equalsDifferentClass() {
        assertEquals(false, shopListItem.equals("not a shopping item"));
    }

    @Test
    public void equalsSameValues() {
        ShoppingListItem other = new ShoppingListItem(
                recIngredient.getIngredientRef(),
                formalAmount,
                recipe.getId());

        assertEquals(shopListItem, other);
    }

    @Test
    public void equalsDifferentIngredient() {
        ShoppingListItem other = new ShoppingListItem(
                UUID.randomUUID(),
                formalAmount,
                recipe.getId());

        assertEquals(false, shopListItem.equals(other));
    }

    @Test
    public void equalsDifferentAmount() {
        Amount otherAmount = new Amount(5.0, Unit.CUP);

        ShoppingListItem other = new ShoppingListItem(
                recIngredient.getIngredientRef(),
                otherAmount,
                recipe.getId());

        assertEquals(false, shopListItem.equals(other));
    }

    @Test
    public void equalsBothSourceRecipeNull() {
        ShoppingListItem a = new ShoppingListItem(recIngredient.getIngredientRef(), formalAmount);

        ShoppingListItem b = new ShoppingListItem(recIngredient.getIngredientRef(), formalAmount);

        assertEquals(a, b);
    }

    @Test
    public void equalsOneSourceRecipeNull() {
        ShoppingListItem a = new ShoppingListItem(recIngredient.getIngredientRef(), formalAmount);

        ShoppingListItem b = new ShoppingListItem(
                recIngredient.getIngredientRef(),
                formalAmount,
                recipe.getId());

        assertEquals(false, a.equals(b));
    }

    @Test
    public void hashCodeEqualObjects() {
        ShoppingListItem other = new ShoppingListItem(
                recIngredient.getIngredientRef(),
                formalAmount,
                recipe.getId());

        assertEquals(shopListItem.hashCode(), other.hashCode());
    }

    @Test
    public void hashCodeWithNullSourceRecipe() {
        ShoppingListItem item = new ShoppingListItem(recIngredient.getIngredientRef(), formalAmount);

        assertNotNull(item.hashCode());
    }
}
