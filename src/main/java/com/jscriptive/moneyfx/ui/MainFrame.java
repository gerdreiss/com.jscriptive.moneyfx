package com.jscriptive.moneyfx.ui;

import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author jscriptive.com
 */
public class MainFrame extends BorderPane implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void tabSelectionChanged(Event event) {
        Tab t = (Tab) event.getTarget();
        if (t.isSelected()) {
            Node node = t.getContent().lookup("#dataTable");
            if (node == null) {
                node = t.getContent().lookup("#chartFrame");
            }
            if (node != null) {
                node.fireEvent(new TabSelectionEvent());
            }
        }
    }

    public void closeButtonHit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void optionsButtonHit(ActionEvent actionEvent) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Options");
        dialog.setGraphic(new ImageView(this.getClass().getResource("images/Options-32.png").toString()));
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Options-48.png"));
        dialog.setHeaderText("Here be options...");
        dialog.setContentText("We'll have some options in due time...");
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            return dialogButton == loginButtonType;
        });
        dialog.showAndWait();
    }
}
