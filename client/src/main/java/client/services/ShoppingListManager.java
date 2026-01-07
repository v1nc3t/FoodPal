package client.services;

import client.config.ConfigManager;
import client.shoppingList.ShoppingListItem;
import com.google.inject.Inject;
import commons.Amount;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShoppingListManager {

    private final ObservableList<ShoppingListItem> items = FXCollections.observableArrayList();
    private final ConfigManager configManager;

    @Inject
    public ShoppingListManager(ConfigManager configManager) {
        this.configManager = configManager;
        loadFromConfig();
    }

    private void loadFromConfig() {
        var saved = configManager.getConfig().getShoppingList();
        if (saved != null) {
            items.setAll(saved);
        }
    }

    private void saveToConfig() {
        // Create a copy to avoid concurrency issues if any,
        // essentially just dumping current state to config
        configManager.getConfig().setShoppingList(new ArrayList<>(items));
        configManager.save();
    }

    public ObservableList<ShoppingListItem> getItems() {
        return items;
    }

    public void addManualItem(String name, Amount amount) {
        if (name == null || name.isEmpty() || amount == null)
            return;

        ShoppingListItem item = new ShoppingListItem(name, amount);
        addItem(item);
    }

    public void addItem(ShoppingListItem item) {
        if (item == null)
            return;

        // Check if item already exists (same ingredient & source) to maybe merge
        // amounts?
        // For now, straightforward add.

        runOnFx(() -> {
            items.add(item);
            saveToConfig();
        });
    }

    public void removeItem(ShoppingListItem item) {
        runOnFx(() -> {
            items.remove(item);
            saveToConfig();
        });
    }

    public void updateItem(ShoppingListItem oldItem, ShoppingListItem newItem) {
        runOnFx(() -> {
            int index = items.indexOf(oldItem);
            if (index >= 0) {
                items.set(index, newItem);
                saveToConfig();
            }
        });
    }

    public void clear() {
        runOnFx(() -> {
            items.clear();
            saveToConfig();
        });
    }

    public void addRecipe(Recipe recipe) {
        if (recipe == null)
            return;
        List<RecipeIngredient> ingredients = recipe.getIngredients();
        if (ingredients == null)
            return;

        runOnFx(() -> {
            for (RecipeIngredient ri : ingredients) {
                ShoppingListItem item = new ShoppingListItem(
                        ri.getIngredientRef(),
                        ri.getAmount(),
                        recipe.getId());
                items.add(item);
            }
            saveToConfig();
        });
    }

    private void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }
}
