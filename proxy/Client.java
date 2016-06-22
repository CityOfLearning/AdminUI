package com.dyn.admin.proxy;

import org.lwjgl.input.Keyboard;

import com.dyn.admin.gui.Home;
import com.dyn.server.ServerMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.RequestUserlistMessage;
import com.dyn.server.utils.PlayerLevel;
import com.rabbit.gui.GuiFoundation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class Client implements Proxy {

	private KeyBinding adminKey;

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);

		adminKey = new KeyBinding("key.toggle.adminui", Keyboard.KEY_B, "key.categories.toggle");

		ClientRegistry.registerKeyBinding(adminKey);
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {

		if ((Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
			return;
		} //realistically this will only occur if an admin but lets do sanity check
		if ((ServerMod.status == PlayerLevel.ADMIN) && adminKey.isPressed()) {
			PacketDispatcher.sendToServer(new RequestUserlistMessage());
			GuiFoundation.proxy.display(new Home());
		}
	}

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Render GUI when on call from client
	}
}