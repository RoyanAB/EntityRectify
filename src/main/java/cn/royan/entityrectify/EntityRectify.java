package cn.royan.entityrectify;

import cn.royan.entityrectify.command.EntityRectifyCommand;
import cn.royan.entityrectify.util.ClickManager;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityRectify implements ClientModInitializer {

	public static final String MOD_ID = "entityrectify";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityRectify INSTANCE = new EntityRectify();

	public ClickManager clickList = new ClickManager();

	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_WORLD_TICK.register(this::onClientTick);
		ClientCommandRegistrationCallback.EVENT.register((
				dispatcher, registryAccess) ->
				EntityRectifyCommand.register(dispatcher));
	}

	private void onClientTick(ClientWorld clientWorld) {
		clickList.onTick();
	}

}