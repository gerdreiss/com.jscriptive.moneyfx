package com.jscriptive.moneyfx.ui;

import com.jscriptive.moneyfx.exception.TechnicalException;
import com.jscriptive.moneyfx.model.FlatTransaction;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.JsonRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionBackupDialog;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionBackupRequest;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.jscriptive.moneyfx.model.FlatTransaction.FIELD_NAMES;
import static com.jscriptive.moneyfx.ui.event.ShowTransactionsEvent.SHOW_TRANSACTIONS;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.writeLines;

/**
 * @author jscriptive.com
 */
public class MainFrame extends BorderPane implements Initializable {

    private static Logger log = Logger.getLogger(MainFrame.class);

    @FXML
    private TabPane tabPane;

    private TransactionFilter filter;

    private JsonRepository jsonRepository;
    private TransactionRepository transactionRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jsonRepository = RepositoryProvider.getInstance().getJsonRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
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
                    if (log.isDebugEnabled())
                        log.debug(format("TabSelectionEvent sent to %s with filter: %s", t.getId(), filter));
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
        openNotification("Options", "Settings", "Here be options...", "We'll have some options in due time...");
    }

    public void backupButtonHit(ActionEvent actionEvent) {
        TransactionBackupDialog dlg = new TransactionBackupDialog();
        Optional<TransactionBackupRequest> result = dlg.showAndWait();
        if (result.isPresent()) {
            File dir = result.get().getPath();
            if ("JSON".equals(result.get().getFormat())) {
                dir = new File(dir, "moneyfx-" + now().format(ofPattern("yyyyMMddHHmmss")));
                if (!dir.mkdirs()) return;
                Map<String, List<String>> data = jsonRepository.extractAll();
                try {
                    for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                        writeLines(new File(dir, entry.getKey() + ".json"), "UTF-8", entry.getValue());
                    }
                } catch (IOException e) {
                    throw new TechnicalException(e);
                }
            } else if ("CSV".equals(result.get().getFormat())) {
                List<Transaction> transactions = transactionRepository.findAll();
                List<String> lines = transactions.parallelStream()
                        .map(Transaction::toFlatTransaction)
                        .sorted((f1, f2) -> f1.getDtOp().compareTo(f2.getDtOp()))
                        .map(FlatTransaction::toString)
                        .collect(toList());
                if (result.get().isHeader()) {
                    lines.add(0, String.join(",", FIELD_NAMES));
                }
                try {
                    writeLines(new File(dir, "transactions.csv"), "UTF-8", lines);
                } catch (IOException e) {
                    throw new TechnicalException(e);
                }
            }
            openNotification("Backup", "Backup", "Backup successful", "The data has been successfully written to " + dir.getAbsolutePath());
        }
    }

    public void aboutButtonHit(ActionEvent actionEvent) {
        openNotification("About", "PM", "Here be about...", "We'll have some about in due time...");
    }

    private void openNotification(String title, String image, String header, String content) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setGraphic(new ImageView(this.getClass().getResource("images/" + image + "-32.png").toString()));
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/" + image + "-48.png"));
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(buttonType -> buttonType == loginButtonType);
        dialog.showAndWait();
    }
}
