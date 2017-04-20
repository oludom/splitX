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

    private boolean enableLog = false;
    private RmiClientLog clientLog = new RmiClientLog(this);
    private String logInfo = "INFO: ";
    private String logError = "ERROR: ";
    private String logException = "Exception: ";

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
            }catch (Exception e){
                errorRate++;
                if(enableLog) {
                    String logText = logException + "Beim Abfragen der Gegnerliste vom Server - "+e;
                    System.out.println(logText);
                    clientLog.addLogItem(logText);
                    e.printStackTrace();
                }
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

                    if(enableLog){
                        String logText = logInfo + tfUserName.getText()+" isShowing:"+dialogIsShowing + " requestMode: "+ requestMode;
                        System.out.println(logText);
                        clientLog.addLogItem(logText);
                    }
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
                                    //Fensterpostition anpassen
                                    //infoDialog.setX(xWindowPos + infoDialog.getWidth() / 2);
                                    //infoDialog.setY(yWindowPos + infoDialog.getHeight() / 2);

                                    Optional<ButtonType> result = infoDialog.showAndWait();
                                    if(result.get() == ButtonType.OK){

                                        try {
                                            rmiServerInterface.setRequestState(gameID, RmiServerInterface.ACCEPT);
                                            stopRequestThread();

                                            //hier beginnt das Spiel
                                            gameBeginner = false;
                                            startGameThread();

                                        }catch (Exception e){
                                            if(enableLog){
                                                String logText = logException + "Dialog RMI-Anfrage OK-Button -" + e;
                                                clientLog.addLogItem(logText);
                                                e.printStackTrace();
                                            }
                                        }finally {
                                            dialogIsShowing = false;
                                            if(enableLog){
                                                String logText = logInfo + "Das Spiel wird angenommen";
                                                System.out.println(logText);
                                                clientLog.addLogItem(logText);
                                            }
                                        }
                                    }else{

                                        try {
                                            rmiServerInterface.setRequestState(gameID, RmiServerInterface.DECLINED);

                                        }catch (Exception e){
                                            if(enableLog){
                                                String logText = logException + "Dialog RMI-Anfrage Abbrechen-Button -" + e;
                                                clientLog.addLogItem(logText);
                                                e.printStackTrace();
                                            }
                                        }finally {
                                            dialogIsShowing = false;
                                            if(enableLog) {
                                                String logText = logInfo + "Das Spiel wird abgelehnt";
                                                System.out.println(logText);
                                                clientLog.addLogItem(logText);
                                            }
                                        }
                                    }
                                }
                            });

                        }

                    }else  if(requestMode && !dialogIsShowing){
                        int state = -1;
                        try{
                            state = rmiServerInterface.getRequestState(gameID);
                        }catch (Exception e){
                            if(enableLog) {
                                String logText = logException + "Beim Abfragen des Status der Anfrage - Status: "+state+" - "+e;
                                System.out.println(logText);
                                clientLog.addLogItem(logText);
                            }
                        }

                        if(state == RmiServerInterface.ACCEPT){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    dialogIsShowing = true;
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Anfrage");
                                    alert.setContentText("Anfrage wurde angenommen");
                                    //Fenster Position anpassen
                                    //System.out.println("Poition:"+mainStage.getX() + " | " + xWindowPos);
                                    //alert.setX(mainStage.getX() + alert.getWidth() /3);
                                    //alert.setY(mainStage.getY() + alert.getHeight() / 3);
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if(result.get() == ButtonType.OK){

                                        try {
                                            rmiServerInterface.setRequestState(gameID,RmiServerInterface.RUNNING);
                                            stopRequestThread();

                                            //hier beginnt das Spiel
                                            startGameThread();

                                        } catch (RemoteException e) {
                                            if(enableLog) {
                                                String logText = logException + "Beim Setzen des Status auf RUNNING - "+e;
                                                System.out.println(logText);
                                                clientLog.addLogItem(logText);
                                                e.printStackTrace();
                                            }
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
                }catch (Exception e){
                    if(!enableLog) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    clientLog.start(new Stage());
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                String logText = logError + "Allgemeiner Fehler bei der Prüfung von Abfragen auf dem Server." +
                                        "\nBitte Starten Sie das Programm und/oder den Server neu!\n"+e;
                                clientLog.addLogItem(logText);
                            }
                        });
                        stopRequestThread();
                        stopGameThread();
                        connectionTimeline.stop();
                    }
                    if(enableLog){
                        String logText = logError + "Allgemeiner Fehler bei der Prüfung von Abfragen auf dem Server." +
                                "\nBitte Starten Sie das Programm oder den Server neu!\n"+e;
                        System.out.println(logText);
                        clientLog.addLogItem(logText);
                        e.printStackTrace();
                    }

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
                           if(enableLog) {
                               String logText = logException + "Beim Sleep des Threads nach Abfrage der Boardgröße - "+e;
                               System.out.println(logText);
                               clientLog.addLogItem(logText);
                               e.printStackTrace();
                           }
                       }
                   }
                   //TODO ANPASSEN
//                   try {
//                       sleep(1000);
//                   } catch (InterruptedException e) {
//                       e.printStackTrace();
//                   }

                   if (boardDimension > 5 && !checkBoardDim) {
                       try {
                           rmiServerInterface.setGameControl(gameID, RmiServerInterface.CBORDDIM, boardDimension);
                           checkBoardDim = true;
                           gameIsRunning = true;
                           toBack();
                       } catch (RemoteException e) {
                           if(enableLog) {
                               String logText = logException + "Beim Übertragen der Brettgröße an den Server - "+e;
                               System.out.println(logText);
                               clientLog.addLogItem(logText);
                               e.printStackTrace();
                           }
                       }
                   }

               }else if (!gameIsRunning){
                   //Spieler Weiß
                   if(boardDimension < 0){
                       int[] gameControl = new int[2];
                       try {
                            gameControl = rmiServerInterface.getGameControl(gameID);
                       } catch (RemoteException e) {
                           if(enableLog) {
                               String logText = logException + "Beim Abfragen der Brettgröße vom Server - "+e;
                               System.out.println(logText);
                               clientLog.addLogItem(logText);
                               e.printStackTrace();
                           }
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
                           if(enableLog) {
                               String logText = logException + "Beim Setzen der Brettgröße - "+e;
                               System.out.println(logText);
                               clientLog.addLogItem(logText);
                               e.printStackTrace();
                           }
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
                       if(enableLog) {
                           String logText = logException + "Beim Abfragen der Steinanzahl von Server - "+e;
                           System.out.println(logText);
                           clientLog.addLogItem(logText);
                           e.printStackTrace();
                       }
                   }catch (NullPointerException e){

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
                       }catch (Exception e){
                           if(enableLog) {
                               String logText = logException + "Beim Abfragen der Steine vom Server- "+e;
                               System.out.println(logText);
                               clientLog.addLogItem(logText);
                               e.printStackTrace();
                           }
                       }
                   }
                   try {
                       sleep(sleepTime);
                   } catch (InterruptedException e) {

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
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
        xWindowPos = gameUI.getPrimaryStage().getX() + scene.getWidth() /2;
        yWindowPos = gameUI.getPrimaryStage().getY() + scene.getHeight() /3;
        primaryStage.setX(xWindowPos);
        primaryStage.setY(yWindowPos);
        scene.getStylesheets().add("css/styles.css");

        BorderPane borderPane = new BorderPane();
        GridPane topGridPane = new GridPane();
        Button btnConnect = new Button("Connect");
        Button btnDisconnect = new Button("Disconnect");
        Button btnClientLog  = new Button("Log öffnen");
        Label labelState  = new Label("Verbindunsstatus:");
        Label labelUsername = new Label("Benutzername:");

        btnConnect.setId("button");
        btnClientLog.setId("button");
        btnDisconnect.setId("button");

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
        topGridPane.add(btnClientLog, 4, 1);
        topGridPane.setHgap(5);
        topGridPane.setVgap(10);

        topGridPane.getStyleClass().add("font");
        tfServerIP.getStyleClass().add("size");
        tfUserName.getStyleClass().add("size");
        listView.getStyleClass().add("font");

        listView.setItems(opponents);
        tfUserName.setText(System.getProperty("user.name"));

        root.getChildren().add(borderPane);
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

        btnClientLog.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openClientLog();
            }
        });

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String clickedName = (String) listView.getSelectionModel().getSelectedItem();
                if(enableLog) {
                    String logText = logInfo + "Clicked:" +clickedName;
                    System.out.println(logText);
                    clientLog.addLogItem(logText);
                }
                try{
                    if (clickedName != null && !clickedName.equals(noOpponentFound)) {
                        gameID = rmiServerInterface.requestOpponent(tfUserName.getText(), clickedName);
                        requestMode = true;
                    }
                }catch (Exception e){
                    if(enableLog) {
                        String logText = logException + "Im Click-Event in der Gegnerliste - "+e;
                        System.out.println(logText);
                        clientLog.addLogItem(logText);
                        e.printStackTrace();
                    }
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
            if(enableLog) {
                String logText = logException + "Beim Senden des Steins an den Server - "+e;
                System.out.println(logText);
                clientLog.addLogItem(logText);
                e.printStackTrace();
            }
        }
    }

    public void sendGameStateToServer(int gameState){
        try {
            rmiServerInterface.setGameControl(gameID, RmiServerInterface.CGAMESTATE,gameState);
        } catch (RemoteException e) {
            if(enableLog) {
                String logText = logException + "Beim Senden des Spielstatus an den Server - "+e;
                System.out.println(logText);
                clientLog.addLogItem(logText);
                e.printStackTrace();
            }
        }
    }

    public void sendBoardFullToServer(){
        try {
            rmiServerInterface.setGameControl(gameID, RmiServerInterface.CBOARDFULL,0);
            rmiServerInterface.setRequestState(gameID, RmiServerInterface.FINISH);
            stopGameThread();
        } catch (RemoteException e) {
            if(enableLog) {
                String logText = logException + "Beim Senden des Brett voll an den Server - "+e;
                System.out.println(logText);
                clientLog.addLogItem(logText);
                e.printStackTrace();
            }
        }
    }

    public void sendGameWonToServer(boolean color){
        try {
            rmiServerInterface.setGameControl(gameID, RmiServerInterface.CGAMEWINNER,color ? 0 :1);
            rmiServerInterface.setRequestState(gameID, RmiServerInterface.FINISH);
            stopGameThread();
        } catch (RemoteException e) {
            if(enableLog) {
                String logText = logException + "Beim Senden des Gewinners an den Server - "+e;
                System.out.println(logText);
                clientLog.addLogItem(logText);
                e.printStackTrace();
            }
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
            connectionTimeline.setCycleCount(Timeline.INDEFINITE);
            connectionTimeline.play();
            startRequestThread();
        }catch (Exception e){
            if(enableLog) {
                String logText = logException + "Beim Verbinden mit dem Server - "+e;
                System.out.println(logText);
                clientLog.addLogItem(logText);
                e.printStackTrace();
            }
            if(e.toString().contains("Connection refused")){
                labelConnectionState.setText("Server nicht erreichbar!");
                labelConnectionState.setTextFill(Color.RED);
            }
            if(e.toString().contains("Der Username wird Bereits verwendet. Bitte Neuen wählen")){
                labelConnectionState.setText("Der Username wird Bereits verwendet.\nBitte Neuen wählen");
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
                if(enableLog) {
                    String logText = logException + "Beim Trennen vom Server - "+e;
                    System.out.println(logText);
                    clientLog.addLogItem(logText);
                    e.printStackTrace();
                }
            }finally {
                listView.getItems().clear();
                try {
                    connectionTimeline.stop();
                    stopRequestThread();
                    stopGameThread();
                }catch (Exception e){
                    if(enableLog) {
                        String logText = logException + "Beim Beenden der Serverabfragen - "+e;
                        System.out.println(logText);
                        clientLog.addLogItem(logText);
                        e.printStackTrace();
                    }
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
                    if(enableLog) {
                        String logText = logException + "Beim Verschieben der Fenster - "+e;
                        System.out.println(logText);
                        clientLog.addLogItem(logText);
                        e.printStackTrace();
                    }
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

    private void openClientLog(){
        enableLog = true;
        try {
            clientLog.start(new Stage());
        } catch (Exception e) {
            String logText = logException + "Beim Öffnen des Logs - "+e;
            System.out.println(logText);
            e.printStackTrace();
        }
    }

    public void stopLog(){
        enableLog =false;
    }
}
