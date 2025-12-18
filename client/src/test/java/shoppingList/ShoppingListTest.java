package shoppingList;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.shoppingList.ShoppingList;
import client.shoppingList.ShoppingListItem;

public class ShoppingListTest {

    private ShoppingList list;
    private ShoppingListItem item;

    @BeforeEach
    public void setup() {
        list = new ShoppingList();
        item = new ShoppingListItem(
            UUID.randomUUID(),
            new Amount(1.0, Unit.CUP)
        );
    }

    @Test
    public void newListIsEmpty() {
        assertNotNull(list.getItems());
        assertEquals(0, list.getItems().size());
    }

    @Test
    public void addItemAddsItem() {
        list.addItem(item);
        assertEquals(1, list.getItems().size());
        assertEquals(item, list.getItems().get(0));
    }

    @Test
    public void clearEmptiesList() {
        list.addItem(item);
        list.clear();
        assertEquals(0, list.getItems().size());
    }

    @Test
    public void toStringTest() {
        list.addItem(item);
        assertEquals("ShoppingList{items=" + list.getItems() + "}", list.toString());
    }

    @Test
    public void equalsSameObject() {
        assertEquals(list, list);
    }

    @Test
    public void equalsNull() {
        assertNotEquals(list, null);
    }

    @Test
    public void equalsDifferentClass() {
        assertNotEquals(list, "not a list");
    }

    @Test
    public void equalsSameContent() {
        ShoppingList other = new ShoppingList();
        list.addItem(item);
        other.addItem(item);
        assertEquals(list, other);
    }

    @Test
    public void equalsDifferentContent() {
        ShoppingList other = new ShoppingList();
        list.addItem(item);
        assertNotEquals(list, other);
    }

    @Test
    public void hashCodeSameForEqualLists() {
        ShoppingList other = new ShoppingList();
        list.addItem(item);
        other.addItem(item);
        assertEquals(list.hashCode(), other.hashCode());
    }

    @Test
    public void addManualItemAddsItem() {
        UUID ingredientId = UUID.randomUUID();
        Amount amount = new Amount(2.0, Unit.GRAM);

        list.addManualItem(ingredientId, amount);

        assertEquals(1, list.getItems().size());
        ShoppingListItem added = list.getItems().get(0);
        assertEquals(ingredientId, added.getIngredientId());
        assertEquals(amount, added.getAmount());
        assertNull(added.getSourceRecipeId());
    }

    @Test
    public void addRecipeItemsAddsAllIngredients() {
        RecipeIngredient ingredient1 = new RecipeIngredient(
            UUID.randomUUID(),
            new Amount(1.0, Unit.CUP)
        );

        RecipeIngredient ingredient2 = new RecipeIngredient(
            UUID.randomUUID(),
            new Amount(2.0, Unit.GRAM)
        );

        Recipe recipe = new Recipe("Test Recipe", List.of(ingredient1, ingredient2), List.of("Step1: hello"), 2,
                Language.EN);
        list.addRecipeItems(recipe);

        assertEquals(2, list.getItems().size());

        ShoppingListItem first = list.getItems().get(0);
        assertEquals(ingredient1.getIngredientRef(), first.getIngredientId());
        assertEquals(ingredient1.getAmount(), first.getAmount());
        assertEquals(recipe.getId(), first.getSourceRecipeId());
    }

}

