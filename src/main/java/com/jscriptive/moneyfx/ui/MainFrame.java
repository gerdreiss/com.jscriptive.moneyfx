package com.jscriptive.moneyfx.ui;

import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static com.jscriptive.moneyfx.ui.event.ShowTransactionsEvent.SHOW_TRANSACTIONS;
import static java.lang.String.format;

/**
 * @author jscriptive.com
 */
public class MainFrame extends BorderPane implements Initializable {

    private static Logger log = Logger.getLogger(MainFrame.class);

    @FXML
    private TabPane tabPane;

    private TransactionFilter filter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabPane.addEventHandler(SHOW_TRANSACTIONS, event -> {
            filter = event.getFilter();
            if (log.isDebugEnabled()) log.debug("ShowTransactionEvent received with filter: " + filter);
            tabPane.getSelectionModel().select(1);
        });
    }

    public void tabSelectionChanged(Event event) {
        Tab t = (Tab) event.getTarget();
        if (t.isSelected()) {
            Node node = t.getContent().lookup("#dataTable");
            if (node == null) {
                node = t.getContent().lookup("#chartFrame");
            }
            if (node != null) {
                if (t.getId().equals("transactionsTab")) {
                    node.fireEvent(new TabSelectionEvent(filter));
                    if (log.isDebugEnabled()) log.debug(format("TabSelectionEvent sent to %s with filter: %s", t.getId(), filter));
                } else {
                    node.fireEvent(new TabSelectionEvent());
                    if (log.isDebugEnabled()) log.debug(format("TabSelectionEvent sent to %s", t.getId()));
                }
            }
        }
    }

    public void closeButtonHit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void optionsButtonHit(ActionEvent actionEvent) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Options");
        dialog.setGraphic(new ImageView(this.getClass().getResource("images/Settings-32.png").toString()));
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Settings-48.png"));
        dialog.setHeaderText("Here be options...");
        dialog.setContentText("We'll have some options in due time...");
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(buttonType -> buttonType == loginButtonType);
        dialog.showAndWait();
    }

    public void backupButtonHit(ActionEvent actionEvent) {

    }
}
