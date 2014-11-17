package com.jscriptive.moneyfx.ui.exception;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by jscriptive.com on 14/11/2014.
 */
public class ExceptionDialog extends Alert {
    public ExceptionDialog(Throwable e) {
        super(AlertType.ERROR);
        setTitle("Exception");
        setHeaderText("Exception occurred while opening dialog");
        setContentText(e.getMessage());

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(ExceptionUtils.getStackTrace(e));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        getDialogPane().setExpandableContent(expContent);
    }
}
