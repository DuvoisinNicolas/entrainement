import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Main extends Application {


    private static final double W = 800, H = 600;

    private static final double speed = 0.2;
    private IntegerProperty level = new SimpleIntegerProperty(6);

    private Rectangle rect1 = new Rectangle();
    private Rectangle rect2 = new Rectangle();

    private ArrayList<Shape>  tabShapes = new ArrayList<>();
    private static final String HERO_IMAGE_LOC =
            "http://icons.iconarchive.com/icons/raindropmemory/legendora/64/Hero-icon.png";

    private static final String DOOR_IMAGE_LOC =
            "http://christophe.delagarde.free.fr/IUT/Troll/img/head.jpg";

    private Group dungeon;
    private Image heroImage;
    private Node  hero;
    private Label levelLabel;
    private Label levelValue;

    private Image doorImage = new Image(DOOR_IMAGE_LOC);
    private ImageView door = new ImageView(doorImage);

    // Booléens à vrai si on va dans cette direction
    boolean goNorth, goSouth, goEast, goWest;

    @Override
    public void start(Stage stage) {

        // Création de l'image du héros
        heroImage = new Image(HERO_IMAGE_LOC);
        hero = new ImageView(heroImage);

        door.setFitHeight(100);
        door.setFitWidth(80);


        levelLabel = new Label("Niveau : ");
        levelLabel.relocate(10,10);
        levelValue = new Label();
        levelValue.relocate(60,10);
        levelValue.textProperty().bind(level.asString());




        // Groupe comprennant le héros
        dungeon = new Group();
        buildLevel();



        // On place le héros où on veut sur l'écran
        moveHeroTo(W / 15, H / 2);

        Scene scene = new Scene(dungeon, W, H, Color.FORESTGREEN);

        // Quand on appuie sur une touche , changer le boolean en true (celui de la direction)
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:    goNorth = true; break;
                case DOWN:  goSouth = true; break;
                case LEFT:  goWest  = true; break;
                case RIGHT: goEast  = true; break;
            }
        });

        // L'inverse de avant , quand on relache la touche on met à false
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case UP:    goNorth = false; break;
                case DOWN:  goSouth = false; break;
                case LEFT:  goWest  = false; break;
                case RIGHT: goEast  = false; break;
            }
        });

        stage.setScene(scene);
        stage.show();

        // Crée une variable dx et dy , qui vont représenter la modification de la position à chaque seconde
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dx = 0, dy = 0;
                if (goNorth) dy -= speed;
                if (goSouth) dy += speed;
                if (goEast)  dx += speed;
                if (goWest)  dx -= speed;
                moveHeroBy(dx, dy);

                try
                {
                    testColisionAlways(rect1);
                    testColisionAlways(rect2);
                }
                catch (NullPointerException npe)
                {
                    System.out.println("C'est tellement moche que ça marche");
                }
            }
        };
        timer.start();



    }

    // Calcul de la hitbox du héros
    private void moveHeroBy(double dx, double dy) {
        if (dx == 0 && dy == 0) return;

        final double cx = hero.getBoundsInLocal().getWidth()  / 2;
        final double cy = hero.getBoundsInLocal().getHeight() / 2;

        double x = cx + hero.getLayoutX() + dx;
        double y = cy + hero.getLayoutY() + dy;

        moveHeroTo(x, y);
    }

    // Vérification que le héros peut se déplacer ici , et si oui , le déplace
    private void moveHeroTo(double x, double y) {
        final double cx = hero.getBoundsInLocal().getWidth()  / 2;
        final double cy = hero.getBoundsInLocal().getHeight() / 2;

        if (x - cx >= 0 &&
                x + cx <= W &&
                y - cy >= 0 &&
                y + cy <= H) {
            hero.relocate(x - cx, y - cy);
        }

        try
        {
            testColisionsDeplacement(rect1);
            testColisionsDeplacement(rect2);
        }
        catch (NullPointerException npe)
        {
            System.out.println("C'est tellement moche que ça marche");
        }

        testExit();
    }

    public void testColisionAlways (Shape shape)
    {
        if (hero.getBoundsInParent().intersects(shape.getBoundsInParent()) && shape.getFill().equals(Color.RED))
        {
            hero.relocate(W/15,H/2);
            if(level.get()>1)
                level.set(level.get()-1);
            buildLevel();
        }
    }

    public void testColisionsDeplacement(Shape shape)
    {
        if (hero.getBoundsInParent().intersects(shape.getBoundsInParent()) && shape.getFill().equals(Color.CORNFLOWERBLUE))
        {
            hero.relocate(W/15,H/2);
            if(level.get()>1)
                level.set(level.get()-1);
            buildLevel();
        }
    }

    public void testExit ()
    {
        if (hero.getBoundsInParent().intersects(door.getBoundsInParent()))
        {
            level.set(level.get()+1);
            buildLevel();
        }
    }

    public void buildLevel ()
    {
        dungeon.getChildren().clear();

        dungeon.getChildren().addAll(hero,door,levelLabel,levelValue);

        hero.relocate(0 + hero.getLayoutX(), H / 2);
        door.relocate(700,250);
        rect1.setFill(Color.TRANSPARENT);
        rect2.setFill(Color.TRANSPARENT);

        if (level.getValue() == 1)
        {
            rect1 = new Rectangle(W/2,0,200,50);

            rect1.setArcHeight(10);
            rect1.setArcWidth(10);
            rect1.setFill(Color.CORNFLOWERBLUE);

            Path path = new Path();
            path.getElements().add(new MoveTo(rect1.getX(), rect1.getY()));
            path.getElements().add(new LineTo(rect1.getX(),H- rect1.getHeight()/2));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(1000));
            pathTransition.setPath(path);
            pathTransition.setNode(rect1);
            pathTransition.setCycleCount(Timeline.INDEFINITE);
            pathTransition.setAutoReverse(true);
            pathTransition.play();

            dungeon.getChildren().add(rect1);
        }

        if (level.getValue() == 2)
        {
            rect1 = new Rectangle(W/2,0,400,50);

            rect1.setArcHeight(10);
            rect1.setArcWidth(10);
            rect1.setFill(Color.CORNFLOWERBLUE);

            Path path = new Path();
            path.getElements().add(new MoveTo(rect1.getX(), rect1.getY()));
            path.getElements().add(new LineTo(rect1.getX(),H- rect1.getHeight()/2));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(1000));
            pathTransition.setPath(path);
            pathTransition.setNode(rect1);
            pathTransition.setCycleCount(Timeline.INDEFINITE);
            pathTransition.setAutoReverse(true);
            pathTransition.play();

            dungeon.getChildren().add(rect1);

        }

        if (level.getValue() == 3)
        {
            rect1 = new Rectangle(W,H/2,25,700);

            rect1.setArcHeight(10);
            rect1.setArcWidth(10);
            rect1.setFill(Color.CORNFLOWERBLUE);

            Path path = new Path();
            path.getElements().add(new MoveTo(rect1.getX(), rect1.getY()));
            path.getElements().add(new LineTo(0, rect1.getY()));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(1000));
            pathTransition.setPath(path);
            pathTransition.setNode(rect1);
            pathTransition.setCycleCount(Timeline.INDEFINITE);
            pathTransition.setAutoReverse(true);
            pathTransition.play();

            dungeon.getChildren().add(rect1);
        }

        if(level.getValue() == 4) {
            rect1 = new Rectangle(W / 2, 0, 400, 50);

            rect1.setArcHeight(10);
            rect1.setArcWidth(10);
            rect1.setFill(Color.CORNFLOWERBLUE);

            Path path = new Path();
            path.getElements().add(new MoveTo(rect1.getX(), rect1.getY()));
            path.getElements().add(new LineTo(rect1.getX(), H - rect1.getHeight() / 2));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(1000));
            pathTransition.setPath(path);
            pathTransition.setNode(rect1);
            pathTransition.setCycleCount(Timeline.INDEFINITE);
            pathTransition.setAutoReverse(true);
            pathTransition.play();

            dungeon.getChildren().add(rect1);



            rect2 = new Rectangle(W, H / 2, 25, 700);

            rect2.setArcHeight(10);
            rect2.setArcWidth(10);
            rect2.setFill(Color.CORNFLOWERBLUE);

            Path path2 = new Path();
            path2.getElements().add(new MoveTo(rect2.getX(), rect2.getY()));
            path2.getElements().add(new LineTo(0, rect2.getY()));
            PathTransition pathTransition2 = new PathTransition();
            pathTransition2.setDuration(Duration.millis(1000));
            pathTransition2.setPath(path2);
            pathTransition2.setNode(rect2);
            pathTransition2.setCycleCount(Timeline.INDEFINITE);
            pathTransition2.setAutoReverse(true);
            pathTransition2.play();

            dungeon.getChildren().add(rect2);
        }

        if(level.getValue() == 5) {
            rect1 = new Rectangle(W-150,H/4,25,300);

            rect1.setArcHeight(10);
            rect1.setArcWidth(10);
            rect1.setFill(Color.RED);

            Path path = new Path();
            path.getElements().add(new MoveTo(rect1.getX(), rect1.getY()));
            path.getElements().add(new LineTo(100,rect1.getY()));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(1500));
            pathTransition.setPath(path);
            pathTransition.setNode(rect1);
            pathTransition.setCycleCount(Timeline.INDEFINITE);
            pathTransition.setAutoReverse(true);
            pathTransition.play();

            dungeon.getChildren().add(rect1);


            rect2 = new Rectangle(150,H*0.75,25,300);

            rect2.setArcHeight(10);
            rect2.setArcWidth(10);
            rect2.setFill(Color.RED);

            Path path2 = new Path();
            path2.getElements().add(new MoveTo(rect2.getX(), rect2.getY()));
            path2.getElements().add(new LineTo(W-100,rect2.getY()));
            PathTransition pathTransition2 = new PathTransition();
            pathTransition2.setDuration(Duration.millis(1500));
            pathTransition2.setPath(path2);
            pathTransition2.setNode(rect2);
            pathTransition2.setCycleCount(Timeline.INDEFINITE);
            pathTransition2.setAutoReverse(true);
            pathTransition2.play();

            dungeon.getChildren().add(rect2);

            }

            if ( level.get() == 6 )
            {

                rect1 = new Rectangle(W/2,H*0.25,25,600);

                rect1.setArcHeight(10);
                rect1.setArcWidth(10);
                rect1.setFill(Color.CORNFLOWERBLUE);


                Path path1 = new Path();
                path1.getElements().add(new MoveTo(rect1.getX(), rect1.getY()));
                PathTransition pathTransition = new PathTransition();
                pathTransition.setDuration(Duration.millis(1500));
                pathTransition.setPath(path1);
                pathTransition.setNode(rect1);
                pathTransition.setCycleCount(Timeline.INDEFINITE);
                pathTransition.setAutoReverse(true);
                pathTransition.play();

                RotateTransition rotateTransition = new RotateTransition(Duration.millis(3000), rect1);
                rotateTransition.setByAngle(360);
                rotateTransition.setNode(rect1);
                rotateTransition.setCycleCount(Timeline.INDEFINITE);
                rotateTransition.setAutoReverse(true);
                rotateTransition.play();

                dungeon.getChildren().add(rect1);

            }

        }



    public static void main(String[] args) { launch(args); }
}


