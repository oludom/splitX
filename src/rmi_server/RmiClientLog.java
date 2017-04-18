package rmi_server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 18.04.2017
 *
 * @author SWirries
 */
public class RmiClientLog extends Application {

    ObservableList<String> clientLog = FXCollections.observableArrayList();
    RmiClient parentRmiClient;

    public RmiClientLog(RmiClient rmiClient){
        parentRmiClient = rmiClient;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Connect6 - Multiplayer Log");
        StackPane root  = new StackPane();
        Scene scene = new Scene(root, 600, 700);

        ListView lvLog = new ListView();
        lvLog.setItems(clientLog);

        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(lvLog);
        borderPane.setMargin(lvLog, new Insets(10,10,10,10));

        root.getChildren().add(borderPane);

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                parentRmiClient.stopLog();
            }
        });
    }


    public void addLogItem(String item){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientLog.add(item);
            }
        });
    }
}
