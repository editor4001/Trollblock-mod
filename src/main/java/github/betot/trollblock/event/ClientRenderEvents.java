package github.betot.trollblock.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.betot.trollblock.blockentity.TransmitterBlockEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;

        if (level == null) return;

        UUID selected = ClientData.selectedLink;
        if (selected == null) return;

        PoseStack poseStack = event.getPoseStack();

        VertexConsumer buffer = event.getMultiBufferSource()
                .getBuffer(RenderType.lines());

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cam = camera.getPosition();

        // 🔥 ICI TU METS TA BOUCLE
        for (BlockPos pos : BlockPos.betweenClosed(
                mc.player.blockPosition().offset(-32, -32, -32),
                mc.player.blockPosition().offset(32, 32, 32))) {

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof TransmitterBlockEntity te)) continue;

            if (!selected.equals(te.getLinkId())) continue;

            AABB box = new AABB(pos).move(-cam.x, -cam.y, -cam.z);

            LevelRenderer.renderLineBox(
                    poseStack,
                    buffer,
                    box,
                    0f, 1f, 0f, 1f
            );
        }
    }
}