package client.scenes;

import client.MyFXML;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.Locale;
import java.util.ResourceBundle;

import static client.Main.BUNDLE_NAME;
import static client.Main.DEFAULT_LOCALE;

public class MainApplicationCtrl {

    /**
     *   This is the right pane(This pane will load different screens)
     */
    @FXML
    private Pane contentPane;

    private final StringProperty addProperty = new SimpleStringProperty();
    @FXML
    private Button addButton;

    private final StringProperty removeProperty = new SimpleStringProperty();
    @FXML
    private Button removeButton;

    private final StringProperty refreshProperty = new SimpleStringProperty();
    @FXML
    private Button refreshButton;

    private MyFXML fxml;

    @Inject
    public MainApplicationCtrl(MyFXML fxml){
        this.fxml =fxml;
    }

    /**
     * Initializes UI functionality
     */
    @FXML
    private void initialize(){
        bindElementsProperties();

        /* For UI testing purposes, since we don't have a button
         for language selection just yet, change this line
         if you want to visualize language changes.
         Parameter choices:
         EN: DEFAULT_LOCALE
         DE: Locale.GERMAN
         NL: Locale.forLanguageTag("nl-NL")
        */
        setLocale(DEFAULT_LOCALE);
    }

    /**
     * Binds the elements of the UI to StringProperties,
     * allowing dynamic updates, i.e. instant propagation,
     * of the language of buttons, labels, etc.
     * Should only be called once when initializing the controller.
     */
    private void bindElementsProperties() {
        addButton.textProperty().bind(addProperty);
        removeButton.textProperty().bind(removeProperty);
        refreshButton.textProperty().bind(refreshProperty);
    }

    /**
     * Dynamically updates properties of UI elements to the language
     * of a corresponding locale
     * @param locale provided locale/language for UI elements
     */
    private void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        addProperty.set(resourceBundle.getString("txt.add"));
        removeProperty.set(resourceBundle.getString("txt.remove"));
        refreshProperty.set(resourceBundle.getString("txt.refresh"));
    }

    /**
     *   Loads Recipe panel
     */
    @FXML
    private void addRecipe() {
        var bundle = ResourceBundle.getBundle(BUNDLE_NAME, DEFAULT_LOCALE);
        Pair<AddRecipeCtrl, Parent> pair = fxml.load(AddRecipeCtrl.class, bundle,
            "client", "scenes", "AddRecipePanel.fxml");

        /*
        Injects the main ctrl into the add recipe ctrl
        */
        AddRecipeCtrl addRecipeCtrl = pair.getKey();
        Parent addRecipeRoot = pair.getValue();

        contentPane.getChildren().setAll(addRecipeRoot);
    }

    /**
     *  This clears the current screen back to the main(blank for now)
     */
    public void showMainScreen(){
        contentPane.getChildren().clear();
    }
}