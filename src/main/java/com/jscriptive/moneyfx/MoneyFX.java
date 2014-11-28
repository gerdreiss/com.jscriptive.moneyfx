/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static java.lang.String.format;

/**
 * @author jscriptive.com
 */
public class MoneyFX extends Application {

    private static Logger log = Logger.getLogger(MoneyFX.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        setupExceptionHandling();

        Parent root = loadMainFrame();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("com/jscriptive/moneyfx/ui/css/MoneyFX.css");
        stage.setScene(scene);
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/MoneyFX.png"));
        stage.setTitle("MoneyFX");
        stage.show();
    }

    private void setupExceptionHandling() {
        // start is called on the FX Application Thread,
        // so Thread.currentThread() is the FX application thread:
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            Throwable message = getTheThrowable(throwable);
            log.error("MoneyFX has produced an error:", message);
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("MoneyFX error");
            error.setHeaderText("MoneyFX has produced an error");
            error.setContentText(format("MoneyFX has produced an error: %s.For more info see the logs.", message.getMessage()));
            error.showAndWait();
        });
    }

    private Throwable getTheThrowable(Throwable throwable) {
        Throwable theThrowable = throwable;
        if (throwable instanceof RuntimeException) {
            if (throwable.getCause() != null) {
                theThrowable = getTheThrowable(throwable.getCause());
            }
        } else if (throwable instanceof InvocationTargetException) {
            if (((InvocationTargetException) throwable).getTargetException() != null) {
                theThrowable = ((InvocationTargetException) throwable).getTargetException();
            } else {
                if (throwable.getCause() != null) {
                    theThrowable = getTheThrowable(throwable.getCause());
                }
            }
        }
        return theThrowable;
    }

    private Parent loadMainFrame() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("ui/MainFrame.fxml"));
        } catch (IOException e) {
            log.error("Error reading MainFrame.fxml", e);
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Start up error");
            error.setHeaderText("Application start up error");
            error.setContentText("Error occurred when starting the application. See the logs for details.");
            error.showAndWait();
            Platform.exit();
        }
        return root;
    }

}
