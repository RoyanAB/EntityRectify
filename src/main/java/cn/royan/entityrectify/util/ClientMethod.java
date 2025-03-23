package cn.royan.entityrectify.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ClientMethod {
    public static void doItemUse(){
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.doItemUse();
    }

    public static void lookBlock(ClientPlayerEntity player, BlockPos pos) {
        double dx = (pos.getX() + 0.5) - player.getX();
        double dy = (pos.getY() + 0.5) - (player.getY() + player.getStandingEyeHeight());
        double dz = (pos.getZ() + 0.5) - player.getZ();
        double dh = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));
        doLook(player, yaw, pitch);
    }

    public static void doLook(ClientPlayerEntity player, float yaw, float pitch) {
        player.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), yaw, pitch);
    }
}
