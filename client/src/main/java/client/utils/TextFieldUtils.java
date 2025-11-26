package client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

public class TextFieldUtils {

    /**
     * Extracts a non-empty string from a field
     * @param textField user input
     * @param label to customize error message
     * @return a string of user input
     * @throws IllegalArgumentException when input is null
     */
    public static String getStringFromField(TextField textField, Label label) {
        String text = textField.getText();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(label.getText() + " cannot be empty");
        }
        return text;
    }

    /**
     * Extracts a non-null integer from a field
     * @param textField user input
     * @param label to customize error message
     * @return an integer of user input
     * @throws NumberFormatException when input is null or not integer
     */
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

    /**
     * Extract a non-null double from a field
     * @param textField user input
     * @param label to customize error message
     * @return a double of user input
     * @throws NumberFormatException when input is null or not double
     */
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
