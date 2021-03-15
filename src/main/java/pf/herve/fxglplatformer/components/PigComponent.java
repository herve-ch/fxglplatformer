package pf.herve.fxglplatformer.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author rv_ch
 */
public class PigComponent extends Component {

    private LiftComponent lift;

    private AnimatedTexture texture;

    private AnimationChannel animWalk;

    public PigComponent() {
    
        animWalk = new AnimationChannel(FXGL.image("pig_walk.png"), 4, 512 / 4, 512 / 4, Duration.seconds(0.75), 12, 15);

        texture = new AnimatedTexture(animWalk);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(512 / 4 / 2, 512 / 4 / 2));
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        entity.setScaleX(lift.isGoingRight() ? 1 : -1);
    }
}
