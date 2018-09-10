import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {


    private static final double W = 800, H = 600;

    private static final double speed = 0.25;
    private static final double gravity = speed/2;

    private static final String HERO_IMAGE_LOC =
            "http://icons.iconarchive.com/icons/raindropmemory/legendora/64/Hero-icon.png";

    private Image heroImage;
    private Node  hero;

    // Booléens à vrai si on va dans cette direction
    boolean goNorth, goSouth, goEast, goWest;

    @Override
    public void start(Stage stage) {

        // Création de l'image du héros
        heroImage = new Image(HERO_IMAGE_LOC);
        hero = new ImageView(heroImage);

        // Groupe comprennant le héros
        Group dungeon = new Group(hero);

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
                dy = dy + gravity;
                moveHeroBy(dx, dy);
            }
        };
        timer.start();


        Rectangle rect = new Rectangle(W/2,0,50,50);

        rect.setArcHeight(10);
        rect.setArcWidth(10);
        rect.setFill(Color.ORANGE);

        Path path = new Path();
        path.getElements().add(new MoveTo(rect.getX(),rect.getY()));
        path.getElements().add(new LineTo(rect.getX(),H-rect.getHeight()/2));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(4000));
        pathTransition.setPath(path);
        pathTransition.setNode(rect);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Timeline.INDEFINITE);
        pathTransition.setAutoReverse(true);
        pathTransition.play();


        dungeon.getChildren().add(rect);


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
    }

    public static void main(String[] args) { launch(args); }
}