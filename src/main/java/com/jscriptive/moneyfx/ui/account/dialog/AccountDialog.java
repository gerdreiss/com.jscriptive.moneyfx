package com.jscriptive.moneyfx.ui.account.dialog;

import com.jscriptive.moneyfx.exception.TechnicalException;
import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class AccountDialog extends Dialog<AccountItem> {

    public AccountDialog() {
        this(null);
    }

    public AccountDialog(Account account) {
        setTitle("Account");
        setHeaderText("Fill in the account data");
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Bank-48.png"));

        // Set the button types.
        ButtonType importButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(importButtonType, ButtonType.CANCEL);
        // Enable/Disable login button depending on whether a username was entered.
        Node importButton = getDialogPane().lookupButton(importButtonType);
        importButton.setDisable(true);

        URL resource = AccountDialog.class.getResource("AccountDialog.fxml");
        FXMLLoader loader = new FXMLLoader(resource, null, new JavaFXBuilderFactory());

        InputStream in = null;
        try {
            in = resource.openStream();
            getDialogPane().setContent(loader.load(in));
        } catch (IOException e) {
            throw new TechnicalException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        AccountDialogController controller = loader.getController();
        controller.setDisabledNodeToObserve(importButton);
        controller.setAccount(account);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getDialogPane().lookup("#bankField").requestFocus();
            }
        });

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButtonType -> {
            if (dialogButtonType == importButtonType) {
                return controller.getAccount();
            }
            return null;
        });
    }

}
