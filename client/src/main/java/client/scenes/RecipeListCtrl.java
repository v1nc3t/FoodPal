package client.scenes;

import client.services.RecipeManager;
import client.utils.SortUtils;
import commons.Recipe;
import javafx.fxml.FXML;
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
    private SortUtils sortUtils;
    private ListView<Recipe> listView;
    private boolean removeMode = false;

    /**
     * Initializes RecipeList controller
     */
    @FXML
    public void initialize() {
        if (sortUtils == null) {
            initializeSortUtils();
        }
    }

    /**
     * Initializes SortUtils for sorting and filtering
     */
    private void initializeSortUtils() {
        sortUtils = new SortUtils(manager);
    }

    /**
     * Connects this controller to the UI ListView, and binds it to a sorted list of recipes.
     * @param lv the ListView defined in FXML
     */
    public void setListView(ListView<Recipe> lv) {
        this.listView = lv;

        setListViewSorted();

        loadListViewProperties();
    }

    /**
     * Applies functional properties to the UI ListView:
     * <ul>
     *  <li>A cell factory that displays each recipe's title.</li>
     *  <li>A remove on-click handler when in remove mode.</li>
     * </ul>
     */
    private void loadListViewProperties() {
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
     * Binds the current list view items to the RecipeManager's ObservableList
     * (in a sorted manner through SortUtils)
     */
    private void setListViewSorted() {
        if (sortUtils == null) {
            initializeSortUtils();
        }
        listView.setItems(sortUtils.applyFilters());
    }

    /**
     * Sets SortUtils to use the specified ordering
     * @param sortMethod provided ordering manner
     */
    public void setSortMethod(String sortMethod) {
        if (sortUtils == null) {
            initializeSortUtils();
        }

        sortUtils.setSortMethod(sortMethod);
        setListViewSorted();
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
