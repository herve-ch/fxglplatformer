package pf.herve.fxglplatformer.components;

import com.almasb.fxgl.animation.Interpolators;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class BouncingComponent extends Component {

    @Override
    public void onAdded() {
        animationBuilder()
                .repeatInfinitely()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .autoReverse(true)
                .duration(Duration.seconds(0.55))
                .scale(entity)
                .from(new Point2D(1.3, 1.0 / 1.3))
                .to(new Point2D(1.0, 1.0))
                .buildAndPlay();

        animationBuilder()
                .repeatInfinitely()
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .autoReverse(true)
                .duration(Duration.seconds(0.55))
                .translate(entity)
                .from(new Point2D(entity.getX(), entity.getY() + 10))
                .to(new Point2D(entity.getX(), entity.getY() - 105))
                .buildAndPlay();
    }
}