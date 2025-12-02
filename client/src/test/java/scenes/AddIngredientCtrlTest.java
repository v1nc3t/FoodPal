package scenes;

import client.MyFXML;
import client.MyModule;
import client.scenes.AddIngredientCtrl;
import client.scenes.AddRecipeCtrl;
import client.utils.IServerUtils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Unit;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javax.swing.*;

import static client.Main.BUNDLE_NAME;
import static client.Main.DEFAULT_LOCALE;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ApplicationExtension.class)
public class AddIngredientCtrlTest {

    private static class StubServer implements IServerUtils {}

    private static class StubParentCtrl extends AddRecipeCtrl {}

    private Label nameLabel;
    private Label amountLabel;
    private Label proteinLabel;
    private Label fatLabel;
    private Label carbsLabel;

    private TextField nameField;
    private TextField amountField;
    private TextField proteinField;
    private TextField fatField;
    private TextField carbsField;

    private ComboBox<String> unitComboBox;

    private Button doneButton;
    private Button cancelButton;

    private AddIngredientCtrl ctrl;
    private ResourceBundle resourceBundle;

    @Start
    private void start(Stage stage) throws IOException {
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML fxml = new MyFXML(injector);

        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, DEFAULT_LOCALE);

        var loaded = fxml.load(
                AddIngredientCtrl.class,
                resourceBundle,
                "client", "scenes", "AddIngredient.fxml"
        );

        ctrl = loaded.getKey();
        Parent root = loaded.getValue();
        var scene = new Scene(root);
        scene.setOnKeyPressed(ctrl::keyPressed);

        stage.setTitle("Add Ingredient Test");
        stage.setScene(scene);
        stage.show();

        lookupAll(scene);
    }

    private void lookupAll(Scene scene) {
        nameLabel = lookup(scene, "#nameLabel");
        amountLabel = lookup(scene, "#amountLabel");
        proteinLabel = lookup(scene, "#proteinLabel");
        fatLabel = lookup(scene, "#fatLabel");
        carbsLabel = lookup(scene, "#carbsLabel");

        nameField = lookup(scene, "#nameField");
        amountField = lookup(scene, "#amountField");
        proteinField = lookup(scene, "#proteinField");
        fatField = lookup(scene, "#fatField");
        carbsField = lookup(scene, "#carbsField");

        unitComboBox = lookup(scene, "#unitComboBox");

        doneButton = lookup(scene, "#doneButton");
        cancelButton = lookup(scene, "#cancelButton");
    }

    @SuppressWarnings("unchecked")
    private <T> T lookup(Scene scene, String id) {
        return (T) scene.lookup(id);
    }

    @Test
    public void testControllerLoaded() {
        assertNotNull(ctrl);
    }

    @Test
    public void testLabelLocalized(FxRobot robot) {
        assertNotNull(nameLabel);
        assertEquals(resourceBundle.getString("txt.name"), nameLabel.getText());
        assertNotNull(amountLabel);
        assertEquals(resourceBundle.getString("txt.amount"), amountLabel.getText());
        assertNotNull(proteinLabel);
        assertEquals(resourceBundle.getString("txt.protein"), proteinLabel.getText());
        assertNotNull(fatLabel);
        assertEquals(resourceBundle.getString("txt.fat"), fatLabel.getText());
        assertNotNull(carbsLabel);
        assertEquals(resourceBundle.getString("txt.carbs"), carbsLabel.getText());
    }

    @Test
    public void testFieldPromptLocalized(FxRobot robot) {
        assertNotNull(nameField);
        assertEquals(resourceBundle.getString("txt.enter_ingredient_name"), nameField.getPromptText());
        assertNotNull(amountField);
        assertEquals(resourceBundle.getString("txt.amount_of_ingredient"), amountField.getPromptText());
        assertNotNull(proteinField);
        assertEquals(resourceBundle.getString("txt.grams_of_protein"), proteinField.getPromptText());
        assertNotNull(fatField);
        assertEquals(resourceBundle.getString("txt.grams_of_fat"), fatField.getPromptText());
        assertNotNull(carbsField);
        assertEquals(resourceBundle.getString("txt.grams_of_carbohydrates"), carbsField.getPromptText());
    }

    @Test
    public void testButtonsLocalized(FxRobot robot) {
        assertNotNull(doneButton);
        assertEquals(resourceBundle.getString("txt.done"), doneButton.getText());
        assertNotNull(cancelButton);
        assertEquals(resourceBundle.getString("txt.cancel"), cancelButton.getText());
    }

    @Test
    public void testComboBox(FxRobot robot) {
        assertNotNull(unitComboBox);
        assertEquals(resourceBundle.getString("txt.select_unit"), unitComboBox.getPromptText());

        var expected = Arrays.stream(Unit.values())
                .map(x -> x.name().toLowerCase())
                .toList();
        assertEquals(expected, unitComboBox.getItems());
    }

    @Test
    public void testUnitSelected(FxRobot robot) {
        assertNotNull(unitComboBox);

        robot.clickOn(unitComboBox);

        unitComboBox.setValue(unitComboBox.getItems().get(0));

        assertEquals(unitComboBox.getItems().get(0), unitComboBox.getValue());
    }

    @Test
    public void testClickCancelCloseWindow(FxRobot robot) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        assertTrue(stage.isShowing());

        robot.clickOn(cancelButton);

        assertFalse(stage.isShowing());
    }

    @Test
    public void testKeyPressedEscapeCloseWindow(FxRobot robot) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        assertTrue(stage.isShowing());

        robot.press(KeyCode.ESCAPE).release(KeyCode.ESCAPE);

        assertFalse(stage.isShowing());
    }

    @Test
    public void testNameFieldInput(FxRobot robot) {
        assertNotNull(nameField);

        robot.clickOn(nameField);
        robot.write("test");

        assertEquals("test", nameField.getText());
    }
}