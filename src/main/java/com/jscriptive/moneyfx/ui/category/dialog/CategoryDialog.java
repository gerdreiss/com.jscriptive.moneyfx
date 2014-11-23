package com.jscriptive.moneyfx.ui.category.dialog;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class CategoryDialog extends Dialog<Pair<String, Boolean>> {

    public CategoryDialog() {
        this(null);
    }

    public CategoryDialog(String category) {
        super();
        setTitle("Category");
        setHeaderText("Enter the category name");
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Category-48.png"));

        // Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        // Enable/Disable login button depending on whether a username was entered.
        Node importButton = getDialogPane().lookupButton(saveButtonType);
        importButton.setDisable(true);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField categoryTextField = new TextField(category);
        categoryTextField.setPrefWidth(300);
        categoryTextField.textProperty().addListener((observable, oldValue, newValue) -> importButton.setDisable(StringUtils.isBlank(newValue)));

        CheckBox filterRuleCheckBox = new CheckBox(String.format("%s filter rule for this category", StringUtils.isBlank(category) ? "Create" : "Edit"));

        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryTextField, 1, 0);
        if (StringUtils.isBlank(category)) {
            grid.add(filterRuleCheckBox, 1, 1);
        }

        GridPane.setHgrow(categoryTextField, Priority.ALWAYS);

        getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButtonType -> {
            if (dialogButtonType == saveButtonType) {
                return new Pair<>(categoryTextField.getText(), filterRuleCheckBox.isSelected());
            }
            return null;
        });
    }
}
