package com.jscriptive.moneyfx.ui.transaction.dialog;

import com.jscriptive.moneyfx.model.Bank;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.input.KeyEvent.KEY_TYPED;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import static javafx.scene.layout.Priority.ALWAYS;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class TransactionImportDialog extends Dialog<Pair<String, File>> {

    private String selectedBank;
    private File selectedFile;

    public TransactionImportDialog(Collection<Bank> banks) {
        setTitle("Import transaction");
        setHeaderText("Select the bank and the transaction file");
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Data-Import-48.png"));

        // Set the button types.
        ButtonType importButtonType = new ButtonType("Import", OK_DONE);
        getDialogPane().getButtonTypes().addAll(importButtonType, CANCEL);
        // Enable/Disable login button depending on whether a username was entered.
        Node importButton = getDialogPane().lookupButton(importButtonType);
        importButton.setDisable(true);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bankTextField = new TextField();
        bankTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            selectedBank = newValue;
            importButton.setDisable(StringUtils.isBlank(selectedBank) || selectedFile == null || !selectedFile.exists());
        });
        TextFields.bindAutoCompletion(bankTextField, banks.stream().map(Bank::getName).collect(Collectors.toList()));

        TextField filePathTextField = new TextField();
        filePathTextField.setEditable(false);
        filePathTextField.addEventHandler(KEY_TYPED, event -> selectFile(filePathTextField, importButton));
        filePathTextField.addEventHandler(MOUSE_CLICKED, event -> selectFile(filePathTextField, importButton));

        grid.add(new Label("Bank:"), 0, 0);
        grid.add(bankTextField, 1, 0);
        grid.add(new Label("File:"), 0, 1);
        grid.add(filePathTextField, 1, 1);

        GridPane.setHgrow(bankTextField, ALWAYS);
        GridPane.setHgrow(filePathTextField, ALWAYS);

        getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButtonType -> {
            if (dialogButtonType == importButtonType) {
                return new Pair<>(bankTextField.getText(), new File(filePathTextField.getText()));
            }
            return null;
        });
    }

    private void selectFile(TextField filePathTextField, Node importButton) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel sheets", "*.xls"));
        selectedFile = fileChooser.showOpenDialog(getOwner());
        if (selectedFile != null) {
            filePathTextField.setText(selectedFile.getAbsolutePath());
        }
        importButton.setDisable(StringUtils.isBlank(selectedBank) || selectedFile == null || !selectedFile.exists());
    }
}
