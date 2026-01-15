package client.scenes;

import client.services.ShoppingListManager;
import client.shoppingList.ShoppingListItem;
import com.google.inject.Inject;
import commons.Amount;
import commons.Unit;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.List;
import javafx.scene.control.TextInputDialog;

public class IngredientOverviewCtrl {

    private final ShoppingListManager shoppingListManager;

    @FXML
    private TableView<ShoppingListItem> ingredientsTable;
    @FXML
    private TableColumn<ShoppingListItem, String> nameColumn;
    @FXML
    private TableColumn<ShoppingListItem, String> amountColumn;
    @FXML
    private TableColumn<ShoppingListItem, String> sourceColumn;

    private Stage stage;

    @Inject
    public IngredientOverviewCtrl(ShoppingListManager shoppingListManager) {
        this.shoppingListManager = shoppingListManager;
    }

    @FXML
    public void initialize() {
        ingredientsTable.setEditable(true);

        nameColumn.setCellValueFactory(item -> {
            String name = item.getValue().getCustomName();
            return new SimpleStringProperty(name == null ? "" : name);
        });
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            event.getRowValue().setCustomName(event.getNewValue());
        });

        sourceColumn.setCellValueFactory(item -> {
            String source = item.getValue().getSourceRecipeName();
            return new SimpleStringProperty(source == null ? "" : source);
        });

        amountColumn.setCellValueFactory(item -> {
            Amount amt = item.getValue().getAmount();
            return new SimpleStringProperty(amt == null ? "" : amt.toPrettyString());
        });
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setOnEditCommit(event -> {
            Amount newAmount = parseAmount(event.getNewValue());
            event.getRowValue().setAmount(newAmount);
            ingredientsTable.refresh();
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setItems(List<ShoppingListItem> items) {
        ingredientsTable.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    private void confirm() {
        shoppingListManager.addItems(ingredientsTable.getItems());
        if (stage != null)
            stage.close();
    }

    @FXML
    private void cancel() {
        if (stage != null)
            stage.close();
    }

    private Amount parseAmount(String input) {
        if (input == null || input.isBlank())
            return new Amount(0, "");

        String[] parts = input.trim().split("\\s+", 2);
        try {
            double quantity = Double.parseDouble(parts[0]);
            if (parts.length > 1) {
                String unitStr = parts[1].toUpperCase();
                try {
                    Unit unit = Unit.valueOf(unitStr);
                    return new Amount(quantity, unit);
                } catch (IllegalArgumentException e) {
                    return new Amount(quantity, parts[1]);
                }
            } else {
                return new Amount(quantity, "");
            }
        } catch (NumberFormatException e) {
            // If the first part isn't a number, treat the whole thing as a description with
            // 0 quantity?
            // Or maybe 1? Let's use 0 quantity and the whole string as description for
            // safety:)
            return new Amount(0, input);
        }
    }

    @FXML
    private void addItem() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Item");
        nameDialog.setHeaderText("Enter item name:");
        nameDialog.setContentText("Name:");

        nameDialog.showAndWait().ifPresent(name -> {
            TextInputDialog amountDialog = new TextInputDialog("1");
            amountDialog.setTitle("Add Item");
            amountDialog.setHeaderText("Enter amount (e.g., '1 kg', '2 pieces'):");
            amountDialog.setContentText("Amount:");

            amountDialog.showAndWait().ifPresent(amountStr -> {
                Amount amount = parseAmount(amountStr);
                ShoppingListItem newItem = new ShoppingListItem(name, amount);
                ingredientsTable.getItems().add(newItem);
            });
        });
    }

    @FXML
    private void removeItem() {
        ShoppingListItem selected = ingredientsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ingredientsTable.getItems().remove(selected);
        }
    }
}
