package com.jscriptive.moneyfx.ui.transaction.dialog;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;

import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.input.KeyEvent.KEY_TYPED;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import static javafx.scene.layout.Priority.ALWAYS;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class TransactionBackupDialog extends Dialog<Pair<String, File>> {

    public TransactionBackupDialog() {
        setTitle("Backup transactions");
        setHeaderText("Select the backup form and path");
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Backup-48.png"));

        // Set the button types.
        ButtonType backupButtonType = new ButtonType("Backup", OK_DONE);
        getDialogPane().getButtonTypes().addAll(backupButtonType, CANCEL);
        // Enable/Disable login button depending on whether a username was entered.
        Node importButton = getDialogPane().lookupButton(backupButtonType);
        importButton.setDisable(true);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final ToggleGroup group = new ToggleGroup();
        RadioButton rb1 = new RadioButton("JSON");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        RadioButton rb2 = new RadioButton("CSV");
        rb2.setToggleGroup(group);

        TextField filePathTextField = new TextField();
        filePathTextField.setEditable(false);
        filePathTextField.setPrefWidth(300);
        filePathTextField.addEventHandler(KEY_TYPED, event -> selectFile(filePathTextField, importButton));
        filePathTextField.addEventHandler(MOUSE_CLICKED, event -> selectFile(filePathTextField, importButton));

        grid.add(new Label("Format:"), 0, 0);
        grid.add(rb1, 1, 0);
        grid.add(rb2, 2, 0);
        grid.add(new Label("File:"), 0, 1);
        grid.add(filePathTextField, 1, 1, 2, 1);

        GridPane.setHgrow(filePathTextField, ALWAYS);

        getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButtonType -> {
            if (dialogButtonType == backupButtonType) {
                return new Pair<>(((RadioButton) group.getSelectedToggle()).getText(), new File(filePathTextField.getText()));
            }
            return null;
        });
    }

    private void selectFile(TextField filePathTextField, Node importButton) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select backup directory");
        File dir = chooser.showDialog(getOwner());
        if (dir != null) {
            filePathTextField.setText(dir.getAbsolutePath());
        }
        importButton.setDisable(dir == null);
    }
}
