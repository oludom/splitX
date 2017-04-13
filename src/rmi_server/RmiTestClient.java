package rmi_server;

import game.BoardPoint;
import game.Stone;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Optional;

import static java.lang.Thread.sleep;

/**
 * 09.04.2017
 *
 * @author SWirries
 */
public class RmiTestClient extends Application{

    String host;
    private Label connectionState = new Label("Nicht verbunden!");
    private boolean state = false;
    private ObservableList opponents = FXCollections.observableArrayList();
    private ListView listView = new ListView();
    private RmiServerInterface rmiServerInterface;
    private TextField textField = new TextField("Name");
    private TextField tfServerIP = new TextField("127.0.0.1");
    private Label lableServerIp = new Label("Server IP:");
    private Stage mainStage;
    private boolean runningRequestThread = true;
    private boolean runningGameThread = true;
    private boolean dialogIsShowing = false;
    private boolean requestMode = false;
    private int gameID = 0;

    private int boardDimension = -1;
    private boolean gameBeginner = true;
    private int stoneCount;
    /**
     * Thread für das aktualliesiern der Browserliste
     */
    private Timeline connectionTimeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

        int errorRate = 0;
        @Override
        public void handle(ActionEvent event) {
            try{
                ArrayList<String>  list = rmiServerInterface.getOpponentList();
//                System.out.println(list);
                listView.getItems().clear();
                for (String o: list) {
//                    System.out.println("clients: "+o);
                    if(!o.equals(textField.getText())) listView.getItems().add(o);
                }
            }catch (Exception ex){
                System.out.println("ServerVerbindungsError:" + ex);
                if( errorRate > 10)textField.setDisable(false);
                connectionState.setText("Nicht verbunden!");
            }
        }
    }));

    /**
     * Thread für das Überprüfen von Anfragen sowie das annahmen der Anfragen
     */
    private Thread requestThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (runningRequestThread){
                try{
                    System.out.println(textField.getText()+" isShowing:"+dialogIsShowing + " requestMode: "+ requestMode);
                    if(!dialogIsShowing && !requestMode){
                        String name = rmiServerInterface.newRequest(textField.getText());

                        if(!name.equals("")){
                            gameID = rmiServerInterface.getRequest(name);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    dialogIsShowing = true;
                                    Alert infoDialog = new Alert(Alert.AlertType.CONFIRMATION);
                                    infoDialog.setTitle("Anfrage");
                                    infoDialog.setHeaderText("Sie habe eine Anfrage zu einem Spiel erhalten.");
                                    infoDialog.setContentText(name + " hat Sie zu einem Spiel aufgefordert!");

                                    Optional<ButtonType> result = infoDialog.showAndWait();
                                    if(result.get() == ButtonType.OK){

                                        try {
                                            rmiServerInterface.setRequestState(gameID, RmiServerInterface.ACCEPT);
                                            runningRequestThread = false;

                                        }catch (Exception e){
                                            System.out.println("DialogRMI Acc Error"+e);
                                            e.printStackTrace();
                                        }finally {
                                            dialogIsShowing = false;
                                            System.out.println("Game wurde angenommen");
                                        }
                                    }else{

                                        try {
                                            rmiServerInterface.setRequestState(gameID, RmiServerInterface.DECLINED);

                                        }catch (Exception e){
                                            System.out.println("DialogRMI Dec Error"+e);
                                            e.printStackTrace();
                                        }finally {
                                            dialogIsShowing = false;
                                            System.out.println("Game wurde abgelehnt");
                                        }
                                    }
                                }
                            });

                        }

                    }else  if(requestMode && !dialogIsShowing){
                        int state = -1;
                        try{
                            state = rmiServerInterface.getRequestState(gameID);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                        if(state == RmiServerInterface.ACCEPT){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    dialogIsShowing = true;
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Anfrage");
                                    alert.setContentText("Anfrage wurde angenommen");
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if(result.get() == ButtonType.OK){
                                        //TODO Aktionen anpassen
                                        try {
                                            rmiServerInterface.setRequestState(gameID,RmiServerInterface.RUNNING);
                                            runningRequestThread = false;

                                            //TODO hier beginnt das Spiel
                                            runningGameThread = true;
                                            gameThread.start();

                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                        runningRequestThread = false;
                                        connectionTimeline.stop();
                                        requestMode = false;
                                        dialogIsShowing = false;
                                    }
                                }
                            });
                        }else if(state == RmiServerInterface.DECLINED){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    dialogIsShowing = true;
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setContentText("Anfrage wurde abgelehnt");

                                    Optional<ButtonType> result = alert.showAndWait();
                                    if(result.get() == ButtonType.OK){
                                        gameID = -1;
                                        requestMode = false;
                                        dialogIsShowing = false;
                                    }
                                }
                            });
                        }
                    }

                    sleep(2000);
                }catch (Exception ex){
                    System.out.println("runningGameError:"+ex);
                }

            }


        }
    });

    /**
     * Thread für den Abruf der Steine vom Server
     */
    private Thread gameThread = new Thread(new Runnable() {
        @Override
        public void run() {
           while(runningGameThread) {

               if (gameBeginner){
                   //Spieler Schwarz
                   //TODO BoardDim austauschen
                   if(boardDimension < 6){
                       //TODO Größe via Dialog abfragen
//                       boardDimension = //Größe abfragen
                       try {
                           rmiServerInterface.setGameControl(gameID, RmiServerInterface.CBORDDIM, boardDimension);
                       } catch (RemoteException e) {
                           e.printStackTrace();
                       }
                   }
                   /**
                    * Methode um die einen Stein an der Server zu senden
                    */
//                   if(stoneCount == 0){
//                       //TODO setzen des ersten Steins
//                       try {
//                           rmiServerInterface.setStone(gameID, new Stone(new BoardPoint("A",1),true));
//                       } catch (RemoteException e) {
//                           e.printStackTrace();
//                       }
//                   }


               }else {
                   //Spieler Weiß
                   if(boardDimension < 0){
                       int[] gameControl = new int[2];
                       try {
                            gameControl = rmiServerInterface.getGameControl(gameID);
                       } catch (RemoteException e) {
                           e.printStackTrace();
                       }
                       if (gameControl[0] == RmiServerInterface.CBORDDIM) {
                           //TODO Setzten der Board Größe
                           boardDimension = gameControl[1];
                       }
                   }

               }
               /**
               Gilt für alle Spieler
                */
               int serverStoneCount = -1;
               try {
                   //Prüfen ob neue Steine vorhanden
                   serverStoneCount = rmiServerInterface.countStones(gameID);
               } catch (RemoteException e) {
                   e.printStackTrace();
               }
               if(stoneCount <= serverStoneCount){
                   try{
                       ArrayList<Stone> allStones = rmiServerInterface.getStone(gameID, true);
                       stoneCount = allStones.size();
                       //TODO Methode um die Steine dem Board hinzuzufügen
                   }catch (Exception ex){
                       ex.printStackTrace();
                   }
               }
           }
        }
    });

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
        mainStage = primaryStage;

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);

        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        GridPane topGridPane = new GridPane();
        Button btnConntect = new Button("Connect");
        Button btnDisconnect = new Button("Disconnect");

        borderPane.setBottom(gridPane);
        borderPane.setCenter(listView);
        borderPane.setTop(topGridPane);

        gridPane.add(textField,1,1);
        gridPane.add(btnConntect, 1,2);
        gridPane.add(btnDisconnect, 2, 2);

        topGridPane.add(lableServerIp, 1, 1);
        topGridPane.add(tfServerIP, 2, 1);
        topGridPane.add(connectionState, 3,1);

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

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String clickedName = (String) listView.getSelectionModel().getSelectedItem();
                System.out.println("Clicked:" +clickedName);
                try{
                   gameID = rmiServerInterface.requestOpponent(textField.getText(), clickedName);
                   requestMode = true;
                }catch (Exception ex){
                    System.out.println("ListClickeError:"+ex);
                }
            }
        });

    }

    private void connectToServer(){
        String host = tfServerIP.getText();

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            rmiServerInterface = (RmiServerInterface) registry.lookup("Server");

            rmiServerInterface.addClient(textField.getText());

            connectionState.setText("Mit dem Server verbunden!");
            state = true;
            textField.setDisable(true);

            connectionTimeline.setCycleCount(Timeline.INDEFINITE);
//            runningGameTimeline.setCycleCount(Timeline.INDEFINITE);

            connectionTimeline.play();
//            runningGameTimeline.play();
            requestThread.start();

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
                runningRequestThread = false;
                runningGameThread = false;

                listView.getItems().clear();
            }catch (Exception e){
                System.out.println("Error: "+e);
            }
        }
    }
}
