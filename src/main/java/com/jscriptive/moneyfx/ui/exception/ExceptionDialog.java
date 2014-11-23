package com.jscriptive.moneyfx.ui.exception;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static java.lang.Double.MAX_VALUE;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.layout.Priority.ALWAYS;

/**
 * Created by jscriptive.com on 14/11/2014.
 */
public class ExceptionDialog extends Alert {

    public ExceptionDialog(Throwable e) {
        super(ERROR);
        setTitle("Exception");
        setHeaderText("Exception occurred while opening dialog");
        setContentText(e.getMessage());

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(ExceptionUtils.getStackTrace(e));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(MAX_VALUE);
        textArea.setMaxHeight(MAX_VALUE);

        GridPane.setVgrow(textArea, ALWAYS);
        GridPane.setHgrow(textArea, ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        getDialogPane().setExpandableContent(expContent);
    }
}
