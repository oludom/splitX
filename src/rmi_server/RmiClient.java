package rmi_server;

import game.Board;
import game.Stone;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import ui.BasicUIX;

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
public class RmiClient extends Application{

    String host;
    private Label labelConnectionState = new Label("Nicht verbunden!");

    private ObservableList opponents = FXCollections.observableArrayList();
    private ListView listView = new ListView();
    private RmiServerInterface rmiServerInterface;
    private TextField tfUserName = new TextField("Name");
    private TextField tfServerIP = new TextField("127.0.0.1");
    private Label labelServerIp = new Label("Server IP:");
    private Stage mainStage;

    private boolean state = false;
    private boolean runningRequestThread = true;
    private boolean runningGameThread = true;
    private boolean dialogIsShowing = false;
    private boolean requestMode = false;
    private int gameID = 0;
    private String noOpponentFound = "Keine Gegner auf dem Server vorhanden!";

    private int boardDimension = -1;
    private boolean gameBeginner = true;
    private int stoneCount;
    private double xWindowPos;
    private double yWindowPos;

    BasicUIX gameUI;

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
                    if(!o.equals(tfUserName.getText())) listView.getItems().add(o);
                }
                if (listView.getItems().size() == 0) {
                    listView.getItems().add(noOpponentFound);
                }
                labelConnectionState.setText("Mit dem Server verbunden!");
                labelConnectionState.setTextFill(Color.GREEN);
            }catch (Exception ex){
                System.out.println("ServerVerbindungsError:" + ex);
                if( errorRate > 10) tfUserName.setDisable(false);
                labelConnectionState.setText("Nicht verbunden!");
                labelConnectionState.setTextFill(Color.RED);
            }
        }
    }));

    /**
     * Thread für das Überprüfen von Anfragen sowie das annahmen der Anfragen
     */
    private Runnable runnableRequest = new Runnable() {
        @Override
        public void run() {
            while (runningRequestThread){
                try{
                    //TODO REMOVE
                    System.out.println(tfUserName.getText()+" isShowing:"+dialogIsShowing + " requestMode: "+ requestMode);
                    if(!dialogIsShowing && !requestMode){
                        String name = rmiServerInterface.newRequest(tfUserName.getText());

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
                                    //TODO Fensterpostition anpassen
                                    infoDialog.setX(xWindowPos + infoDialog.getWidth() / 2);
                                    infoDialog.setY(yWindowPos + infoDialog.getHeight() / 2);

                                    Optional<ButtonType> result = infoDialog.showAndWait();
                                    if(result.get() == ButtonType.OK){

                                        try {
                                            rmiServerInterface.setRequestState(gameID, RmiServerInterface.ACCEPT);
                                            stopRequestThread();

                                            //hier beginnt das Spiel
                                            gameBeginner = false;
                                            startGameThread();

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
                                    //TODO Fenster Position anpassen
                                    System.out.println("Poition:"+mainStage.getX() + " | " + xWindowPos);
                                    alert.setX(mainStage.getX() + alert.getWidth() /3);
                                    alert.setY(mainStage.getY() + alert.getHeight() / 3);
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if(result.get() == ButtonType.OK){

                                        try {
                                            rmiServerInterface.setRequestState(gameID,RmiServerInterface.RUNNING);
                                            stopRequestThread();

                                            //hier beginnt das Spiel
                                            startGameThread();

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
                                    alert.setX(xWindowPos);
                                    alert.setY(yWindowPos);

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
    };

    /**
     * Runnable für den Abruf der Steine vom Server
     */
    private Runnable runnableGame=  new Runnable() {
        @Override
        public void run() {
           boolean checkBoardDim = false;
           boolean gameIsRunning = false;
           while(runningGameThread) {

               if (gameBeginner && !gameIsRunning){
                   //Spieler Schwarz

                   if(boardDimension < 6 && !dialogIsShowing){

                       Platform.runLater(new Runnable() {
                           @Override
                           public void run() {
                               dialogIsShowing = true;
                               //TODO Fenster Position anpassen
                               boardDimension = gameUI.getBoardDimensions(mainStage.getX() + 100,mainStage.getY() + 100);
                               gameUI.setBoard(new Board(boardDimension));
                               gameUI.startMultiGame(gameBeginner);
                           }
                       });
                       try {
                           sleep(5000);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
                   try {
                       sleep(5000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }finally {
//                       dialogIsShowing = false;
                   }

                   if (boardDimension > 5 && !checkBoardDim) {
                       try {
                           rmiServerInterface.setGameControl(gameID, RmiServerInterface.CBORDDIM, boardDimension);
                           checkBoardDim = true;
                           gameIsRunning = true;
                           toBack();
                       } catch (RemoteException e) {
                           e.printStackTrace();
                       }
                   }

               }else if (!gameIsRunning){
                   //Spieler Weiß
                   if(boardDimension < 0){
                       int[] gameControl = new int[2];
                       try {
                            gameControl = rmiServerInterface.getGameControl(gameID);
                       } catch (RemoteException e) {
                           e.printStackTrace();
                       }catch (NullPointerException e){

                       }
                       try {
                           if (gameControl[0] == RmiServerInterface.CBORDDIM) {
                               boardDimension = gameControl[1];
                               gameUI.setBoard(new Board(boardDimension));
                               gameUI.startMultiGame(gameBeginner);
                               gameIsRunning = true;
                               toBack();
                           }
                       } catch (Exception e) {
                           System.out.println("Exeption in Spieler Weiß BoardDim");
                           e.printStackTrace();
                           try {
                               sleep(5000);
                           } catch (InterruptedException e1) {
                               e1.printStackTrace();
                           }
                       }
                   }

               }
               if (gameIsRunning) {
                   /**
                   Gilt für alle Spieler
                    */
                   int serverStoneCount = -1;
                   try {
                       //Prüfen ob neue Steine vorhanden
                       serverStoneCount = rmiServerInterface.countStones(gameID);
                   } catch (RemoteException e) {
                       e.printStackTrace();
                   }catch (NullPointerException e){
//                       e.printStackTrace();
                       System.out.println("NullPointer in countStone");
                   }
                   int sleepTime = 5000;
                   if(stoneCount <= serverStoneCount){
                       try{
                           ArrayList<Stone> allStones = rmiServerInterface.getStone(gameID, true);
                           int[] gameControl = rmiServerInterface.getGameControl(gameID);
                           stoneCount = allStones.size();

                           for(Stone stone : allStones){
                               if (gameUI.getBoard().checkPoint(stone.getPoint())){
                                   gameUI.getBoard().addStone(stone);
                               }

                           }
                           if(gameControl[0] == RmiServerInterface.CGAMESTATE){
                               gameUI.setGameState(gameControl[1]);

                           }else if(gameControl[0] == RmiServerInterface.CBOARDFULL){
                               gameUI.setGameBrake("Alle Felder sind belegt. Das Spiel endet unentschieden.", true);
                               stopGameThread();
                               startRequestThread();
                               connectionTimeline.play();

                           }else if(gameControl[0] == RmiServerInterface.CGAMEWINNER){
                               stopGameThread();
                               startRequestThread();
                               connectionTimeline.play();
                               if(gameControl[1] == 0){
                                   gameUI.setGameBrake("Spieler Schwarz hat gewonnen!", true);
                               }else{
                                   gameUI.setGameBrake("Spieler Weiß hat gewonnen!", true);
                               }
                           }
                           sleepTime = 1500;
                       }catch (Exception ex){
                           ex.printStackTrace();
                       }
                   }
                   try {
                       sleep(sleepTime);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
           }
        }
    };

    public RmiClient(BasicUIX uix){
       this.gameUI = uix;
   }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Connect6 - MultiplayerClient");
        mainStage = primaryStage;

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        xWindowPos = gameUI.getPrimaryStage().getX() + scene.getWidth() /2;
        yWindowPos = gameUI.getPrimaryStage().getY() + scene.getHeight() /3;
        primaryStage.setX(xWindowPos);
        primaryStage.setY(yWindowPos);

        BorderPane borderPane = new BorderPane();
        GridPane topGridPane = new GridPane();
        Button btnConnect = new Button("Connect");
        Button btnDisconnect = new Button("Disconnect");
        Label labelState  = new Label("Verbindunsstatus:");
        Label labelUsername = new Label("Benutzername:");

        borderPane.setCenter(listView);
        borderPane.setTop(topGridPane);
        borderPane.setPadding(new Insets(10,10,10,10));
        borderPane.setMargin(listView, new Insets(10,0,0,0));

        topGridPane.add(labelUsername, 1,1);
        topGridPane.add(tfUserName,2,1);
        topGridPane.add(labelServerIp, 1, 2);
        topGridPane.add(tfServerIP, 2, 2);
        topGridPane.add(btnConnect, 1,3);
        topGridPane.add(btnDisconnect,2,3);
        topGridPane.add(labelState,3,3);
        topGridPane.add(labelConnectionState, 4,3);
        topGridPane.setHgap(5);
        topGridPane.setVgap(10);

        listView.setItems(opponents);
        tfUserName.setText(System.getProperty("user.name"));

        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();

        btnConnect.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
                    if (clickedName != null && !clickedName.equals(noOpponentFound)) {
                        gameID = rmiServerInterface.requestOpponent(tfUserName.getText(), clickedName);
                        requestMode = true;
                    }
                }catch (Exception ex){
                    System.out.println("ListClickeError:"+ex);
                }
            }
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                disconnectFromServer();
                stopGameThread();
                stopRequestThread();
                connectionTimeline.stop();
            }
        });

    }

    public void sendStoneToServer(Stone stone){
        try {
            rmiServerInterface.setStone(gameID, stone);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendGameStateToServer(int gameState){
        try {
            rmiServerInterface.setGameControl(gameID, RmiServerInterface.CGAMESTATE,gameState);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendBoardFullToServer(){
        try {
            rmiServerInterface.setGameControl(gameID, RmiServerInterface.CBOARDFULL,0);
            rmiServerInterface.setRequestState(gameID, RmiServerInterface.FINISH);
            runningGameThread = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendGameWonToServer(boolean color){
        try {
            rmiServerInterface.setGameControl(gameID, RmiServerInterface.CGAMEWINNER,color ? 0 :1);
            rmiServerInterface.setRequestState(gameID, RmiServerInterface.FINISH);
            runningGameThread = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer(){
        String host = tfServerIP.getText();

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            rmiServerInterface = (RmiServerInterface) registry.lookup("Server");

            rmiServerInterface.addClient(tfUserName.getText());

            labelConnectionState.setText("Mit dem Server verbunden!");
            labelConnectionState.setTextFill(Color.GREEN);
            state = true;
            tfUserName.setDisable(true);
            tfServerIP.setDisable(true);
            //TODO REMOVE
            System.out.println("connection: "+connectionTimeline.getStatus());

            connectionTimeline.setCycleCount(Timeline.INDEFINITE);
            connectionTimeline.play();
            startRequestThread();
            //TODO REMOVE
            System.out.println("connection: "+connectionTimeline.getStatus());

        }catch (Exception e){
            System.out.println("Connect Error: "+e);
            if(e.toString().contains("Connection refused")){
                labelConnectionState.setText("Server nicht erreichbar!");
                labelConnectionState.setTextFill(Color.RED);
            }
        }
    }

    private void disconnectFromServer(){
        if(state){
            try {
                if(gameID != 0) rmiServerInterface.setRequestState(gameID, RmiServerInterface.FINISH);
                rmiServerInterface.removeClient(tfUserName.getText());
                tfUserName.setDisable(false);
                tfServerIP.setDisable(false);
                labelConnectionState.setText("Nicht verbunden!");
                gameID = 0;
            }catch (Exception e){
                System.out.println("Discconect Error: "+e);
            }finally {
                listView.getItems().clear();
                try {
                    connectionTimeline.stop();
                    stopRequestThread();
                    stopGameThread();
                }catch (Exception e){
                    System.out.println("StopThread Error: "+e);
                }
            }


        }
    }

    private void toBack(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    mainStage.toBack();
                    gameUI.getPrimaryStage().toFront();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startRequestThread(){
        dialogIsShowing = false;
        runningRequestThread = true;
        new Thread(runnableRequest).start();
    }

    private void stopRequestThread(){
        runningRequestThread = false;
    }

    private void startGameThread(){
        runningGameThread = true;
        new Thread(runnableGame).start();
    }

    private void stopGameThread(){
        runningGameThread = false;
    }
}
