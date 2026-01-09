package client.scenes;

import client.services.RecipeManager;
import client.utils.SortUtils;
import com.google.inject.Inject;
import commons.Language;
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
public class SidebarListCtrl {

    @Inject
    private RecipeManager recipeManager;
    private SortUtils sortUtils;
    private ListView<ListObject> listView;
    private boolean removeMode = false;
    private boolean cloneMode = false;
    private boolean favouriteMode = false;
    private java.util.function.Consumer<Recipe> onCloneRequest;
    private ESidebarMode currentMode = ESidebarMode.Recipe;



    /**
     * Initializes RecipeList controller
     */
    @FXML
    public void initialize() {
        if (sortUtils == null) {
            initializeSortUtils(currentMode);
        }
    }

    /**
     * Sets the sidebar mode, if different, and reinitializes sort-utils if so
     * @param mode the mode to become
     */
    public void setSidebarMode(ESidebarMode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            initializeSortUtils(mode);
            setListViewSorted();
        }
    }

    /**
     * Gets the sidebar mode
     * @return mode that is currently set
     */
    public ESidebarMode getSidebarMode() {
        return currentMode;
    }

    /**
     * Initializes SortUtils for sorting and filtering
     */
    private void initializeSortUtils(ESidebarMode mode) {
        switch (mode) {
            case Recipe:
                initializeRecipeSortUtils();
                break;
            case Ingredient:
                initializeIngredientSortUtils();
                break;
        }
    }

    /**
     * Initializes SortUtils for sorting using Recipes from the RecipeManager
     */
    private void initializeRecipeSortUtils() {
        // This makes a list which is automatically updated whenever the list of recipes changes.
        var recipesList = recipeManager.getObservableRecipes();
        sortUtils = SortUtils.fromRecipeList(recipesList);
    }

    /**
     * Initializes SortUtils for sorting and filtering
     */
    private void initializeIngredientSortUtils() {
        // This makes a list which is automatically updated whenever the list of recipes changes.
        var recipesList = recipeManager.getObservableIngredients();
        sortUtils = SortUtils.fromIngredientList(recipesList);
    }

    /**
     * Connects this controller to the UI ListView, and binds it to a sorted list of recipes.
     * @param lv the ListView defined in FXML
     */
    public void setListView(ListView<ListObject> lv) {
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
        listView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(ListObject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    boolean fav = recipeManager.isFavourite(item.id());
                    setText((fav ? "★ " : "") + item.name());
                }

            }
        });

        // Handle remove-mode and clone-mode clicks in a single event filter.
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {
            if (removeMode) {
                ListObject sel = listView.getSelectionModel().getSelectedItem();
                if (sel == null) {
                    exitRemoveMode();
                    ev.consume();
                    return;
                }

                boolean removed = recipeManager.removeRecipe(sel.id());

                exitRemoveMode();

                listView.getSelectionModel().clearSelection();

                ev.consume();
                return;
            }


            if (cloneMode) {
                ListObject sel = listView.getSelectionModel().getSelectedItem();
                if (sel != null && onCloneRequest != null) {
                    Recipe recipe = recipeManager.getRecipe(sel.id());
                    onCloneRequest.accept(recipe);
                }

                exitCloneMode();
                listView.getSelectionModel().clearSelection();

                ev.consume();
                return;
            }
            if (favouriteMode) {
                ListObject sel = listView.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    recipeManager.toggleFavourite(sel.id());
                    listView.refresh(); // redraw star
                }

                exitFavouriteMode();
                listView.getSelectionModel().clearSelection();
                ev.consume();
                return;
            }
        });

    }

    /**
     * Binds the current list view items to the RecipeManager's ObservableList
     * (in a sorted manner through SortUtils)
     */
    private void setListViewSorted() {
        if (sortUtils == null) {
            initializeSortUtils(currentMode);
        }
        listView.setItems(sortUtils.applyFilters());
    }

    /**
     * Toggles the language filter of the provided language in SortUtils
     * @param language provided language to toggle filter of
     */
    public void toggleLanguageFilter(Language language) {
        if (sortUtils == null) {
            initializeSortUtils(currentMode);
        }

        sortUtils.toggleLanguageFilter(language);
        setListViewSorted();
    }

    /**
     * Sets SortUtils to use the specified ordering
     * @param sortMethod provided ordering manner
     */
    public void setSortMethod(String sortMethod) {
        if (sortUtils == null) {
            initializeSortUtils(currentMode);
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
        recipeManager.addRecipeOptimistic(r);
    }

    /**
     * Removes a recipe from the list using the RecipeManager.
     *
     * @param r the recipe to remove
     */
    public void removeRecipe(Recipe r) {
        if (r == null) return;
        recipeManager.removeRecipe(r.getId());
    }

    /**
     * Enables remove mode.
     * The next click on a list item will remove it instead of selecting it.
     */
    public void enterRemoveMode() {

        cloneMode = false;
        removeMode = true;
        if (listView != null) listView.requestFocus();
    }
    /**
     * Returns true if the controller is currently in remove mode.
     */
    public boolean isInRemoveMode() {
        return removeMode;
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
        return recipeManager.getRecipesSnapshot();
    }
    public void enterCloneMode() {
        removeMode = false;
        cloneMode = true;
        if (listView != null) listView.requestFocus();
    }

    public void exitCloneMode() {
        cloneMode = false;
    }

    public boolean isInCloneMode() {
        return cloneMode;
    }

    public void setOnRecipeCloneRequest(java.util.function.Consumer<Recipe> callback) {
        this.onCloneRequest = callback;
    }

    public void enterFavouriteMode() {
        removeMode = false;
        cloneMode = false;
        favouriteMode = true;
        if (listView != null) listView.requestFocus();
    }

    private void exitFavouriteMode() {
        favouriteMode = false;
    }

}
