package client.scenes;

import client.services.LocaleManager;
import client.services.ShoppingListManager;
import client.shoppingList.ShoppingListItem;
import com.google.inject.Inject;
import commons.Amount;
import commons.Unit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.control.TextInputDialog;

public class IngredientOverviewCtrl implements Internationalizable {

    private final ShoppingListManager shoppingListManager;
    private final LocaleManager localeManager;

    @FXML
    private Label titleLabel;
    @FXML
    private Label instructionLabel;
    @FXML
    private TableView<ShoppingListItem> ingredientsTable;
    @FXML
    private TableColumn<ShoppingListItem, String> nameColumn;
    @FXML
    private TableColumn<ShoppingListItem, String> amountColumn;
    @FXML
    private TableColumn<ShoppingListItem, String> sourceColumn;
    @FXML
    private Button addItemButton;
    @FXML
    private Button removeItemButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;

    private final StringProperty titleProperty = new SimpleStringProperty();
    private final StringProperty instructionProperty = new SimpleStringProperty();
    private final StringProperty nameColumnProperty = new SimpleStringProperty();
    private final StringProperty amountColumnProperty = new SimpleStringProperty();
    private final StringProperty sourceColumnProperty = new SimpleStringProperty();
    private final StringProperty addItemProperty = new SimpleStringProperty();
    private final StringProperty removeItemProperty = new SimpleStringProperty();
    private final StringProperty cancelProperty = new SimpleStringProperty();
    private final StringProperty confirmProperty = new SimpleStringProperty();

    private String addItemDialogHeader;
    private String addAmountDialogHeader;

    private Stage stage;

    @Inject
    public IngredientOverviewCtrl(ShoppingListManager shoppingListManager, LocaleManager localeManager) {
        this.shoppingListManager = shoppingListManager;
        this.localeManager = localeManager;
        this.localeManager.register(this);
    }

    @FXML
    public void initialize() {
        bindProperties();
        setLocale(localeManager.getCurrentLocale());

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

    private void bindProperties() {
        titleLabel.textProperty().bind(titleProperty);
        instructionLabel.textProperty().bind(instructionProperty);
        nameColumn.textProperty().bind(nameColumnProperty);
        amountColumn.textProperty().bind(amountColumnProperty);
        sourceColumn.textProperty().bind(sourceColumnProperty);
        addItemButton.textProperty().bind(addItemProperty);
        removeItemButton.textProperty().bind(removeItemProperty);
        cancelButton.textProperty().bind(cancelProperty);
        confirmButton.textProperty().bind(confirmProperty);
    }

    @Override
    public void setLocale(Locale newLocale) {
        ResourceBundle bundle = ResourceBundle.getBundle(localeManager.getBundleName(), newLocale);
        titleProperty.set(bundle.getString("txt.review_ingredients"));
        instructionProperty.set(bundle.getString("txt.adjust_instruction"));
        nameColumnProperty.set(bundle.getString("txt.ingredient"));
        amountColumnProperty.set(bundle.getString("txt.amount"));
        sourceColumnProperty.set(bundle.getString("txt.source_recipe"));
        addItemProperty.set(bundle.getString("txt.add_item"));
        removeItemProperty.set(bundle.getString("txt.remove_item"));
        cancelProperty.set(bundle.getString("txt.cancel"));
        confirmProperty.set(bundle.getString("txt.add_to_shopping_list"));

        addItemDialogHeader = bundle.getString("txt.add_item_dialog_header");
        addAmountDialogHeader = bundle.getString("txt.add_amount_dialog_header");
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
            return new Amount(0, input);
        }
    }

    @FXML
    private void addItem() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle(addItemProperty.get());
        nameDialog.setHeaderText(addItemDialogHeader);
        nameDialog.setContentText(nameColumnProperty.get() + ":");

        nameDialog.showAndWait().ifPresent(name -> {
            TextInputDialog amountDialog = new TextInputDialog("1");
            amountDialog.setTitle(addItemProperty.get());
            amountDialog.setHeaderText(addAmountDialogHeader);
            amountDialog.setContentText(amountColumnProperty.get() + ":");

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
