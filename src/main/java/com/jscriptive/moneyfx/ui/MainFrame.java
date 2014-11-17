package com.jscriptive.moneyfx.ui;

import com.jscriptive.moneyfx.ui.account.AccountFrame;
import com.jscriptive.moneyfx.ui.category.CategoryFrame;
import com.jscriptive.moneyfx.ui.chart.ChartFrame;
import com.jscriptive.moneyfx.ui.events.EventTypes;
import com.jscriptive.moneyfx.ui.transaction.TransactionFrame;
import javafx.event.Event;
import javafx.fxml.FXML;
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

    @FXML
    private AccountFrame accountFrame;
    @FXML
    private TransactionFrame transactionFrame;
    @FXML
    private CategoryFrame categoryFrame;
    @FXML
    private ChartFrame chartFrame;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }


    public void tabSelectionChanged(Event event) {
        Tab t = (Tab) event.getTarget();
        if (t.isSelected()) {
            Node content = t.getContent();
            content.fireEvent(new Event(EventTypes.TAB_SELECTED));
        }
    }
}
