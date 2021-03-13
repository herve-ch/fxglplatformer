/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pf.herve.fxglplatformer;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGL.texture;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.ui.FontType;
import java.awt.Paint;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import static pf.herve.fxglplatformer.GameType.BUTTON;
import static pf.herve.fxglplatformer.GameType.DOOR_BOT;
import static pf.herve.fxglplatformer.GameType.DOOR_TOP;
import static pf.herve.fxglplatformer.GameType.EXIT_SIGN;
import static pf.herve.fxglplatformer.GameType.EXIT_TRIGGER;
import static pf.herve.fxglplatformer.GameType.KEY_PROMPT;
import static pf.herve.fxglplatformer.GameType.LIFT;
import static pf.herve.fxglplatformer.GameType.MESSAGE_PROMPT;
import static pf.herve.fxglplatformer.GameType.PLATFORM;
import static pf.herve.fxglplatformer.GameType.PLAYER;
import static pf.herve.fxglplatformer.GameType.SHEEPOU;
import pf.herve.fxglplatformer.components.PhysicsLiftComponent;
import pf.herve.fxglplatformer.components.PlayerComponent;

/**
 *
 * @author rv_ch
 */
public class GameFactory implements EntityFactory {

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder()
                .view(new ScrollingBackgroundView(texture("background/forest.png")))
                .zIndex(-1)
                .with(new IrremovableComponent())
                .build();
    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return entityBuilder(data)
                .type(PLATFORM)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("exitTrigger")
    public Entity newExitTrigger(SpawnData data) {
        return entityBuilder(data)
                .type(EXIT_TRIGGER)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("doorTop")
    public Entity newDoorTop(SpawnData data) {
        return entityBuilder(data)
                .type(DOOR_TOP)
                .opacity(0)
                .build();
    }

    @Spawns("doorBot")
    public Entity newDoorBot(SpawnData data) {
        return entityBuilder(data)
                .type(DOOR_BOT)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .opacity(0)
                .with(new CollidableComponent(false))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(64, 64), BoundingShape.box(6, 25)));

        // this avoids player sticking to walls
        physics.setFixtureDef(new FixtureDef().friction(0.0f));

        return entityBuilder(data)
                .type(PLAYER)
                //.bbox(new HitBox(new Point2D(5, 5), BoundingShape.circle(12)))
                .bbox(new HitBox(new Point2D(40, 30), BoundingShape.box(50, 50)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new PlayerComponent())
                .build();
    }

    @Spawns("exitSign")
    public Entity newExit(SpawnData data) {
        return entityBuilder(data)
                .type(EXIT_SIGN)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("keyPrompt")
    public Entity newPrompt(SpawnData data) {
        return entityBuilder(data)
                .type(KEY_PROMPT)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("keyCode")
    public Entity newKeyCode(SpawnData data) {
        String key = data.get("key");

        KeyCode keyCode = KeyCode.getKeyCode(key);

        var lift = new LiftComponent();
        lift.setGoingUp(true);
        lift.yAxisDistanceDuration(6, Duration.seconds(0.76));

        var view = new KeyView(keyCode, Color.YELLOW, 24);
        view.setCache(true);
        view.setCacheHint(CacheHint.SCALE);

        return entityBuilder(data)
                .view(view)
                .with(lift)
                .zIndex(100)
                .build();
    }

    @Spawns("button")
    public Entity newButton(SpawnData data) {
        var keyEntity = getGameWorld().create("keyCode", new SpawnData(data.getX(), data.getY() - 50).put("key", "E"));
        keyEntity.getViewComponent().setOpacity(0);

        return entityBuilder(data)
                .type(BUTTON)
                .viewWithBBox(texture("button.png", 20, 18))
                .with(new CollidableComponent(true))
                .with("keyEntity", keyEntity)
                .build();
    }

    @Spawns("messagePrompt")
    public Entity newMessagePrompt(SpawnData data) {
        var text = getUIFactoryService().newText(data.get("message"), Color.BLACK, FontType.GAME, 20.0);
        var stack = new StackPane();
        var rect = new Rectangle(20, 20, text.getText().length() * 10, 50);

        text.setStrokeWidth(2);
        text.setTranslateY(-100);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setTranslateY(-100);

        stack.getChildren().addAll(rect, text);
        return entityBuilder(data)
                .type(MESSAGE_PROMPT)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .view(stack)
                .zIndex(999)
                .with(new CollidableComponent(true))
                .opacity(0)
                .build();
    }

    @Spawns("sheepou")
    public Entity newSheepou(SpawnData data) {
        return entityBuilder(data)
                .type(SHEEPOU)
                //.bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .viewWithBBox(texture("sheepou.png", 36, 64))
                .with(new CollidableComponent(true))
                .build();
    }

    /*Pas de phisics*/
//    @Spawns("lift")
//    public Entity newLift(SpawnData data) {
//        //var physics = new PhysicsComponent();       
//        boolean isGoingUp =  true;
//
//        var lift = new LiftComponent();
//        lift.setGoingUp(isGoingUp);
//        lift.yAxisDistanceDuration(100, Duration.seconds(0.76));
//
//        return entityBuilder(data)
//                .type(LIFT)
//                .bbox(new HitBox(new Point2D(0, 50), BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height") - 50)))
//                .with(lift)
//                //.with(physics)
//                .zIndex(100)
//                .build();
//    }
    @Spawns("lift")
    public Entity newLift(SpawnData data) {
        var physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        boolean isGoingUp = data.hasKey("up") ? data.get("up") : true;

        var distance = (isGoingUp) ? data.getY() - data.<Integer>get("endY") : data.<Integer>get("endY") - data.getY();
        var speed = 100;
        var duration = Duration.seconds(distance / speed);

        return entityBuilder(data)
                .type(LIFT)
                .bbox(new HitBox(new Point2D(0, 50), BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height") - 50)))
                .with(physics)
                .with(new PhysicsLiftComponent(duration, distance, isGoingUp))
                .build();
    }

    @Spawns("tree")
    public Entity newTree(SpawnData data) {

        return entityBuilder(data)
                .build();
    }

}
