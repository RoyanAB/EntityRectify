package cn.royan.entityrectify.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ClickManager {

    public final long COOLDOWN_TIME = 10;

    private static List<String> step = new ArrayList<>();

    private static ClientPlayerEntity player;

    private static BlockPos pos1;
    private static BlockPos pos2;

    private long cooldown = COOLDOWN_TIME;

    public void onTick() {
        if (step.isEmpty() || player == null)
            return;

        cooldown--;
        if (cooldown <= 0) {
            if (step.get(0).equals("1")) {
                ClientMethod.lookBlock(player,pos1);
                ClientMethod.doItemUse();
            } else {
                ClientMethod.lookBlock(player,pos2);
                ClientMethod.doItemUse();
            }
            step.remove(0);
            cooldown = COOLDOWN_TIME;
        }
    }

    public void addList(List<String> data){
        step.clear();
        step.addAll(data);
    }

    public void clearList(){
        step.clear();
    }

    public void setClick(ClientPlayerEntity player, BlockPos pos1, BlockPos pos2) {
        ClickManager.player = player;
        ClickManager.pos1 = pos1;
        ClickManager.pos2 = pos2;
    }
}
