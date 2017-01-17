package com.dyn.admin.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.admin.AdminUI;
import com.dyn.server.ServerMod;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.server.FeedPlayerMessage;
import com.dyn.server.network.packets.server.RemoveEffectsMessage;
import com.dyn.server.network.packets.server.RequestFreezePlayerMessage;
import com.dyn.server.network.packets.server.RequestUserlistMessage;
import com.dyn.server.network.packets.server.ServerCommandMessage;
import com.dyn.utils.BooleanChangeListener;
import com.dyn.utils.PlayerAccessLevel;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBoxButton;
import com.rabbit.gui.component.control.CheckBoxPictureButton;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.PictureToggleButton;
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
	private boolean isAdmin;
	private boolean isFrozen;
	private boolean isMuted;
	private boolean areStudentsInCreative;
	private ScrollableDisplayList rosterDisplayList;
	private String freezeText;
	private CheckBoxPictureButton freezeButton;
	private PictureToggleButton muteButton;
	private CheckBoxButton modeButton;
	private PictureToggleButton selfModeButton;
	private PictureToggleButton selfLevelButton;

	public Home() {
		setBackground(new DefaultBackground());
		title = "Admin GUI Home";
		isFrozen = false;
		areStudentsInCreative = false;
		freezeText = "Freeze Students";

		BooleanChangeListener listener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				rosterDisplayList.clear();
				for (String student : AdminUI.adminSubRoster) {
					rosterDisplayList.add(new StringEntry(student));
				}
			}
		};
		DYNServerMod.serverUserlistReturned.addBooleanChangeListener(listener, this);
	}

	// Manage Students
	private void feedStudents() {
		for (String student : AdminUI.adminSubRoster) {
			NetworkManager.sendToServer(new FeedPlayerMessage(student));
		}
	}

	private void freezeUnfreezeStudents() {
		isFrozen = !isFrozen;
		for (String student : AdminUI.adminSubRoster) {
			NetworkManager.sendToServer(new RequestFreezePlayerMessage(student, isFrozen));
		}
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
		for (String student : AdminUI.adminSubRoster) {
			ServerMod.proxy.addScheduledTask(() -> {
				admin.sendChatMessage("/heal " + student);
			});
		}
	}

	private float mapClamp(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
		float outVal = ((((value - inputMin) / (inputMax - inputMin)) * (outputMax - outputMin)) + outputMin);
		return Math.max(outputMin, Math.min(outputMax, outVal));
	}

	private void muteUnmuteStudents() {
		for (String student : AdminUI.adminSubRoster) {
			ServerMod.proxy.addScheduledTask(() -> {
				if (isMuted) {
					admin.sendChatMessage("/mute " + student);
				} else {
					admin.sendChatMessage("/unmute " + student);
				}
			});
		}

		isMuted = !isMuted;
		if (isMuted) {
			List<String> text = muteButton.getHoverText();
			text.clear();
			text.add("UnMute Students");
			muteButton.setHoverText(text);
		} else {
			List<String> text = muteButton.getHoverText();
			text.clear();
			text.add("Mute Students");
			muteButton.setHoverText(text);
		}
	}

	@Override
	public void onClose() {
		DYNServerMod.serverUserlistReturned.removeBooleanChangeListener(this);
	}

	private void removeEffects() {
		for (String student : AdminUI.adminSubRoster) {
			ServerMod.proxy.addScheduledTask(() -> {
				NetworkManager.sendToServer(new RemoveEffectsMessage(student));
			});
		}
	}

	@Override
	public void setup() {
		super.setup();

		admin = Minecraft.getMinecraft().thePlayer;
		isCreative = admin.capabilities.isCreativeMode;

		registerComponent(
				new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Manage Classroom", TextAlignment.CENTER));

		SideButtons.init(this, 1);

		// gui main area

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<>();

		// View roster list
		for (String student : AdminUI.adminSubRoster) {
			rlist.add(new StringEntry(student));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .45), width / 3, 105, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		registerComponent(
				new PictureButton((int) (width * .15), (int) (height * .35), 20, 20, DYNServerConstants.REFRESH_IMAGE)
						.addHoverText("Refresh").setDoesDrawHoverText(true).setClickListener(
								but -> NetworkManager.sendToServer(new RequestUserlistMessage())));

		// Manage Students
		registerComponent(new Button((int) (width * .55), (int) (height * .72), (int) (width / 3.3), 20,
				"Teleport Students to me").setClickListener(but -> teleportStudentsToMe()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .82), (int) (width / 3.3), 20, "Remove Effects")
						.addHoverText("Removes effects like poison and invisibility").setDoesDrawHoverText(true)
						.setClickListener(but -> removeEffects()));

		freezeButton = new CheckBoxPictureButton((int) (width * .55), (int) (height * .37), 50, 25,
				DYNServerConstants.FREEZE_IMAGE, false);
		freezeButton.setIsEnabled(true).addHoverText(freezeText).setDoesDrawHoverText(true)
				.setClickListener(but -> freezeUnfreezeStudents());
		registerComponent(freezeButton);

		muteButton = new PictureToggleButton((int) (width * .55), (int) (height * .485), 50, 25,
				DYNServerConstants.UNMUTE_IMAGE, DYNServerConstants.MUTE_IMAGE, true);
		muteButton.setIsEnabled(true).addHoverText("Mute Students").setDoesDrawHoverText(true)
				.setClickListener(but -> muteUnmuteStudents());
		registerComponent(muteButton);

		modeButton = new CheckBoxButton((int) (width * .55), (int) (height * .62), (int) (width / 3.3), 20,
				"   Toggle Creative", false);
		modeButton.setIsEnabled(true).addHoverText("Creative Mode").setDoesDrawHoverText(true)
				.setClickListener(but -> switchMode());
		registerComponent(modeButton);

		registerComponent(
				new PictureButton((int) (width * .7), (int) (height * .37), 50, 25, DYNServerConstants.HEART_IMAGE)
						.setIsEnabled(true).addHoverText("Heal Students").setDoesDrawHoverText(true)
						.setClickListener(but -> healStudents()));

		registerComponent(new PictureButton((int) (width * .7), (int) (height * .485), 50, 25,
				new ResourceLocation("minecraft", "textures/items/chicken_cooked.png")).setIsEnabled(true)
						.addHoverText("Feed Students").setDoesDrawHoverText(true)
						.setClickListener(but -> feedStudents()));

		registerComponent(
				selfModeButton = (PictureToggleButton) new PictureToggleButton((int) (width * .15), (int) (height * .2),
						25, 25, DYNServerConstants.CREATIVE_IMAGE, DYNServerConstants.SURVIVAL_IMAGE, isCreative)
								.setIsEnabled(true)
								.addHoverText(
										isCreative ? "Set your gamemode to Survival" : "Set your gamemode to Creative")
								.setDoesDrawHoverText(true).setClickListener(but -> toggleCreative()));

		registerComponent(selfLevelButton = (PictureToggleButton) new PictureToggleButton((int) ((width * .15) + 27),
				(int) ((height * .2) + 7), 18, 18, DYNServerConstants.STU_IMAGE, DYNServerConstants.ADMIN_IMAGE,
				DYNServerMod.accessLevel == PlayerAccessLevel.ADMIN)
						.setIsEnabled(true)
						.addHoverText(DYNServerMod.accessLevel == PlayerAccessLevel.ADMIN ? "Set your level to Student"
								: "Set your level to Admin")
						.setDoesDrawHoverText(true).setClickListener(but -> toggleLevel()));

		// time of day

		registerComponent(new TextLabel((int) (width * .53), (int) (height * .18), width / 3, 20, Color.black,
				"Set the Time of Day", TextAlignment.CENTER));

		registerComponent(new Slider((int) (width * .53) + 15, (int) (height * .23), 120, 20, 10)
				.setMouseReleasedListener((Slider s, float pos) -> sliderChanged(s, pos))
				.setProgress(
						mapClamp((Minecraft.getMinecraft().theWorld.getWorldTime() + 6000) % 24000, 0, 24000, 0, 1))
				.setId("tod"));

		// speed slider
		registerComponent(new TextLabel((int) (width * .23), (int) (height * .18), width / 3, 20, Color.black,
				"Set your movement speed", TextAlignment.CENTER));

		registerComponent(new Slider((int) ((width * .23) + 15), (int) (height * .23), 120, 20, 10)
				.setMouseReleasedListener((Slider s, float pos) -> sliderChanged(s, pos)).setId("speed"));

		// The background
		Picture background = new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)),
				(int) (height * .8), DYNServerConstants.BG1_IMAGE);

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

		for (String student : AdminUI.adminSubRoster) {
			ServerMod.proxy.addScheduledTask(() -> {
				admin.sendChatMessage("/gamemode " + (areStudentsInCreative ? "0 " : "1 ") + student);
			});
		}

		areStudentsInCreative = !areStudentsInCreative;
		if (areStudentsInCreative) {
			List<String> text = modeButton.getHoverText();
			text.clear();
			text.add("Survival Mode");
			modeButton.setHoverText(text);
		} else {
			List<String> text = modeButton.getHoverText();
			text.clear();
			text.add("Creative Mode");
			modeButton.setHoverText(text);
		}
	}

	private void teleportStudentsToMe() {
		/// tp <Player1> <Player2>. Player1 is the person doing the teleporting,
		/// Player2 is the person that Player1 is teleporting to
		for (String student : AdminUI.adminSubRoster) {
			ServerMod.proxy.addScheduledTask(() -> {
				admin.sendChatMessage("/tp " + student + " " + admin.getDisplayNameString());
			});
		}

	}

	private void toggleCreative() {
		admin.sendChatMessage("/gamemode " + (isCreative ? "0" : "1"));
		isCreative = !isCreative;
		if (isCreative) {
			List<String> text = selfModeButton.getHoverText();
			text.clear();
			text.add("Set your gamemode to Survival");
			selfModeButton.setHoverText(text);
		} else {
			List<String> text = selfModeButton.getHoverText();
			text.clear();
			text.add("Set your gamemode to Creative");
			selfModeButton.setHoverText(text);
		}
	}

	private void toggleLevel() {

		isAdmin = !isAdmin;
		if (isAdmin) {
			List<String> text = selfLevelButton.getHoverText();
			text.clear();
			text.add("Set your level to Admin");
			selfLevelButton.setHoverText(text);
			DYNServerMod.accessLevel = PlayerAccessLevel.STUDENT;
			NetworkManager.sendToServer(new ServerCommandMessage("/deop " + admin.getDisplayNameString()));
			NetworkManager.sendToServer(
					new ServerCommandMessage("/p user " + admin.getDisplayNameString() + " group remove _OPS_"));
			NetworkManager.sendToServer(
					new ServerCommandMessage("/p user " + admin.getDisplayNameString() + " group add _STUDENTS_"));
		} else {
			List<String> text = selfLevelButton.getHoverText();
			text.clear();
			text.add("Set your level to Student");
			selfLevelButton.setHoverText(text);
			DYNServerMod.accessLevel = PlayerAccessLevel.ADMIN;
			NetworkManager.sendToServer(new ServerCommandMessage("/op " + admin.getDisplayNameString()));
			NetworkManager.sendToServer(
					new ServerCommandMessage("/p user " + admin.getDisplayNameString() + " group add _OPS_"));
			NetworkManager.sendToServer(
					new ServerCommandMessage("/p user " + admin.getDisplayNameString() + " group remove _STUDENTS_"));
		}
	}
}
