package github.betot.trollblock.mixin;

import github.betot.trollblock.entity.DirtyEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements DirtyEntity {

    private boolean isDurty = false;
    private boolean isOnWater = false;

    @Override
    public boolean trollblock$isDurty() {
        return isDurty;
    }

    @Override
    public void trollblock$setDurty(boolean value) {
        this.isDurty = value;
    }

    @Override
    public boolean trollblock$isOnWater(){return isOnWater;}

    @Override
    public void trollblock$setIsOnWater() {

    }

    @Override
    public void trollblock$setIsOnWater(boolean value){this.isOnWater = value;}

}