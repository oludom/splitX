package ui;

import game.Board;
import game.BoardPoint;
import game.GameException;
import game.Stone;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Micha Heiß
 */
public class BasicUIX extends Application {

    GraphicsContext c;
    Canvas canvas;
    private Stage primaryStage;
    private Scene scene;

    private Board board;
    private double boxWidth;

    Boolean canvasEnabled = false;
    Boolean color = false;


    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Connect6");
        StackPane root = new StackPane();
        scene = new Scene(root, 1200, 1000);
        primaryStage.setScene(scene);

        this.board = new Board(6);

        /*
        // handler für Fenster Skalierung
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                changeSize();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                changeSize();
            }
        });
*/
        primaryStage.show();
        canvas = new Canvas(1000,1000);
        c = canvas.getGraphicsContext2D();

        GridPane gridPane = new GridPane();
        VBox menu = new VBox();
        root.getChildren().add(gridPane);
        gridPane.add(canvas, 0,0);
        gridPane.add(menu, 1,0);

        menu.setStyle("-fx-background-color: olive; -fx-min-width: 200px;");


        primaryStage.setResizable(false);

        //changeSize();
        render();

        //board.addStone(new Stone(new BoardPoint("C", 3), false));

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

        startSingle();

    }

    private void drawHover(int x, int y, Color color) {
        if(canvasEnabled){
            render();
            c.setFill(color);
            c.fillOval(x*boxWidth, y*boxWidth, boxWidth, boxWidth);
        }
    }

    private void setStone(int x, int y){

        try{
            board.addStone(new Stone(new BoardPoint(BoardPoint.getX(x),y), color));
            render();
            color = !color;
        }catch (GameException.BoardOutOfBoundException e){
            System.out.println(e.toString());
        }

    }

    public void render(){
        if(canvasEnabled){
            // show all elements in canvas
            c.setFill(Color.RED);
            //c.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
            // show chess pattern
            int dimensions = board.getDimension();
            boxWidth = canvas.getWidth()/board.getDimension();

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
                    c.fillRect(j*boxWidth, i*boxWidth, boxWidth, boxWidth);
                }
            }

            ArrayList<Stone> black = board.getBlackStones();
            ArrayList<Stone> white = board.getWhiteStones();

            for(Stone s : black){
                BoardPoint b = s.getPoint();
                int x = b.getX();
                int y = b.yPos;
                drawStone(x,y,Color.BLACK);
            }
            for(Stone s : white){
                BoardPoint b = s.getPoint();
                int x = b.getX();
                int y = b.yPos;
                drawStone(x,y,Color.WHITE);
            }
        }


    }

    public void drawStone(int x, int y, Color color){
        if(canvasEnabled){
            c.setFill(color);
            c.fillOval(x*boxWidth, y*boxWidth, boxWidth, boxWidth);
        }
    }
    private int getFieldX(double x){
        return (int) (x/boxWidth);
    }
    private int getFieldY(double y){
        return (int) (y/boxWidth);
    }

    /*
Fenster Skalieren
 */
    public void changeSize() {
        double width = scene.getWidth()-200;
        double height = scene.getHeight();
        // set object size
        canvas.setWidth(width);
        canvas.setHeight(height);
        //primaryStage.setWidth(width+200);
        //primaryStage.setHeight(height);
        render();
    }

    public String getChoice(String title, String header, String content, ArrayList<String> choices){

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent())return result.get();
        return "";
    }

    public void startSingle(){

        try { // player can stop game
            ArrayList<String> choices = new ArrayList<>();
            for(int i = 6; i<=20; i++) choices.add(""+i);
            int dim = Integer.parseInt(getChoice("Brettgroesse", "Bitte waehle die Brettgroesse", "Seitenlaenge: ", choices));
            board = new Board(dim);



            //TODO tell user, what to do!

            canvasEnabled = true;
            render();

            boolean run = true;

            /*
            String winningPhrase = "";
            String errorPhrase = "";
            while(run){
                for(int i = 1; i <= 2; i++){
                    prln(errorPhrase);
                    errorPhrase = "";
                    try{
                        if(color){
                            prln("Schwarz ist am Zug.");

                            board.addStone(new Stone(readBP(),color));

                        }else{
                            prln("Weiss ist am Zug.");

                            board.addStone(new Stone(readBP(),color));

                        }
                        board.checkWinner();

                    }catch (GameException.GameWonException e) {
                        winningPhrase = e.toString();
                        run = false;
                        break;
                    }catch (GameException.BoardOutOfBoundException e) {

                        errorPhrase = e.toString();
                        i--;

                    }catch (GameException.BoardFullException e) {
                        winningPhrase = e.toString();
                        run = false;
                        break;
                    }catch (UiException.StopGameException e) {
                        winningPhrase = e.toString();
                        run = false;
                        break;
                    }catch (Exception e) {

                        errorPhrase = e.toString();
                        i--;

                    }finally {
                        prUIBuff();
                        board.draw();
                        prUIBuff();
                    }

                }
                color = !color;
            }
            prUIBuff();
            prln(winningPhrase);
            prUIBuff();*/

        }catch (Exception e){//UiException.StopGameException e){
            System.out.println(e.toString());
        }

//        int wahl = selectMenue(new String[]{"Moechtest du nochmal Spielen?","Ja","Nein"});
//        switch(wahl){
//            case 1: startSingle();
//                break;
//            case 2:
//                break;
//        }
    }


}
