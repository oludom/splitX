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
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ki.Bot;

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

    Timeline timer;

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

        primaryStage.show();

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

                drawHover(getFieldX(x), getFieldY(y), Color.GRAY);
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

        for(int i = 0; i<dimensions; i++){
            for(int j = 0; j<dimensions;j++){
                if(j%2==0) {
                    if(i%2==0){
                        c.setFill(Color.SANDYBROWN);
                    }else {
                        c.setFill(Color.BROWN);
                    }
                }else{
                    if(i%2!=0){
                        c.setFill(Color.SANDYBROWN);
                    }else {
                        c.setFill(Color.BROWN);
                    }
                }
                c.fillRect(j*boxWidth+boardXPOS, i*boxWidth+boardYPOS, boxWidth, boxWidth);
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

            Label l = new Label("Menu");
            menu_GameSelect.getChildren().add(l);

            // create buttons for menu
            Button button = new Button("Neues Spiel");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    startSingle();
                }
            });
            menu_GameSelect.getChildren().add(button);

            button = new Button("1vBot");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    startSingleBot();
                }
            });
            menu_GameSelect.getChildren().add(button);

            button = new Button("Multiplayer");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    startMulti();
                }
            });
            menu_GameSelect.getChildren().add(button);

            button = new Button("BotvBot");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    startBot();
                }
            });

            menu_GameSelect.getChildren().add(button);
            menu_GameSelect.setMinWidth(150d);

        }
        return menu_GameSelect;

    }

    private void startBot() {

        board = new Board(getBoardDimensions());
        boolean enableHardMode1 = false;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bot 1");
        alert.setHeaderText(null);
        alert.setContentText("Welche Stufe soll der 1. Bot (schwarz) haben?");

        ButtonType buttonTypeE = new ButtonType("Einfach");
        ButtonType buttonTypeS = new ButtonType("Schwer");

        alert.getButtonTypes().setAll(buttonTypeE,buttonTypeS);
        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == buttonTypeS){
            enableHardMode1 = true;
        } if (result.get() == buttonTypeE){
            enableHardMode1 = false;
        }else {
            enableHardMode1 = false;
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


        timer = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1d), new EventHandler<ActionEvent>() {
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
                final String phrase = errorPhrase;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Fehler!");
                        alert.setHeaderText(null);
                        alert.setContentText(phrase);

                        alert.showAndWait();
                    }
                });
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

    }

    private void startSingleBot() {

    }

    private void startSingle() {

        gameType = GameType.SINGLEPLAYER;
        gameState = GameState.FIRSTMOVE;
        board = new Board(getBoardDimensions());
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

    private String getChoice(String title, String header, String content, ArrayList<String> choices){

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent())return result.get();
        return "";
    }

    private int getBoardDimensions() {
        ArrayList<String> choices = new ArrayList<>();
        for(int i = 6; i<=20; i++) choices.add(""+i);
        String choice = getChoice("Brettgroesse", "Bitte waehle die Brettgroesse", "Seitenlaenge: ", choices);
        if(choice == "") return getBoardDimensions();
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

    private void setStone(int x, int y){

        if(canvasAllowUserInput){

            if (x >= 0 && y >= 0 && y < board.getDimension() && x <board.getDimension()) {
                y++;//Muss für das Board in der Console herhöht werden
                switch (gameType){
                    case SINGLEPLAYER:

                        String winningPhrase = "";
                        String errorPhrase = "";

                        try{

                            board.addStone(new Stone(new BoardPoint(BoardPoint.getX(x),y), color));
                            render();
                            board.checkWinner();

                            gameState = GameState.values()[gameState.ordinal()+1];
                            //TODO REMOVE
//                            System.out.println(gameState);
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
                }
            }

        }

    }

}
