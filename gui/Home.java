package com.dyn.admin.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.dyn.admin.AdminUI;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.FeedPlayerMessage;
import com.dyn.server.packets.server.RemoveEffectsMessage;
import com.dyn.server.packets.server.RequestFreezePlayerMessage;
import com.dyn.server.packets.server.RequestUserlistMessage;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.Slider;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.Shape;
import com.rabbit.gui.component.display.ShapeType;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

public class Home extends Show {

	private EntityPlayerSP admin;
	private boolean isCreative;
	private boolean isFrozen;
	private boolean isMuted;
	private boolean areStudentsInCreative;
	private ScrollableDisplayList rosterDisplayList;
	private String freezeText;
	private String muteText;
	private String modeText;
	private PictureButton freezeButton;
	private PictureButton muteButton;
	private PictureButton modeButton;
	private TextLabel numberOfStudentsOnRoster;

	public Home() {
		setBackground(new DefaultBackground());
		title = "Admin GUI Home";
		isFrozen = false;
		areStudentsInCreative = false;
		freezeText = "Freeze Students";
		muteText = "Mute Students";
		modeText = "Creative Mode";
	}

	// Manage Students
	private void feedStudents() {
		for (String student : AdminUI.roster) {
			PacketDispatcher.sendToServer(new FeedPlayerMessage(student.split("-")[0]));
		}
	}

	private void freezeUnfreezeStudents() {
		for (String student : AdminUI.roster) {
			if (isFrozen) {
				admin.sendChatMessage("/p user " + student.split("-")[0] + " group add _FROZEN_");
			} else {
				admin.sendChatMessage("/p user " + student.split("-")[0] + " group remove _FROZEN_");
			}

			PacketDispatcher.sendToServer(new RequestFreezePlayerMessage(student.split("-")[0], isFrozen));
		}
		isFrozen = !isFrozen;
		if (isFrozen) {
			freezeText = "UnFreeze Students";
			List<String> text = freezeButton.getHoverText();
			text.clear();
			text.add(freezeText);
			freezeButton.setHoverText(text);
		} else {
			freezeText = "Freeze Students";
			List<String> text = freezeButton.getHoverText();
			text.clear();
			text.add(freezeText);
			freezeButton.setHoverText(text);
		}
	}

	private void healStudents() {
		for (String student : AdminUI.roster) {
			admin.sendChatMessage("/heal " + student.split("-")[0]);
		}
	}

	private float mapClamp(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
		float outVal = ((((value - inputMin) / (inputMax - inputMin)) * (outputMax - outputMin)) + outputMin);
		return Math.max(outputMin, Math.min(outputMax, outVal));
	}

	private void muteUnmuteStudents() {
		for (String student : AdminUI.roster) {
			if (isMuted) {
				admin.sendChatMessage("/mute " + student.split("-")[0]);
			} else {
				admin.sendChatMessage("/unmute " + student.split("-")[0]);
			}
		}

		isMuted = !isMuted;
		if (isMuted) {
			muteText = "UnMute Students";
			List<String> text = muteButton.getHoverText();
			text.clear();
			text.add(muteText);
			muteButton.setHoverText(text);
		} else {
			muteText = "Mute Students";
			List<String> text = muteButton.getHoverText();
			text.clear();
			text.add(muteText);
			muteButton.setHoverText(text);
		}
	}

	private void removeEffects() {
		for (String student : AdminUI.roster) {
			PacketDispatcher.sendToServer(new RemoveEffectsMessage(student.split("-")[0]));
		}
	}

	@Override
	public void setup() {
		super.setup();

		admin = Minecraft.getMinecraft().thePlayer;
		isCreative = admin.capabilities.isCreativeMode;

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Home", TextAlignment.CENTER));

		// the side buttons
		registerComponent(new PictureButton((int) (width * .03), (int) (height * .5), 30, 30,
				new ResourceLocation("dyn", "textures/gui/group.png")).setIsEnabled(false)
						.addHoverText("Manage Classroom").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .65), 30, 30,
				new ResourceLocation("dyn", "textures/gui/roster.png")).setIsEnabled(true)
						.addHoverText("Student Rosters").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Roster())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .8), 30, 30,
				new ResourceLocation("dyn", "textures/gui/user.png")).setIsEnabled(true)
						.addHoverText("Manage a Student").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudent())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .65), 30, 30,
				new ResourceLocation("dyn", "textures/gui/chest.png")).setIsEnabled(true)
						.addHoverText("Manage Inventory").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudentsInventory())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .8), 30, 30,
				new ResourceLocation("dyn", "textures/gui/achievement.png")).setIsEnabled(true)
						.addHoverText("Award Achievements").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new MonitorAchievements())));

		// gui main area

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		// View roster list
		for (String s : AdminUI.roster) {
			rlist.add(new StringEntry(s));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .45), (int) (width / 3.3),
				105, 15, rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		// TODO Change Total to actual total count of roster
		numberOfStudentsOnRoster = new TextLabel((int) (width * .37), (int) (height * .4), 90, 20,
				AdminUI.roster.size() + "/" + "Total", TextAlignment.LEFT);
		registerComponent(numberOfStudentsOnRoster);

		registerComponent(new Button((int) (width * .15), (int) (height * .35), 20, 20, "<>").addHoverText("Refresh")
				.doesDrawHoverText(true).setClickListener(but -> updateUserList()));

		// Manage Students
		registerComponent(new Button((int) (width * .55), (int) (height * .72), (int) (width / 3.3), 20,
				"Teleport Students to me").setClickListener(but -> teleportStudentsToMe()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .82), (int) (width / 3.3), 20, "Remove Effects")
						.addHoverText("Removes effects like poison and invisibility").doesDrawHoverText(true)
						.setClickListener(but -> removeEffects()));

		freezeButton = new PictureButton((int) (width * .55), (int) (height * .37), 50, 25,
				new ResourceLocation("dyn", "textures/gui/user.png"));
		freezeButton.setIsEnabled(true).addHoverText(freezeText).doesDrawHoverText(true)
				.setClickListener(but -> freezeUnfreezeStudents());
		registerComponent(freezeButton);

		muteButton = new PictureButton((int) (width * .55), (int) (height * .485), 50, 25,
				new ResourceLocation("dyn", "textures/gui/unmute.png"));
		muteButton.setIsEnabled(true).addHoverText(muteText).doesDrawHoverText(true)
				.setClickListener(but -> muteUnmuteStudents());
		registerComponent(muteButton);

		modeButton = new PictureButton((int) (width * .55), (int) (height * .6), 50, 25,
				new ResourceLocation("minecraft", "textures/items/bread.png"));
		modeButton.setIsEnabled(true).addHoverText(modeText).doesDrawHoverText(true)
				.setClickListener(but -> switchMode());
		registerComponent(modeButton);

		registerComponent(new PictureButton((int) (width * .7), (int) (height * .37), 50, 25,
				new ResourceLocation("dyn", "textures/gui/heart.png")).setIsEnabled(true)
						.addHoverText("Heal Students").doesDrawHoverText(true).setClickListener(but -> healStudents()));

		registerComponent(new PictureButton((int) (width * .7), (int) (height * .485), 50, 25,
				new ResourceLocation("minecraft", "textures/items/chicken_cooked.png")).setIsEnabled(true)
						.addHoverText("Feed Students").doesDrawHoverText(true).setClickListener(but -> feedStudents()));

		registerComponent(new PictureButton((int) (width * .15), (int) (height * .2), 25, 25,
				new ResourceLocation("minecraft", "textures/items/bread.png")).setIsEnabled(true)
						.addHoverText("Set your gamemode").doesDrawHoverText(true)
						.setClickListener(but -> toggleCreative()));

		// time of day
		registerComponent(new TextLabel((int) (width * .53), (int) (height * .18), width / 3, 20, "Set the Time of Day",
				TextAlignment.CENTER));

		registerComponent(new Slider((int) (width * .53) + 15, (int) (height * .23), 120, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos))
				.setProgress(
						mapClamp((Minecraft.getMinecraft().theWorld.getWorldTime() + 6000) % 24000, 0, 24000, 0, 1))
				.setId("tod"));

		// speed slider
		registerComponent(new TextLabel((int) (width * .23), (int) (height * .18), width / 3, 20,
				"Set your movement speed", TextAlignment.CENTER));

		registerComponent(new Slider((int) ((width * .23) + 15), (int) (height * .23), 120, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos)).setId("speed"));

		// The background
		Picture background = new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)),
				(int) (height * .8), new ResourceLocation("dyn", "textures/gui/background.png"));

		registerComponent(new Shape((int) (width / 7.4), (int) (height * .23) + 25, (int) (width * .733), 1,
				ShapeType.RECT, Color.GRAY));
		registerComponent(background);
	}

	private void sliderChanged(Slider s, float pos) {
		if (s.getId() == "tod") {
			int sTime = (int) (24000 * pos); // get the absolute time
			sTime -= 6000; // minecraft time is offset so lets move things
							// backward
							// to go from 0-24 instead of 6-5
			if (sTime < 0) {
				sTime += 24000;
			}
			admin.sendChatMessage("/time set " + sTime);
		}
		if (s.getId() == "speed") { // speed has to be an integer value
			admin.sendChatMessage("/speed " + (int) (1 + (pos * 10)));
		}
	}

	private void switchMode() {
		for (String student : AdminUI.roster) {
			admin.sendChatMessage("/gamemode " + (areStudentsInCreative ? "0 " : "1 ") + student.split("-")[0]);
		}
		areStudentsInCreative = !areStudentsInCreative;
		if (areStudentsInCreative) {
			modeText = "Survival Mode";
			List<String> text = modeButton.getHoverText();
			text.clear();
			text.add(modeText);
			modeButton.setHoverText(text);
		} else {
			modeText = "Creative Mode";
			List<String> text = modeButton.getHoverText();
			text.clear();
			text.add(modeText);
			modeButton.setHoverText(text);
		}
	}

	private void teleportStudentsToMe() {
		/// tp <Player1> <Player2>. Player1 is the person doing the teleporting,
		/// Player2 is the person that Player1 is teleporting to
		for (String student : AdminUI.roster) {
			admin.sendChatMessage("/tp " + student.split("-")[0] + " " + admin.getDisplayNameString());
		}
	}

	private void toggleCreative() {
		admin.sendChatMessage("/gamemode " + (isCreative ? "0" : "1"));
		isCreative = !isCreative;
	}

	private void updateUserList() {
		PacketDispatcher.sendToServer(new RequestUserlistMessage());
		getStage().display(new Home());
	}
}
