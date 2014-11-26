package com.jscriptive.moneyfx.ui.category.dialog;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.ui.common.CategoryStringConverter;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.layout.Priority.ALWAYS;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class CategoryDialog extends Dialog<Pair<Category, Boolean>> {

    public CategoryDialog() {
        this(null, Collections.emptyList());
    }

    public CategoryDialog(List<Category> categories) {
        this(null, categories);
    }

    public CategoryDialog(String category) {
        this(category, Collections.emptyList());
    }

    private CategoryDialog(String category, List<Category> categories) {
        super();
        setTitle(categories.isEmpty() ? "Category" : "Categories");
        setHeaderText(categories.isEmpty() ? "Enter the category name" : "Select category");
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Category-48.png"));

        // Set the button types.
        ButtonType okButtonType = new ButtonType(categories.isEmpty() ? "Save" : "Select", OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, CANCEL);
        // Enable/Disable login button depending on whether a username was entered.
        Node importButton = getDialogPane().lookupButton(okButtonType);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Category:"), 0, 0);

        TextField categoryTextField = new TextField(category);
        categoryTextField.setPrefWidth(300);
        categoryTextField.textProperty().addListener((observable, oldValue, newValue) -> importButton.setDisable(StringUtils.isBlank(newValue)));
        CheckBox filterRuleCheckBox = new CheckBox(String.format("%s filter rule for this category", StringUtils.isBlank(category) ? "Create" : "Edit"));

        ComboBox<Category> categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(categories));
        categoryComboBox.setConverter(new CategoryStringConverter(categories));
        categoryComboBox.setOnAction(event -> importButton.setDisable(categoryComboBox.getValue() == null));

        if (categories.isEmpty()) {
            importButton.setDisable(StringUtils.isBlank(category));
            grid.add(categoryTextField, 1, 0);
            grid.add(filterRuleCheckBox, 1, 1);
            GridPane.setHgrow(categoryTextField, ALWAYS);
        } else {
            importButton.setDisable(categoryComboBox.getValue() == null);
            grid.add(categoryComboBox, 1, 0);
            GridPane.setHgrow(categoryComboBox, ALWAYS);
        }

        getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButtonType -> {
            if (dialogButtonType == okButtonType) {
                if (categories.isEmpty()) {
                    return new Pair<>(new Category(categoryTextField.getText()), filterRuleCheckBox.isSelected());
                }
                return new Pair<>(categoryComboBox.getValue(), FALSE);
            }
            return null;
        });
    }
}
