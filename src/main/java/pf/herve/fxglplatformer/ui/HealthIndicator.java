package pf.herve.fxglplatformer.ui;

import com.almasb.fxgl.animation.Interpolators;
import static com.almasb.fxgl.dsl.FXGL.texture;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import pf.herve.fxglplatformer.components.HPComponent;

/**
 * @author rv_ch
 */
public class HealthIndicator extends StackPane {

    private HPComponent playerHP;
    private HBox lifeBox = new HBox();
    private static final Texture HEART = texture("heart.png", 65, 72).darker();

    public HealthIndicator(HPComponent playerHP) {
        this.playerHP = playerHP;

        for (int i = 0; i < playerHP.getValue(); i++) {
            lifeBox.getChildren().add(HEART.copy());
        }
        getChildren().add(lifeBox);
    }

}
