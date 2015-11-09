# A0080485Breused
###### procrastinate\ui\DialogPopupHandler.java
``` java
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
```
###### procrastinate\ui\SystemTrayHandler.java
``` java
    private void createSysTray() {
        sysTray_ = SystemTray.getSystemTray();
        popupMenu_ = createPopupMenu();
        sysTrayIcon_ = createSysTrayIcon(createSysTrayIconImage());

        if (!isWindowsOs()) {
            invisibleFrame_ = new Frame();
            invisibleFrame_.setUndecorated(true);
            invisibleFrame_.setResizable(false);
            invisibleFrame_.add(popupMenu_);
        }

        try {
            sysTray_.add(sysTrayIcon_);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

```
