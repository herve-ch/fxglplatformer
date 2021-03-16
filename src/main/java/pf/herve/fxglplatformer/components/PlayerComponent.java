/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pf.herve.fxglplatformer.components;

import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.runOnce;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;
import pf.herve.fxglplatformer.GameApp;

/**
 *
 * @author rv_ch
 */
public class PlayerComponent extends Component {

    private PhysicsComponent physics;

    private AnimatedTexture texture;

    private AnimationChannel animIdle, animWalk;
    private HPComponent hp;

    private int jumps = 2;

    public PlayerComponent() {

        Image image = image("sheep_walk.png");

        animIdle = new AnimationChannel(image, 4, 128, 128, Duration.seconds(1), 13, 13);
        animWalk = new AnimationChannel(image, 4, 128, 128, Duration.seconds(0.66), 12, 15);

        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(64, 64));
        entity.getViewComponent().addChild(texture);

        physics.onGroundProperty().addListener((obs, old, isOnGround) -> {
            if (isOnGround) {
                //play("land.wav");
                jumps = 2;
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if (physics.isMovingX()) {
            if (texture.getAnimationChannel() != animWalk) {
                texture.loopAnimationChannel(animWalk);
            }
        } else {
            if (texture.getAnimationChannel() != animIdle) {
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    public void onHit(Entity attacker) {
//        if (isBeingDamaged)
//            return;

//        if (hp.getValue() == 0)
//            return;
        hp.setValue(hp.getValue() - 1);
        FXGL.<GameApp>getAppCast().updateHpIndicator();

        Point2D dmgVector = entity.getPosition().subtract(attacker.getPosition());

//        isBeingDamaged = true;
        physics.setLinearVelocity(new Point2D(Math.signum(dmgVector.getX()) * 290, -300));

        // Damage time 1 sec
        runOnce(() -> {
//            isBeingDamaged = false;
            physics.setVelocityX(0);
        }, Duration.seconds(1));

//        if (hp.getValue() == 0) {
//            FXGL.<MarioApp>getAppCast().onPlayerDied();
//        }
    }

    public void left() {
        getEntity().setScaleX(-1);
        physics.setVelocityX(-170);
    }

    public void right() {
        getEntity().setScaleX(1);
        physics.setVelocityX(170);
    }

    public void stop() {
        physics.setVelocityX(0);
    }

    public void jump() {
        if (jumps == 0) {
            return;
        }

        //play("jump.wav");
        physics.setVelocityY(-300);

        jumps--;
    }

}
