//@@author A0121597B-reused
package procrastinate.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * <h1>DialogPopupHandler class handles the creation and showing of all the error
 * dialogs used.</h1>
 */
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

        setHeaderAndContentOfDialog(header, message, dialog);

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
        String stackTrace = convertStackTraceToString(exception);

        setHeaderAndContentOfDialog(MESSAGE_HEADER, exception.getMessage(), dialog);

        // Place the stack trace into a TextArea for display
        TextArea textArea = getTextAreaWithTrace(stackTrace);

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

        setHeaderAndContentOfDialog(header, message, dialog);

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

    private void setHeaderAndContentOfDialog(String header, String message, Alert dialog) {
        dialog.setHeaderText(header);
        dialog.setContentText(message);
    }

    private String convertStackTraceToString(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        String stackTrace = stringWriter.toString();
        return stackTrace;
    }

    private TextArea getTextAreaWithTrace(String stackTrace) {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setText(stackTrace);
        return textArea;
    }
}
