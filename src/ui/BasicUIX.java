package ui;

import game.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;
import ki.Bot;
import rmi_server.*;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Micha Heiß
 */
public class BasicUIX extends Application {

    private Stage primaryStage;
    private Scene scene;
    private VBox menu_GameSelect;
    private Canvas canvas;
    private GraphicsContext c;

    private Boolean canvasAllowUserInput = false;
    private Boolean color = true;

    private GameState gameState;
    private GameType gameType = GameType.NONE;

    private Board board;
    private double boxWidth;
    private double boardSize;

    private Bot SinglePlayerBot;

    private Timeline timer;

    //Für Multiplayer
    RmiClient rmiClient;
    Timeline rederTimer;
    private double xWindowPos;
    private double yWindowPos;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // window setup
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Connect6");
        CanvasPane canvasPane = new CanvasPane(1000,1000);
        canvas = canvasPane.getCanvas();
        c = canvas.getGraphicsContext2D();
        BorderPane root = new BorderPane(canvasPane);
        scene = new Scene(root, 1150, 1000);
        primaryStage.setScene(scene);
        root.setRight(getMenu_GameSelect());
        scene.getStylesheets().add("css/styles.css");

        primaryStage.show();

        xWindowPos = primaryStage.getX() + scene.getWidth() /2;
        yWindowPos = primaryStage.getY() + scene.getHeight() /3;

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                render();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                render();
            }
        });
        canvas.setOnMouseMoved(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();

                Color hovercolor;
                if(color){
                    hovercolor = Color.rgb(0,0,0, .3);
                }else {
                    hovercolor = Color.rgb(255,255,255, .3);
                }
                drawHover(getFieldX(x), getFieldY(y), hovercolor);
            }
        });
        canvas.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    double x = event.getX();
                    double y = event.getY();

                    setStone(getFieldX(x), getFieldY(y));

                }
            }
        });


    }

    private void render(){

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if(width<height){
            boardSize = width;
        }else {
            boardSize = height;
        }

        // draw background
        c.setFill(Color.WHITE);
        c.fillRect(0,0,width,height);

        // draw board background
        if(board == null) return;
        double boardXPOS = (width-boardSize)/2;
        double boardYPOS = (height-boardSize)/2;
        int dimensions = board.getDimension();
        boxWidth = boardSize/dimensions;

        Image image = new Image("images/field.png");
        //asdasdasd

//        c.setFill(new RadialGradient(0, 0, boardSize/2+boardXPOS, boardSize/2+boardYPOS, boardSize, true,
//                CycleMethod.REFLECT,
//                new Stop(0.0, Color.rgb(221,240,255)),
//                new Stop(1.0, Color.rgb(249,252,255))));
//        c.fillRect(boardXPOS,boardYPOS, boardSize, boardSize);

        for(int i = 0; i<dimensions; i++){
            for(int j = 0; j<dimensions;j++){
                if(j%2==0) {
                    if(i%2==0){
                        c.setFill(Color.BLUE);
                    }else {
                        c.setFill(Color.CADETBLUE);
                    }
                }else{
                    if(i%2!=0){
                        c.setFill(Color.BLUE);
                    }else {
                        c.setFill(Color.CADETBLUE);
                    }
                }
                c.fillRect(j*boxWidth+boardXPOS, i*boxWidth+boardYPOS, boxWidth, boxWidth);
                //c.drawImage(image, j*boxWidth+boardXPOS, i*boxWidth+boardYPOS, boxWidth, boxWidth);
            }
        }

        ArrayList<Stone> black = board.getBlackStones();
        ArrayList<Stone> white = board.getWhiteStones();

        for(Stone s : black){
            BoardPoint b = s.getPoint();
            int x = b.getX();
            int y = b.yPos-1;
            drawStone(x,y,Color.BLACK);
        }
        for(Stone s : white){
            BoardPoint b = s.getPoint();
            int x = b.getX();
            int y = b.yPos-1;
            drawStone(x,y,Color.WHITE);
        }
    }

    private enum GameState{

        FIRSTMOVE, WHITE, WHITESECOND, BLACK, BLACKSECOND, NEXT

    }
    private enum GameType{
        NONE, MULTIPLAYER, SINGLEPLAYER, BOT, BOTVBOT
    }

    private static class CanvasPane extends Pane {

        private final Canvas canvas;

        public CanvasPane(double width, double height) {
            canvas = new Canvas(width, height);
            getChildren().add(canvas);
        }

        public Canvas getCanvas() {
            return canvas;
        }

        @Override
        protected void layoutChildren() {
            final double x = snappedLeftInset();
            final double y = snappedTopInset();
            final double w = snapSize(getWidth()) - x - snappedRightInset();
            final double h = snapSize(getHeight()) - y - snappedBottomInset();
            canvas.setLayoutX(x);
            canvas.setLayoutY(y);
            canvas.setWidth(w);
            canvas.setHeight(h);
        }
    }

    private VBox getMenu_GameSelect(){

        // init
        if(menu_GameSelect == null){
            menu_GameSelect = new VBox();

            Label l = new Label(" ");
            menu_GameSelect.getChildren().add(l);

            // create buttons for menu
            Button button = new Button("Neues Spiel");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Fensterauswahl");
                    alert.setHeaderText(null);
                    alert.setContentText("Soll das Spiel in einem neuen Fenster gestartet werden?");

                    ButtonType buttonTypeE = new ButtonType("ja");
                    ButtonType buttonTypeS = new ButtonType("nein", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeE,buttonTypeS);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        if(result.get() == buttonTypeE){
                            Stage newstage = new Stage();
                            BasicUIX b = new BasicUIX();
                            newstage.setScene(b.getScene());
                            try{
                                b.start(newstage);
                                b.startSingle();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            startSingle();
                        }
                    }
                }
            });
            button.setId("button");
            menu_GameSelect.getChildren().add(button);

            button = new Button("1vBot");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Fensterauswahl");
                    alert.setHeaderText(null);
                    alert.setContentText("Soll das Spiel in einem neuen Fenster gestartet werden?");

                    ButtonType buttonTypeE = new ButtonType("ja");
                    ButtonType buttonTypeS = new ButtonType("nein", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeE,buttonTypeS);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        if(result.get() == buttonTypeE){
                            Stage newstage = new Stage();
                            BasicUIX b = new BasicUIX();
                            newstage.setScene(b.getScene());
                            try{
                                b.start(newstage);
                                b.startSingleBot();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            startSingleBot();
                        }
                    }
                }
            });
            button.setId("button");
            menu_GameSelect.getChildren().add(button);

            button = new Button("Multiplayer");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Fensterauswahl");
                    alert.setHeaderText(null);
                    alert.setContentText("Soll das Spiel in einem neuen Fenster gestartet werden?");

                    ButtonType buttonTypeE = new ButtonType("ja");
                    ButtonType buttonTypeS = new ButtonType("nein", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeE,buttonTypeS);
                    Optional<ButtonType> result = alert.showAndWait();


                    if (result.isPresent()) {
                        if(result.get() == buttonTypeE){
                            Stage newstage = new Stage();
                            BasicUIX b = new BasicUIX();
                            newstage.setScene(b.getScene());
                            try{
                                b.start(newstage);
                                b.startMulti();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            startMulti();
                        }
                    }
                }
            });
            button.setId("button");
            menu_GameSelect.getChildren().add(button);

            button = new Button("BotvBot");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Fensterauswahl");
                    alert.setHeaderText(null);
                    alert.setContentText("Soll das Spiel in einem neuen Fenster gestartet werden?");

                    ButtonType buttonTypeE = new ButtonType("ja");
                    ButtonType buttonTypeS = new ButtonType("nein", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeE,buttonTypeS);
                    Optional<ButtonType> result = alert.showAndWait();


                    if (result.isPresent()) {
                        if(result.get() == buttonTypeE){
                            Stage newstage = new Stage();
                            BasicUIX b = new BasicUIX();
                            newstage.setScene(b.getScene());
                            try{
                                b.start(newstage);
                                b.startBot();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            startBot();
                        }
                    }
                }
            });
            button.setId("button");
            menu_GameSelect.getChildren().add(button);
            menu_GameSelect.setMinWidth(150d);
            menu_GameSelect.setSpacing(10);
//            menu_GameSelect.setStyle("-fx-background-color: darkred");

        }
        return menu_GameSelect;

    }

    private void startBot() {

        canvasAllowUserInput = false;
        board = new Board(getBoardDimensions(xWindowPos,yWindowPos));
        boolean enableHardMode1 = false;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bot 1");
        alert.setHeaderText(null);
        alert.setContentText("Welche Stufe soll der 1. Bot (schwarz) haben?");

        ButtonType buttonTypeE = new ButtonType("Einfach");
        ButtonType buttonTypeS = new ButtonType("Schwer");

        alert.getButtonTypes().setAll(buttonTypeE,buttonTypeS);
        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent()) {
            if(result.get() == buttonTypeS){
                enableHardMode1 = true;
            }else if (result.get() == buttonTypeE){
                enableHardMode1 = false;
            }else {
                enableHardMode1 = false;
            }
        }


        boolean enableHardMode2 = false;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bot 2");
        alert.setHeaderText(null);
        alert.setContentText("Welche Stufe soll der 2. Bot (weiß) haben?");

        buttonTypeE = new ButtonType("Einfach");
        buttonTypeS = new ButtonType("Schwer");

        alert.getButtonTypes().setAll(buttonTypeE,buttonTypeS);
        result = alert.showAndWait();

        if(result.get() == buttonTypeS){
            enableHardMode2 = true;
        }else if (result.get() == buttonTypeE){
            enableHardMode2 = false;
        }else {
            enableHardMode2 = false;
        }
        color = false;

        Bot blackBot = new Bot(board, true, enableHardMode1,false);
        Bot whiteBot = new Bot(board, false, enableHardMode2,false);

        blackBot.next();
        render();


        timer = new Timeline(new KeyFrame(javafx.util.Duration.seconds(.5d), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startBot(blackBot, whiteBot);
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

    }

    private void startBot(Bot blackBot, Bot whiteBot){


        boolean run = true;
        String winningPhrase = "";
        String errorPhrase = "";

        for(int i = 1; i <= 2; i++){
            if(!errorPhrase.equals("")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fehler!");
                alert.setHeaderText(null);
                alert.setContentText(errorPhrase);

                timer.stop();
                alert.show();
            }
            errorPhrase = "";
            try{
                if(color){
                    blackBot.next();
                    render();
                }else{
                    whiteBot.next();
                    render();

                }
                board.checkWinner();

            }catch (GameException.GameWonException e) {
                winningPhrase = e.toString();
                break;

            }catch (GameException.BoardFullException e){
                winningPhrase = e.toString();
                break;
            }catch (Exception e) {

                errorPhrase = e.toString();
                i--;

            }
        }
        color = !color;
        render();
        if(!winningPhrase.equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Spiel beendet!");
            alert.setHeaderText(null);
            alert.setContentText(winningPhrase);

            timer.stop();
            alert.show();
        }

    }

    private void startMulti() {
        rmiClient = new RmiClient(this);

        try {
            rmiClient.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        rederTimer = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                render();
            }
        }));
        rederTimer.setCycleCount(Timeline.INDEFINITE);

    }

    private void startSingleBot() {

        boolean enableHardMode = false;

        gameType = GameType.BOT;
        gameState = GameState.FIRSTMOVE;
        board = new Board(getBoardDimensions(xWindowPos,yWindowPos));
        canvasAllowUserInput = true;
        render();
        color = true;

        //TODO tell user what to do

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("dein Gegner");
        alert.setHeaderText(null);
        alert.setContentText("Welche Stufe soll der Bot haben?");

        ButtonType buttonTypeE = new ButtonType("Einfach");
        ButtonType buttonTypeS = new ButtonType("Schwer");

        alert.getButtonTypes().setAll(buttonTypeE,buttonTypeS);
        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent()) {
            if(result.get() == buttonTypeS){
                enableHardMode = true;
            }else if (result.get() == buttonTypeE){
                enableHardMode = false;
            }else {
                enableHardMode = false;
            }
        }

        SinglePlayerBot = new Bot(board, false, enableHardMode, false);


    }

    public void startSingle() {

        gameType = GameType.SINGLEPLAYER;
        gameState = GameState.FIRSTMOVE;
        board = new Board(getBoardDimensions(xWindowPos,yWindowPos));
        canvasAllowUserInput = true;
        render();
        color = true;

        //TODO tell user what to do

    }

    private void drawStone(int x, int y, Color color){
        double boardXPOS = (canvas.getWidth()-boardSize)/2;
        double boardYPOS = (canvas.getHeight()-boardSize)/2;
        c.setFill(color);
        c.fillOval(x*boxWidth+boardXPOS, y*boxWidth+boardYPOS, boxWidth, boxWidth);
    }

    private String getChoice(String title, String header, String content, ArrayList<String> choices, double xPosition, double yPosition){

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        if((xPosition != 0) && (yPosition != 0)){
            dialog.setX(xPosition);
            dialog.setY(yPosition);
        }

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent())return result.get();
        return "";
    }

    public int getBoardDimensions(double xPosition, double yPosition) {
        ArrayList<String> choices = new ArrayList<>();
        for(int i = 6; i<=20; i++) choices.add(""+i);
        String choice = getChoice("Brettgroesse", "Bitte waehle die Brettgroesse", "Seitenlaenge: ", choices,xPosition,yPosition);
        if(choice == "") return getBoardDimensions(xPosition,yPosition);
        else return Integer.parseInt(choice);
    }

    private void drawHover(int x, int y, Color color) {
        if(canvasAllowUserInput){
            double boardXPOS = (canvas.getWidth()-boardSize)/2;
            double boardYPOS = (canvas.getHeight()-boardSize)/2;
            if(x<0 || y<0 || x>board.getDimension()-1 || y>board.getDimension()-1) return;
            render();
            c.setFill(color);
            c.fillOval(x*boxWidth+boardXPOS, y*boxWidth+boardYPOS, boxWidth, boxWidth);
        }
    }

    private int getFieldX(double x){
        double boardXPOS = (canvas.getWidth()-boardSize)/2;
        return (int) ((x-boardXPOS)/boxWidth);
    }
    private int getFieldY(double y){
        double boardYPOS = (canvas.getHeight()-boardSize)/2;
        return (int) ((y-boardYPOS)/boxWidth);
    }

    public Scene getScene() {
        return scene;
    }

    private void setStone(int x, int y){

        if(canvasAllowUserInput){
            String winningPhrase = "";
            String errorPhrase = "";
            if (x >= 0 && y >= 0 && y < board.getDimension() && x <board.getDimension()) {
                y++;//Muss für das Board in der Console erhöht werden
                switch (gameType){
                    case SINGLEPLAYER:

                        winningPhrase = "";
                        errorPhrase = "";

                        try{

                            board.addStone(new Stone(new BoardPoint(BoardPoint.getX(x),y), color));
                            render();
                            board.checkWinner();

                            gameState = GameState.values()[gameState.ordinal()+1];
                            if(gameState.ordinal() > GameState.BLACKSECOND.ordinal())
                                gameState = GameState.WHITE;
                            if(gameState.equals(GameState.BLACK) || gameState.equals(GameState.BLACKSECOND))
                                color = true;
                            if(gameState == GameState.WHITE || gameState == GameState.WHITESECOND)
                                color = false;

                        }catch (GameException.GameWonException e) {
                            canvasAllowUserInput = false;
                            winningPhrase = e.toString();
                        }catch (GameException.BoardOutOfBoundException e) {
                            errorPhrase = e.toString();

                        }catch (GameException.BoardFullException e) {
                            canvasAllowUserInput = false;
                            winningPhrase = e.toString();
                        }catch (Exception e) {
                            errorPhrase = e.toString();
                        }finally {
                            render();
                        }

                        if(!winningPhrase.equals("")){
                            canvasAllowUserInput = false;
                            gameType = GameType.NONE;
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Spiel beendet!");
                            alert.setHeaderText(null);
                            alert.setContentText(winningPhrase);

                            alert.showAndWait();
                        }
                        if(!errorPhrase.equals("")){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Fehler!");
                            alert.setHeaderText(null);
                            alert.setContentText(errorPhrase);

                            alert.showAndWait();
                        }
                        break;

                    case BOT:

                        winningPhrase = "";
                        errorPhrase = "";

                        try{

                            if(gameState == GameState.FIRSTMOVE || gameState == GameState.BLACK || gameState == GameState.BLACKSECOND){
                                color = true;
                                board.addStone(new Stone(new BoardPoint(BoardPoint.getX(x),y), color));
                                render();
                                board.checkWinner();
                                if(gameState == GameState.BLACKSECOND){
                                    gameState = GameState.WHITE;
                                }else{
                                    gameState = GameState.values()[gameState.ordinal()+1];
                                }

                            }
                            if(gameState == GameState.WHITE){
                                // Bot moves

                                color = false;
                                SinglePlayerBot.next();
                                SinglePlayerBot.next();
                                board.checkWinner();
                                gameState = GameState.BLACK;

                            }


                        }catch (GameException.GameWonException e) {
                            canvasAllowUserInput = false;
                            winningPhrase = e.toString();
                        }catch (GameException.BoardOutOfBoundException e) {
                            errorPhrase = e.toString();

                        }catch (GameException.BoardFullException e) {
                            canvasAllowUserInput = false;
                            winningPhrase = e.toString();
                        }catch (Exception e) {
                            errorPhrase = e.toString();
                        }finally {
                            render();
                        }

                        if(!winningPhrase.equals("")){
                            canvasAllowUserInput = false;
                            gameType = GameType.NONE;
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Spiel beendet!");
                            alert.setHeaderText(null);
                            alert.setContentText(winningPhrase);

                            alert.showAndWait();
                        }
                        if(!errorPhrase.equals("")){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Fehler!");
                            alert.setHeaderText(null);
                            alert.setContentText(errorPhrase);

                            alert.showAndWait();
                        }

                        break;

                    case MULTIPLAYER:
                        try{
                            boolean ableToPlay = false;
                            board.checkWinner();

                            if(gameState.ordinal() > GameState.BLACKSECOND.ordinal())
                                gameState = GameState.WHITE;

                            if((gameState.equals(GameState.BLACK) || gameState.equals(GameState.BLACKSECOND) ||
                                    gameState.equals(GameState.FIRSTMOVE)) && color){
                                ableToPlay = true;
                            }
                            if((gameState == GameState.WHITE || gameState == GameState.WHITESECOND) && !color){
                                ableToPlay = true;
                            }
                            if (ableToPlay){
                                Stone stone = new Stone(new BoardPoint(BoardPoint.getX(x),y), color);
                                board.addStone(stone);
                                render();
                                rmiClient.sendStoneToServer(stone);
                                gameState = GameState.values()[gameState.ordinal()+1];
                                rmiClient.sendGameStateToServer(gameState.ordinal());
                                board.checkWinner();
                            }

                        }catch (GameException.GameWonException e) {
                            canvasAllowUserInput = false;
                            rmiClient.sendGameWonToServer(color);
                            winningPhrase = e.toString();
                        }catch (GameException.BoardOutOfBoundException e) {
                            errorPhrase = e.toString();

                        }catch (GameException.BoardFullException e) {
                            canvasAllowUserInput = false;
                            winningPhrase = e.toString();
                            rmiClient.sendBoardFullToServer();
                        }catch (NullPointerException e){

                        }catch (Exception e) {
                            errorPhrase = e.toString();
                        }finally {
                            render();
                        }

                        if(!winningPhrase.equals("")){
                            setGameBrake(winningPhrase, true);
                        }
                        if(!errorPhrase.equals("")){
                            setGameBrake(errorPhrase, false);
                        }
                        break;

                }
            }

        }

    }

    public void setBoard(Board board){
        this.board = board;
    }

    public Board getBoard(){
        return board;
    }

    public void startMultiGame(boolean color){
        this.color = color;
        rederTimer.play();
        gameType = GameType.MULTIPLAYER;
        if(color){
            gameState = GameState.FIRSTMOVE;

        }
        canvasAllowUserInput = true;
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public void setGameState(int state){
        gameState = GameState.values()[state];
    }

    public void setGameBrake(String text, boolean stop){
        final String dialogText;
        if (stop) {
            canvasAllowUserInput = false;
            gameType = GameType.NONE;
            dialogText = new String("Spiel beendet!");
        }else {
            dialogText = new String("Fehler!");
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(dialogText);
                alert.setHeaderText(null);
                alert.setContentText(text);
                System.out.println("GameBrake "+stop);
                alert.show();
            }
        });
    }

}
