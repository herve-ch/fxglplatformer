package pf.herve.fxglplatformer.components;

import com.almasb.fxgl.entity.components.IntegerComponent;

/**
 * @author rv_ch
 */
public class HPComponent extends IntegerComponent {

    private final int maxHP;

    public HPComponent(int hp) {
        super(hp);
        maxHP = hp;
    }

    public int getMaxHP() {
        return maxHP;
    }
}
