package cn.royan.entityrectify.command;

import cn.royan.entityrectify.EntityRectify;
import cn.royan.entityrectify.argument.CBlockPosArgumentType;
import cn.royan.entityrectify.util.ClientMethod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntityRectifyCommand {

    public static List<String> step = new CopyOnWriteArrayList<>();

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher){

        dispatcher.register(ClientCommandManager.literal("entityrectify")
                .then(ClientCommandManager.literal("stop")
                        .executes(EntityRectifyCommand::stop))
                .then(ClientCommandManager.literal("clear")
                        .executes(EntityRectifyCommand::clear))
                .then(ClientCommandManager.literal("calculate")
                        .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg())
                                .executes(EntityRectifyCommand::calculate)))
                .then(ClientCommandManager.literal("start")
                        .then(ClientCommandManager.argument("pos1", CBlockPosArgumentType.blockPos())
                                .then(ClientCommandManager.argument("pos2", CBlockPosArgumentType.blockPos())
                                        .executes(EntityRectifyCommand::start)))));

    }

    private static int stop(CommandContext<FabricClientCommandSource> context) {
        EntityRectify.INSTANCE.clickList.clearList();
        context.getSource().sendFeedback(Text.literal("stopped."));
        return 0;
    }

    private static int start(CommandContext<FabricClientCommandSource> context) {
        if(step == null) {
            context.getSource().sendFeedback(Text.literal("you must calculate at first."));
            return 1;
        }
        BlockPos pos1 = CBlockPosArgumentType.getCBlockPos(context, "pos1");
        BlockPos pos2 = CBlockPosArgumentType.getCBlockPos(context, "pos2");
        EntityRectify.INSTANCE.clickList.setClick(context.getSource().getPlayer(), pos1, pos2);
        EntityRectify.INSTANCE.clickList.addList(step);
        return 0;
    }

    private static int clear(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("done."));
        step.clear();
        return 0;
    }

    private static int calculate(CommandContext<FabricClientCommandSource> context) {
        step.clear();
        double value = DoubleArgumentType.getDouble(context, "value");
        double x = -value + 2.0625D;
        String mt = String.format("%64s", Long.toBinaryString(Double.doubleToLongBits(x))).replace(" ", "0");

        for (int i=0; i<7; i++){
            step.add("0");
        }

        StringBuffer s = new StringBuffer(mt.substring(19));
        char[] charArray = s.reverse().toString().toCharArray();

        for (char c : charArray) {
            if(c == '1'){
                step.add("1");
            }
            step.add("0");
        }

        for (int i=0; i<Integer.parseInt(mt.substring(12,19), 2)+1; i++)
            step.add("1");

        context.getSource().sendFeedback(Text.literal(Integer.parseInt(mt.substring(12,19), 2) + " | " + mt.substring(19)));
        return 0;
    }
}
