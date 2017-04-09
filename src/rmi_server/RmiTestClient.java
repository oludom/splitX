package rmi_server;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 * 09.04.2017
 *
 * @author SWirries
 */
public class RmiTestClient extends Application{

    String host;
    Label connectionState = new Label("Nicht verbunden!");
    boolean state = false;
    ObservableList opponents = FXCollections.observableArrayList();
    ListView listView = new ListView();
    RmiServerInterface rmiServerInterface;
    TextField textField = new TextField("Name");
    Timeline connectionTimeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

        int errorRate = 0;
        @Override
        public void handle(ActionEvent event) {
            try{
                ArrayList<String>  list = rmiServerInterface.getOpponentList();
                System.out.println(list);
                listView.getItems().clear();
                for (String o: list) {
                    System.out.println("clients: "+o);
                    if(!o.equals(textField.getText())) listView.getItems().add(o);
                }
            }catch (Exception ex){
                System.out.println("ServerVerbindungsError:" + ex);
                if( errorRate > 10)textField.setDisable(false);
                connectionState.setText("Nicht verbunden!");
            }
        }
    }));


   /* public static void main(String[] args) {
        String host = "192.168.112.1";

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            RmiServerInterface iconnect6 = (RmiServerInterface) registry.lookup("Server");
            String response = iconnect6.getOpponent();

            System.out.println("Ausgabe: "+response);

        }catch (Exception e){
            System.out.println("Error: "+e);
        }
    }*/



    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Connect6 Client");
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);

        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        Button btnConntect = new Button("Connect");
        Button btnDisconnect = new Button("Disconnect");

        borderPane.setBottom(gridPane);
        borderPane.setCenter(listView);
        borderPane.setTop(connectionState);

        gridPane.add(textField,1,1);
        gridPane.add(btnConntect, 1,2);
        gridPane.add(btnDisconnect, 2, 2);

        opponents.add("Nicht verbunden!");
        listView.setItems(opponents);

        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();

        btnConntect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                connectToServer();
            }
        });

        btnDisconnect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                disconnectFromServer();
            }
        });

    }

    private void connectToServer(){
        String host = "192.168.112.1";

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            rmiServerInterface = (RmiServerInterface) registry.lookup("Server");

            rmiServerInterface.addClient(textField.getText());

            connectionState.setText("Mit dem Server verbunden!");
            state = true;
            textField.setDisable(true);
            connectionTimeline.setCycleCount(Timeline.INDEFINITE);
            connectionTimeline.play();
        }catch (Exception e){
            System.out.println("Error: "+e);
        }
    }

    private void disconnectFromServer(){
        if(state){
            try {
                rmiServerInterface.removeClient(textField.getText());
                textField.setDisable(false);
                connectionState.setText("Nicht verbunden!");
                connectionTimeline.stop();
                listView.getItems().clear();
            }catch (Exception e){
                System.out.println("Error: "+e);
            }
        }
    }
}
