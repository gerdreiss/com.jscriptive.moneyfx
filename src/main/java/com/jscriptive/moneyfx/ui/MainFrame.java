package com.jscriptive.moneyfx.ui;

import com.jscriptive.moneyfx.exception.TechnicalException;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.exec.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static com.jscriptive.moneyfx.ui.event.ShowTransactionsEvent.SHOW_TRANSACTIONS;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

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
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select backup directory");
        File dir = chooser.showDialog(tabPane.getScene().getWindow());
        if (dir == null) return;
        dir = new File(dir, "moneyfx-" + now().format(ofPattern("yyyyMMddHHmmss")));
        if (!dir.mkdirs()) return;


        Map<String, File> map = new HashMap<>();
        map.put("file", dir);
        map.put("db", "moneyfx");
        CommandLine commandLine = new CommandLine("mongodump");
        commandLine.addArgument("--out '${file}'");
        commandLine.addArgument("--db ${db}");
        commandLine.setSubstitutionMap(map);


        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
        Executor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.setWatchdog(watchdog);
        executor.setWorkingDirectory(new File("/"));
        try {
            executor.execute(commandLine, resultHandler);
            resultHandler.waitFor();
            if (resultHandler.hasResult()) {
                if (resultHandler.getException() == null) {
                    openNotification("Backup", "Backup", "Backup successful", "The database has been successfully backed up to " + dir.getAbsolutePath());
                } else {
                    log.error("Backup failed", resultHandler.getException());
                    openNotification("Backup", "Backup", "Backup failed", "The database backup has failed: " + resultHandler.getException().getLocalizedMessage());
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new TechnicalException(e);
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
