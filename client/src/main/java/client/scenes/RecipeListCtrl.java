package client.scenes;

import client.services.RecipeManager;
import commons.Recipe;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Controller for the left-hand recipe list.
 * Uses {@link RecipeManager} as the single source of truth for all recipe data.
 */
public class RecipeListCtrl {

    private final RecipeManager manager = RecipeManager.getInstance();
    private ListView<Recipe> listView;
    private boolean removeMode = false;

    /**
     * Connects this controller to the UI ListView. Also applies:
     * <ul>
     *   <li>Binding of the ListView items to the RecipeManager's ObservableList</li>
     *   <li>A cell factory that displays each recipe's title</li>
     *   <li>A remove-on-click handler when in remove mode</li>
     * </ul>
     *
     * @param lv the ListView defined in FXML
     */
    public void setListView(ListView<Recipe> lv) {
        this.listView = lv;
        listView.setItems(manager.getObservableRecipes());

        listView.setCellFactory(lvv -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });

        // When in remove mode, clicking an item removes it through the manager
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {
            if (!removeMode) return;
            Recipe sel = listView.getSelectionModel().getSelectedItem();
            if (sel != null) {
                manager.removeRecipe(sel.getId());
            }
            removeMode = false;
            ev.consume();
        });
    }

    /**
     * Adds a recipe to the list using the RecipeManager.
     * Uses an optimistic add, meaning the recipe appears immediately in the UI.
     *
     * @param r the recipe to add
     */
    public void addRecipe(Recipe r) {
        if (r == null) return;
        manager.addRecipeOptimistic(r);
    }

    /**
     * Removes a recipe from the list using the RecipeManager.
     *
     * @param r the recipe to remove
     */
    public void removeRecipe(Recipe r) {
        if (r == null) return;
        manager.removeRecipe(r.getId());
    }

    /**
     * Enables remove mode.
     * The next click on a list item will remove it instead of selecting it.
     */
    public void enterRemoveMode() {
        removeMode = true;
        if (listView != null) listView.requestFocus();
    }

    /**
     * Disables remove mode without removing anything.
     */
    public void exitRemoveMode() {
        removeMode = false;
    }

    /**
     * Returns a plain Java snapshot of all recipes currently displayed.
     * Useful for testing, logging, or non-JavaFX logic.
     *
     * @return an immutable snapshot of recipes
     */
    public List<Recipe> getRecipesSnapshot() {
        return manager.getRecipesSnapshot();
    }
}
