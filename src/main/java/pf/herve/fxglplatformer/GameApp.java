package pf.herve.fxglplatformer;

import pf.herve.fxglplatformer.ui.GameLoadingScene;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.app.GameApplication.launch;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import static com.almasb.fxgl.dsl.FXGL.addUINode;
import static com.almasb.fxgl.dsl.FXGL.despawnWithDelay;
import static com.almasb.fxgl.dsl.FXGL.despawnWithScale;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.getPhysicsWorld;
import static com.almasb.fxgl.dsl.FXGL.getSettings;
import static com.almasb.fxgl.dsl.FXGL.getUIFactory;
import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.almasb.fxgl.dsl.FXGL.getip;
import static com.almasb.fxgl.dsl.FXGL.inc;
import static com.almasb.fxgl.dsl.FXGL.isMobile;
import static com.almasb.fxgl.dsl.FXGL.loopBGM;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.onCollisionOneTimeOnly;
import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGL.runOnce;
import static com.almasb.fxgl.dsl.FXGL.set;
import static com.almasb.fxgl.dsl.FXGL.setLevelFromMap;
import static com.almasb.fxgl.dsl.FXGL.showMessage;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGL.spawnWithScale;
import static com.almasb.fxgl.dsl.FXGL.texture;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.PhysicsComponent;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import static pf.herve.fxglplatformer.GameType.BUTTON;
import static pf.herve.fxglplatformer.GameType.DOOR_BOT;
import static pf.herve.fxglplatformer.GameType.DOOR_TOP;
import static pf.herve.fxglplatformer.GameType.EXIT_SIGN;
import static pf.herve.fxglplatformer.GameType.EXIT_TRIGGER;
import static pf.herve.fxglplatformer.GameType.KEY_PROMPT;
import static pf.herve.fxglplatformer.GameType.MESSAGE_PROMPT;
import static pf.herve.fxglplatformer.GameType.PIG;
import static pf.herve.fxglplatformer.GameType.PLAYER;
import pf.herve.fxglplatformer.collisions.PlayerButtonHandler;
import pf.herve.fxglplatformer.components.HPComponent;
import pf.herve.fxglplatformer.components.PlayerComponent;
import pf.herve.fxglplatformer.ui.HealthIndicator;
import pf.herve.fxglplatformer.ui.LevelEndScene;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rv_ch
 */
public class GameApp extends GameApplication {

    private static final int MAX_LEVEL = 5;
    private static final int STARTING_LEVEL = 0;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public LoadingScene newLoadingScene() {
                return new GameLoadingScene();
            }
        });
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
        settings.setDeveloperMenuEnabled(true);
        settings.setVersion("1");
        settings.setIntroEnabled(false);
        settings.setTitle("Cheepapou Game");
       // settings.setMainMenuEnabled(true);
    }

    private LazyValue<LevelEndScene> levelEndScene = new LazyValue<>(() -> new LevelEndScene());
    private Entity player;
    private HealthIndicator hp;
    private boolean isMouseEvents = true;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).left();
            }

            @Override
            protected void onActionEnd() {
                isMouseEvents = true;
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.Q, VirtualButton.LEFT);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                isMouseEvents = false;
                player.getComponent(PlayerComponent.class).jump();
            }
        }, KeyCode.W, VirtualButton.A);

        getInput().addAction(new UserAction("Use") {
            @Override
            protected void onActionBegin() {
                getGameWorld().getEntitiesByType(BUTTON)
                        .stream()
                        .filter(btn -> btn.hasComponent(CollidableComponent.class) && player.isColliding(btn))
                        .forEach(btn -> {
                            btn.removeComponent(CollidableComponent.class);

                            Entity keyEntity = btn.getObject("keyEntity");
                            keyEntity.setProperty("activated", true);

                            KeyView view = (KeyView) keyEntity.getViewComponent().getChildren().get(0);
                            view.setKeyColor(Color.RED);

                            makeExitDoor();
                        });
            }
        }, KeyCode.E, VirtualButton.B);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", STARTING_LEVEL);
        vars.put("levelTime", 0.0);
        vars.put("score", 0);
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.25);
        // loopBGM("Altago.mp3");

    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory());

        player = null;
        nextLevel();

        // player must be spawned after call to nextLevel, otherwise player gets removed
        // before the update tick _actually_ adds the player to game world
        player = spawn("player", 50, 50);
//        var emitter = ParticleEmitters.newExplosionEmitter(300);
//
//        emitter.setMaxEmissions(Integer.MAX_VALUE);
//        emitter.setNumParticles(50);
//        emitter.setEmissionRate(0.86);
//        emitter.setSize(1, 24);
//        emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.01));
//        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 2.5)));
//        emitter.setAccelerationFunction(() -> Point2D.ZERO);
//        emitter.setVelocityFunction(i -> FXGLMath.randomPoint2D().multiply(random(1, 45)));
//
//        player.addComponent(new ParticleComponent(emitter));

        set("player", player);

        spawn("background");

        Viewport viewport = getGameScene().getViewport();
        viewport.setBounds(-1500, 0, 250 * 70, getAppHeight());
        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);
        viewport.setLazy(true);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 760);
        getPhysicsWorld().addCollisionHandler(new PlayerButtonHandler());

        onCollisionOneTimeOnly(PLAYER, EXIT_SIGN, (player, sign) -> {
            var texture = texture("exit_sign.png").brighter();
            texture.setTranslateX(sign.getX() + 9);
            texture.setTranslateY(sign.getY() + 13);

            var gameView = new GameView(texture, 150);

            getGameScene().addGameView(gameView);

            runOnce(() -> getGameScene().removeGameView(gameView), Duration.seconds(1.6));
        });

        onCollisionOneTimeOnly(PLAYER, EXIT_TRIGGER, (player, trigger) -> {
            makeExitDoor();
        });

        onCollisionOneTimeOnly(PLAYER, DOOR_BOT, (player, door) -> {
            levelEndScene.get().onLevelFinish();

            // the above runs in its own scene, so fade will wait until
            // the user exits that scene
            getGameScene().getViewport().fade(() -> {
                nextLevel();
            });
        });

        onCollisionOneTimeOnly(PLAYER, MESSAGE_PROMPT, (player, prompt) -> {
            prompt.setOpacity(1);

            despawnWithDelay(prompt, Duration.seconds(4.5));
        });

        onCollisionBegin(PLAYER, KEY_PROMPT, (player, prompt) -> {
            String key = prompt.getString("key");

            var entity = getGameWorld().create("keyCode", new SpawnData(prompt.getX(), prompt.getY()).put("key", key));
            spawnWithScale(entity, Duration.seconds(1), Interpolators.ELASTIC.EASE_OUT());

            runOnce(() -> {
                despawnWithScale(entity, Duration.seconds(1), Interpolators.ELASTIC.EASE_IN());
            }, Duration.seconds(2.5));
        });

        onCollisionBegin(PLAYER, PIG, (player, enemy) -> {
            player.getComponent(PlayerComponent.class).onHit(enemy);

//            if (enemy.getProperties().exists("isProjectile")) {
//                enemy.removeFromWorld();
//            }
        });
    }

    private void makeExitDoor() {
        var doorTop = getGameWorld().getSingleton(DOOR_TOP);
        var doorBot = getGameWorld().getSingleton(DOOR_BOT);

        doorBot.getComponent(CollidableComponent.class).setValue(true);

        doorTop.setOpacity(1);
        doorBot.setOpacity(1);
    }

    private void nextLevel() {
        if (geti("level") == MAX_LEVEL) {
            showMessage("Tiffany a bu la potion magique et a enfin retrouvé sa forme humaine!");
            return;
        }

        inc("level", +1);

        setLevel(geti("level"));
    }

    @Override
    protected void initUI() {

        hp = new HealthIndicator(player.getComponent(HPComponent.class));

        addUINode(hp);

        if (isMobile()) {
            var dpadView = getInput().createVirtualDpadView();
            var buttonsView = getInput().createXboxVirtualControllerView();

            addUINode(dpadView, 0, getAppHeight() - 290);
            addUINode(buttonsView, getAppWidth() - 280, getAppHeight() - 290);

            runOnce(() -> {
                dpadView.getScene().addEventFilter(TouchEvent.ANY, event -> {
                    if (!isMouseEvents) {
                        System.out.println(event);
                    }
                });

                dpadView.getScene().addEventFilter(MouseEvent.ANY, event -> {
                    if (isMouseEvents) {
                        System.out.println(event);
                    }
                });
            }, Duration.seconds(2));
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        inc("levelTime", tpf);

        if (player.getY() > getAppHeight()) {
            onPlayerDied();
        }
    }

    public void onPlayerDied() {
        setLevel(geti("level"));
    }

    private void setLevel(int levelNum) {
        if (player != null) {
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50, 50));
            player.setZIndex(Integer.MAX_VALUE);
            player.getComponent(PlayerComponent.class).restoreHP();
            updateHpIndicator();
        }

        set("levelTime", 0.0);

        Level level = setLevelFromMap("tmx/level" + levelNum + ".tmx");

        var shortestTime = level.getProperties().getDouble("star1time");

        var levelTimeData = new LevelEndScene.LevelTimeData(shortestTime * 2.4, shortestTime * 1.3, shortestTime);

        set("levelTimeData", levelTimeData);
    }

    public void updateHpIndicator() {
        hp.updateLife();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
