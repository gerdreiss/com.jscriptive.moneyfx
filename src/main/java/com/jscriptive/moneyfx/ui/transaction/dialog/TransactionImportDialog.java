package com.jscriptive.moneyfx.ui.transaction.dialog;

import com.jscriptive.moneyfx.model.Bank;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;

/**
 * Created by Igor on 17/11/2014.
 */
public class TransactionImportDialog extends Dialog<Pair<Bank, File>> {

    private Bank selectedBank;
    private File selectedFile;

    public TransactionImportDialog(Collection<Bank> banks) {
        setTitle("Import transaction");
        setHeaderText("Select the bank and the respective transaction file");

        // Set the button types.
        ButtonType importButtonType = new ButtonType("Import", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(importButtonType, ButtonType.CANCEL);
        // Enable/Disable login button depending on whether a username was entered.
        Node importButton = getDialogPane().lookupButton(importButtonType);
        importButton.setDisable(true);


        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final Control bankControl;
        if (banks.isEmpty()) {
            TextField bankTextField = new TextField();
            bankTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (StringUtils.isBlank(newValue)) {
                    selectedBank = null;
                } else {
                    selectedBank = new Bank(newValue);
                }
                importButton.setDisable(selectedBank == null || selectedFile == null || !selectedFile.exists());
            });
            bankControl = bankTextField;
        } else {
            ComboBox<Bank> bankComboBox = new ComboBox<>(FXCollections.observableArrayList(banks));
            bankComboBox.setConverter(new StringConverter<Bank>() {
                @Override
                public String toString(Bank bank) {
                    return bank.getName();
                }

                @Override
                public Bank fromString(String name) {
                    return bankComboBox.getItems().stream().filter(bank -> name.equals(bank.getName())).findFirst().get();
                }
            });
            bankComboBox.setOnAction(event -> {
                selectedBank = bankComboBox.getValue();
                importButton.setDisable(selectedBank == null || selectedFile == null || !selectedFile.exists());
            });
            bankControl = bankComboBox;
        }

        TextField filePathTextField = new TextField();
        filePathTextField.setEditable(false);

        Button fileSelectionButton = new Button("Select");
        fileSelectionButton.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel sheets", "*.xls"));
            selectedFile = fileChooser.showOpenDialog(getOwner());
            if (selectedFile != null) {
                filePathTextField.setText(selectedFile.getAbsolutePath());
            }
            importButton.setDisable(selectedBank == null || selectedFile == null || !selectedFile.exists());
        });

        grid.add(new Label("Bank:"), 0, 0);
        grid.add(bankControl, 1, 0);
        grid.add(new Label("File:"), 0, 1);
        grid.add(filePathTextField, 1, 1);
        grid.add(fileSelectionButton, 2, 1);


        GridPane.setHgrow(bankControl, Priority.ALWAYS);
        GridPane.setHgrow(filePathTextField, Priority.ALWAYS);


        getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButtonType -> {
            if (dialogButtonType == importButtonType) {
                return new Pair<>(
                        banks.isEmpty()
                                ? new Bank(((TextField) bankControl).getText())
                                : ((ComboBox<Bank>) bankControl).getValue(),
                        new File(filePathTextField.getText()));
            }
            return null;
        });
    }
}
