package client.scenes;

import commons.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the left-hand recipe list.
 */
public class RecipeListCtrl {

    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    private ListView<Recipe> listView;
    private boolean removeMode = false;

    public void setListView(ListView<Recipe> lv) {
        this.listView = lv;
        listView.setItems(recipes);

        listView.setCellFactory(lvv -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });

        // when in remove mode, clicking a list item removes it
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {
            if (!removeMode) return;
            Recipe sel = listView.getSelectionModel().getSelectedItem();
            if (sel != null) {
                recipes.remove(sel);
            }
            removeMode = false;
            ev.consume();
        });
    }

    public void addRecipe(Recipe r) {
        if (r == null) return;
        recipes.add(r);
    }

    public void removeRecipe(Recipe r) {
        recipes.remove(r);
    }

    public void enterRemoveMode() {
        removeMode = true;
        if (listView != null) listView.requestFocus();
    }

    public void exitRemoveMode() {
        removeMode = false;
    }

    /** ---------- NEW: plain-Java accessor for tests (no JavaFX types) ---------- */
    public List<Recipe> getRecipesSnapshot() {
        return new ArrayList<>(recipes);
    }
}
