//@@author A0121597B-reused
package procrastinate.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class DialogPopupHandler {

    // ================================================================================
    // Message Strings
    // ================================================================================

    private static final String MESSAGE_HEADER = "An error has occurred with the following message:";

    private static final String BUTTON_MESSAGE_CANCEL = "Cancel";

    // ================================================================================
    // Class Variables
    // ================================================================================

    private Stage primaryStage_;

    // ================================================================================
    // DialogPopupHandler Constructor
    // ================================================================================

    protected DialogPopupHandler(Stage primaryStage) {
        // Set up the parent stage to retrieve information if required
        this.primaryStage_ = primaryStage;
    }

    // ================================================================================
    // DialogPopupHandler Methods
    // ================================================================================

    /**
     * Creates an error dialog with a header and message.
     *
     * @param header     to be displayed as the title
     * @param message    to be displayed in the body
     */
    protected void createErrorDialogPopup(String header, String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.initOwner(primaryStage_);

        dialog.setHeaderText(header);
        dialog.setContentText(message);

        dialog.showAndWait();
    }

    /**
     * Creates an error dialog that takes in an Exception and displays the exception message with its stack trace
     * in an expandable region.
     *
     * @param exception    whose stack trace should be shown in the expandable region
     */
    protected void createErrorDialogPopupWithTrace(Exception exception) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.initOwner(primaryStage_);

        // Retrieve the stack trace as String
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        String stackTrace = stringWriter.toString();

        dialog.setHeaderText(MESSAGE_HEADER);
        dialog.setContentText(exception.getMessage());

        // Place the stack trace into a TextArea for display
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setText(stackTrace);

        dialog.getDialogPane().setExpandableContent(textArea);
        dialog.showAndWait();
    }

    //@@author A0080485B-reused
    /**
     * Creates an error dialog which requires the user's action to either confirm or cancel,
     * and returns the choice result.
     *
     * @param header     to be displayed as the title
     * @param message    to be dispalyed in the body
     * @param okLabel    to be displayed in place of the text of the 'OK' button
     * @return           true if the result of the user's choice is 'OK', else false
     */
    protected boolean createErrorDialogPopupWithConfirmation(String header, String message, String okLabel) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.initOwner(primaryStage_);

        dialog.setHeaderText(header);
        dialog.setContentText(message);

        ButtonType okBtn = new ButtonType(okLabel, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType(BUTTON_MESSAGE_CANCEL, ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(okBtn, cancelBtn);

        ((Button) dialog.getDialogPane().lookupButton(okBtn)).setDefaultButton(false);
        ((Button) dialog.getDialogPane().lookupButton(cancelBtn)).setDefaultButton(true);

        Optional<ButtonType> choice = dialog.showAndWait();
        if (choice.get().equals(okBtn)) {
            return true;
        } else {
            return false;
        }
    }
}
