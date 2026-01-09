package client.scenes;

import client.config.ConfigManager;
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
import java.util.Set;
import java.util.UUID;

/**
 * Controller for the left-hand recipe list.
 * Uses {@link RecipeManager} as the single source of truth for all recipe data.
 */
public class SidebarListCtrl {

    @Inject
    private RecipeManager recipeManager;
    @Inject
    private ConfigManager configManager;
    private SortUtils recipeSortUtils;
    private SortUtils ingredientsSortUtils;
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
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }
    }

    /**
     * Sets the sidebar mode, if different, and resets the list view
     * @param mode the mode to become
     */
    public void setSidebarMode(ESidebarMode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            setListViewSorted();
        }
    }

    /**
     * Initializes SortUtils for sorting and filtering
     */
    private void initializeSortUtils() {
        if (recipeSortUtils == null) initializeRecipeSortUtils();
        if (ingredientsSortUtils == null) initializeIngredientSortUtils();
    }

    /**
     * Initializes SortUtils for sorting using Recipes from the RecipeManager
     */
    private void initializeRecipeSortUtils() {
        // This makes a list which is automatically updated whenever the list of recipes changes.
        var recipesList = recipeManager.getObservableRecipes();
        recipeSortUtils = SortUtils.fromRecipeList(recipesList);
    }

    /**
     * Initializes SortUtils for sorting and filtering
     */
    private void initializeIngredientSortUtils() {
        // This makes a list which is automatically updated whenever the list of recipes changes.
        var ingredientsList = recipeManager.getObservableIngredients();
        ingredientsSortUtils = SortUtils.fromIngredientList(ingredientsList);
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
                    updateFavourites(recipeManager.getFavouriteRecipesSnapshot());
                }

                exitFavouriteMode();
                listView.getSelectionModel().clearSelection();
                ev.consume();
                return;
            }
        });

    }

    /**
     * Updates the given list of favourite recipe UUIDs to sortUtils
     * and applies filters to the list view, in case any relevant changes were made.
     * Also updates the config to the latest list of favourites.
     * @param favouriteRecipes list of favourite recipe UUIDs
     */
    public void updateFavourites(Set<UUID> favouriteRecipes) {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.setFavourites(favouriteRecipes.stream().toList());

        setListViewSorted();

        configManager.getConfig().setFavoriteRecipeIDs(favouriteRecipes.stream().toList());
        configManager.save();
    }

    /**
     * Binds the current list view items to the RecipeManager's ObservableList
     * (in a sorted manner through SortUtils)
     */
    private void setListViewSorted() {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        switch (currentMode) {
            case Recipe:
                listView.setItems(recipeSortUtils.applyFilters());
                break;
            case Ingredient:
                listView.setItems(ingredientsSortUtils.applyFilters());
                break;
        }
    }

    /**
     * Toggles the language filter of the provided language in SortUtils
     * @param language provided language to toggle filter of
     */
    public void toggleLanguageFilter(Language language) {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.toggleLanguageFilter(language);
        ingredientsSortUtils.toggleLanguageFilter(language);

        setListViewSorted();
    }

    /**
     * Sets SortUtils to use the specified ordering
     * @param sortMethod provided ordering manner
     */
    public void setSortMethod(String sortMethod) {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.setSortMethod(sortMethod);
        ingredientsSortUtils.setSortMethod(sortMethod);

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

    /**
     * Toggles whether sortUtils filters only favourites.
     */
    public void toggleOnlyFavourites() {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.setOnlyFavourites(!recipeSortUtils.isOnlyFavourites());

        setListViewSorted();
    }
}
