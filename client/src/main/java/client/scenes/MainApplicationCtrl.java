package client.scenes;

import client.MyFXML;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class MainApplicationCtrl {

    /**
     *   This is the right pane(This pane will load different screens)
     */
    @FXML
    private Pane contentPane;

    @FXML
    private Button addButton;

    private MyFXML fxml;

    @Inject
    public MainApplicationCtrl(MyFXML fxml){
        this.fxml =fxml;
    }

    /**
     *   Loads Recipe panel
     */
    @FXML
    private void addRecipe() {
        Pair<AddRecipeCtrl, Parent> pair = fxml.load(AddRecipeCtrl.class,
            "client", "scenes", "AddRecipePanel.fxml");

      /**
       *  Injects the main ctrl into the add recipe ctrl
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