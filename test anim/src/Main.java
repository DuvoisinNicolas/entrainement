import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        BorderPane root = new BorderPane();
        primaryStage.setScene(new Scene(root, 800, 600));

        Circle c1 = new Circle(0,0,5);


        final Rectangle rectPath = new Rectangle (0, 0, 40, 40);
        rectPath.setArcHeight(10);
        rectPath.setArcWidth(10);
        rectPath.setFill(Color.ORANGE);


        Path path = new Path();
        path.getElements().add (new MoveTo (0, 0));
        path.getElements().add (new LineTo (500,0));
        path.getElements().add (new LineTo (500,500));


        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(4000));
        pathTransition.setPath(path);
        pathTransition.setNode(rectPath);
        pathTransition.setCycleCount(Timeline.INDEFINITE);
        pathTransition.setAutoReverse(true);
        pathTransition.play();

        root.getChildren().add(rectPath);

        root.getChildren().add(c1);

        primaryStage.show();
    }
}




