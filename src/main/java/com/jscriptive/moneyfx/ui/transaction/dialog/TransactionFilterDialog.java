package com.jscriptive.moneyfx.ui.transaction.dialog;

import com.jscriptive.moneyfx.exception.TechnicalException;
import com.jscriptive.moneyfx.model.TransactionFilter;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
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
public class TransactionFilterDialog extends Dialog<TransactionFilter> {

    public TransactionFilterDialog() {
        this(null);
    }

    public TransactionFilterDialog(TransactionFilter filter) {
        setTitle("Filter transactions");
        setHeaderText("Fill in the transaction data");
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Filter-Standard-48.png"));

        // Set the button types.
        ButtonType importButtonType = new ButtonType("Filter", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(importButtonType, ButtonType.CANCEL);

        URL resource = TransactionFilterDialog.class.getResource("TransactionFilterDialog.fxml");
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

        TransactionFilterDialogController controller = loader.getController();
        controller.setFilter(filter);

        // Convert the result to a username-password-pair when the login button is clicked.
        setResultConverter(dialogButtonType -> {
            if (dialogButtonType == importButtonType) {
                return controller.getTransactionFilter();
            }
            return null;
        });
    }
}
