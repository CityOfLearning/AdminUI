package com.dyn.admin.gui;

import java.util.ArrayList;

import com.dyn.admin.AdminUI;
import com.dyn.server.ServerMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.FeedPlayerMessage;
import com.dyn.server.packets.server.RemoveEffectsMessage;
import com.dyn.server.packets.server.RequestFreezePlayerMessage;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

public class ManageStudents extends Show {

	private EntityPlayerSP teacher;
	private ArrayList<String> userlist = new ArrayList<String>();

	public ManageStudents() {
		setBackground(new DefaultBackground());
		title = "Teacher Gui Roster Management";
	}

	private void feedStudents() {
		for (String student : AdminUI.roster) {
			PacketDispatcher.sendToServer(new FeedPlayerMessage(student));
		}
	}

	private void freezeUnfreezeStudents(boolean state) {
		for (String student : AdminUI.roster) {
			if (state) {
				teacher.sendChatMessage("/p user " + student + " group add _FROZEN_");
			} else {
				teacher.sendChatMessage("/p user " + student + " group remove _FROZEN_");
			}
			PacketDispatcher.sendToServer(new RequestFreezePlayerMessage(student, state));
		}
	}

	private void healStudents() {
		for (String student : AdminUI.roster) {
			teacher.sendChatMessage("/heal " + student);
		}
	}

	private void muteStudents() {
		for (String student : AdminUI.roster) {
			teacher.sendChatMessage("/mute " + student);
		}
	}

	private void removeEffects() {
		for (String student : AdminUI.roster) {
			PacketDispatcher.sendToServer(new RemoveEffectsMessage(student));
		}
	}

	@Override
	public void setup() {
		super.setup();

		teacher = Minecraft.getMinecraft().thePlayer;

		for (String s : ServerMod.usernames) {
			if (!AdminUI.roster.contains(s) && (s != Minecraft.getMinecraft().thePlayer.getDisplayNameString())) {
				userlist.add(s);
			}
		}

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Roster Management",
				TextAlignment.CENTER));
		;

		// the side buttons
		registerComponent(new PictureButton((int) (width * .03), (int) (height * .2), 30, 30,
				new ResourceLocation("minecraft", "textures/items/nether_star.png")).setIsEnabled(true)
						.addHoverText("Home Page").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .35), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ruby.png")).setIsEnabled(true)
						.addHoverText("Setup Student Roster").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Roster())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .5), 30, 30,
				new ResourceLocation("minecraft", "textures/items/cookie.png")).setIsEnabled(true)
						.addHoverText("Manage a Student").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudent())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .65), 30, 30,
				new ResourceLocation("minecraft", "textures/items/fish_clownfish_raw.png")).setIsEnabled(false)
						.addHoverText("Manage Students").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudents())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .35), 30, 30,
				new ResourceLocation("minecraft", "textures/items/emerald.png")).setIsEnabled(true)
						.addHoverText("Give Items").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new GiveItem())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .5), 30, 30,
				new ResourceLocation("minecraft", "textures/items/sugar.png")).setIsEnabled(true)
						.addHoverText("Give Items").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new RemoveItem())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .65), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ender_eye.png")).setIsEnabled(true)
						.addHoverText("Award Achievements").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new GiveAchievement())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .8), 30, 30,
				new ResourceLocation("minecraft", "textures/items/book_writable.png")).setIsEnabled(true)
						.addHoverText("Check Achievements").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new CheckPlayerAchievements())));

		// GUI main section
		registerComponent(new Button((int) (width * .525), (int) (height * .2), 135, 20, "Teleport Students to me")
				.setClickListener(but -> teleportStudentsToMe()));

		registerComponent(new Button((int) (width * .15), (int) (height * .2), 135, 20, "Freeze Students")
				.setClickListener(but -> freezeUnfreezeStudents(true)));

		registerComponent(new Button((int) (width * .15), (int) (height * .3), 135, 20, "Unfreeze Students")
				.setClickListener(but -> freezeUnfreezeStudents(false)));

		registerComponent(new Button((int) (width * .525), (int) (height * .3), 135, 20, "Heal Students")
				.setClickListener(but -> healStudents()));

		registerComponent(new Button((int) (width * .525), (int) (height * .4), 135, 20, "Feed Students")
				.setClickListener(but -> feedStudents()));

		registerComponent(new Button((int) (width * .15), (int) (height * .4), 135, 20, "Mute Students")
				.setClickListener(but -> muteStudents()));

		registerComponent(new Button((int) (width * .15), (int) (height * .5), 135, 20, "Unmute Students")
				.setClickListener(but -> unmuteStudents()));

		registerComponent(new Button((int) (width * .525), (int) (height * .5), 135, 20, "Remove Effects")
				.addHoverText("Removes effects like poison and invisibility").doesDrawHoverText(true)
				.setClickListener(but -> removeEffects()));

		registerComponent(new Button((int) (width * .15), (int) (height * .6), 135, 20, "Set to Creative mode")
				.setClickListener(but -> switchMode(1)));

		registerComponent(new Button((int) (width * .525), (int) (height * .6), 135, 20, "Set to Survival mode")
				.setClickListener(but -> switchMode(0)));

		registerComponent(new Button((int) (width * .525), (int) (height * .8), 135, 20, "Clear Student Roster")
				.setClickListener(but -> {
					AdminUI.roster.clear();
				}));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void switchMode(int mode) {
		for (String student : AdminUI.roster) {
			teacher.sendChatMessage("/gamemode " + mode + " " + student);
		}
	}

	private void teleportStudentsToMe() {
		/// tp <Player1> <Player2>. Player1 is the person doing the teleporting,
		/// Player2 is the person that Player1 is teleporting to
		for (String student : AdminUI.roster) {
			teacher.sendChatMessage("/tp " + student + " " + teacher.getDisplayNameString());
		}
	}

	private void unmuteStudents() {
		for (String student : AdminUI.roster) {
			teacher.sendChatMessage("/unmute " + student);
		}
	}
}
