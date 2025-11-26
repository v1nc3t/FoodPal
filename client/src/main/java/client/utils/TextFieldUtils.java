package client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

public class TextFieldUtils {

    public static String getStringFromField(TextField textField, Label label) {
        String name = textField.getText();
        if (name.isEmpty()) {
            throw new IllegalArgumentException(label.getText() + " cannot be empty");
        }
        return name;
    }

    public static int getIntFromField(TextField textField, Label label) {
        String text = textField.getText();

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            String message = label.getText() + " must be a number";

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(message);
            alert.showAndWait();

            textField.clear();
            throw new NumberFormatException(message);
        }
    }

    public static double getDoubleFromField(TextField textField, Label label) {
        String text = textField.getText();

        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            String message = label.getText() + " must be a number, with or without decimal point";

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(message);
            alert.showAndWait();

            textField.clear();
            throw new NumberFormatException(message);
        }
    }
}
