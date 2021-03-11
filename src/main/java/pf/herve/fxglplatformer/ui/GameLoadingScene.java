/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pf.herve.fxglplatformer.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.LoadingScene;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.centerText;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGL.texture;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author rv_ch
 */
public class GameLoadingScene extends LoadingScene {

   public GameLoadingScene() {
        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.AZURE);

        var text = getUIFactoryService().newText("Loading level", Color.BLACK, 46.0);
        centerText(text, getAppWidth() / 2, getAppHeight() / 3  + 25);

        var hbox = new HBox(5);

        for (int i = 0; i < 3; i++) {
            var textDot = getUIFactoryService().newText(".", Color.BLACK, 46.0);

            hbox.getChildren().add(textDot);

            animationBuilder(this)
                    .autoReverse(true)
                    .delay(Duration.seconds(i * 0.5))
                    .repeatInfinitely()
                    .fadeIn(textDot)
                    .buildAndPlay();
        }

        hbox.setTranslateX(getAppWidth() / 2 - 20);
        hbox.setTranslateY(getAppHeight() / 2);

        var playerTexture = texture("player.png").subTexture(new Rectangle2D(0, 0, 32, 42));
        playerTexture.setTranslateX(getAppWidth() / 2 - 32/2);
        playerTexture.setTranslateY(getAppHeight() / 2 - 42/2);

        animationBuilder(this)
                .duration(Duration.seconds(1.25))
                .repeatInfinitely()
                .autoReverse(true)
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT())
                .rotate(playerTexture)
                .from(0)
                .to(360)
                .buildAndPlay();

        getContentRoot().getChildren().setAll(bg, text, hbox, playerTexture);
    }
    
}
