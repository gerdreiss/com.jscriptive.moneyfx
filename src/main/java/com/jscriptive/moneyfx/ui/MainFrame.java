package com.jscriptive.moneyfx.ui;

import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

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
                node = t.getContent().lookup("#lineChart");
            }
            if (node != null) {
                node.fireEvent(new TabSelectionEvent());
            }
        }
    }
}
