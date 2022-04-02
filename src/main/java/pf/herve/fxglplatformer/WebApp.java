/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pf.herve.fxglplatformer;

import com.almasb.fxgl.app.GameApplication;
import com.jpro.webapi.JProApplication;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author rv_ch
 */
public class WebApp extends JProApplication {

    @Override
    public void start(Stage stage) {
        //System.setProperty("fxgl.isBrowser", "true");

        GameApplication app = new GameApp();
//        StackPane root = new StackPane();
//        root.getChildren().setAll(GameApplication.embeddedLaunch(app));
//        Scene scene = new Scene(root);
//        stage.setScene(scene);

        GameApplication.embeddedLaunch(app);

    }

    public static void main(String[] args) {
        launch(args);
    }

}
