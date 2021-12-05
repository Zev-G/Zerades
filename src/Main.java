import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tmw.me.com.app.gui.Zerades;
import tmw.me.com.app.gui.home.GameAdder;
import tmw.me.com.app.gui.home.HomePage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        System.out.println(System.getenv("APPDATA"));
        File zeradesFolder = new File(System.getenv("APPDATA") + "\\Zerades");
        if (!zeradesFolder.exists()) {
            zeradesFolder.mkdir();
        }
        File zeradesGamesFolder = new File(zeradesFolder.getPath() + "\\Games");
        if (!zeradesGamesFolder.exists()) {
            zeradesGamesFolder.mkdir();
        }
        HomePage.gameFolder = zeradesGamesFolder;

        Zerades zerades = new Zerades();
        HomePage homePage = new HomePage(zerades);
        homePage.getItemsPane().addChild(new GameAdder(homePage), false);
        zerades.setNode(homePage.getPage());
        primaryStage.setTitle("Zerades");
        primaryStage.setScene(new Scene(zerades, 300, 275, Color.valueOf("#282c34")));
        primaryStage.show();
        primaryStage.setOnHidden(windowEvent -> homePage.save());

    }


    public static void main(String[] args) {
        launch(args);
    }
}
