package client.scenes;

import client.services.RecipeManager;
import client.utils.SortUtils;
import commons.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Controller for the left-hand recipe list.
 * Uses {@link RecipeManager} as the single source of truth for all recipe data.
 */
public class SidebarListCtrl {

    private final RecipeManager manager = RecipeManager.getInstance();
    private SortUtils sortUtils;
    private ListView<String> listView;
    private boolean removeMode = false;
    private boolean cloneMode = false;
    private java.util.function.Consumer<Recipe> onCloneRequest;

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
        // This makes a list which is automatically updated whenever the list of recipes changes.
        var recipesList = manager.getObservableRecipes();
        sortUtils = SortUtils.fromRecipeList(recipesList);
    }

    /**
     * Connects this controller to the UI ListView, and binds it to a sorted list of recipes.
     * @param lv the ListView defined in FXML
     */
    public void setListView(ListView<String> lv) {
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
        // Handle remove-mode and clone-mode clicks in a single event filter.
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {


            if (removeMode) {
                Recipe sel = manager.getObservableRecipes().get(listView.getSelectionModel().getSelectedIndex());
                if (sel == null) {
                    exitRemoveMode();
                    ev.consume();
                    return;
                }

                boolean removed = manager.removeRecipe(sel.getId());

                exitRemoveMode();

                listView.getSelectionModel().clearSelection();


                ev.consume();
                return;
            }


            if (cloneMode) {
                Recipe sel = RecipeManager.getInstance().getObservableRecipes().get(listView.getSelectionModel().getSelectedIndex());
                if (sel != null && onCloneRequest != null) {

                    onCloneRequest.accept(sel);
                }

                exitCloneMode();
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
        return manager.getRecipesSnapshot();
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

    public void setOnCloneRequest(java.util.function.Consumer<Recipe> callback) {
        this.onCloneRequest = callback;
    }

}
