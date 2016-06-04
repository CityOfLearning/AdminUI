package com.dyn.admin.gui;

import java.util.ArrayList;

import com.dyn.admin.AdminUI;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.CheckBox;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.Slider;
import com.rabbit.gui.component.display.Picture;
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
	private ScrollableDisplayList rosterDisplayList;

	public Home() {
		setBackground(new DefaultBackground());
		title = "Admin Gui";
	}

	private float mapClamp(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
		float outVal = ((((value - inputMin) / (inputMax - inputMin)) * (outputMax - outputMin)) + outputMin);
		return Math.max(outputMin, Math.min(outputMax, outVal));
	}

	@Override
	public void setup() {
		super.setup();

		admin = Minecraft.getMinecraft().thePlayer;
		isCreative = admin.capabilities.isCreativeMode;

		registerComponent(
				new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Teacher Control", TextAlignment.CENTER));

		// the side buttons
		registerComponent(new PictureButton((int) (width * .03), (int) (height * .2), 30, 30,
				new ResourceLocation("minecraft", "textures/items/nether_star.png")).setIsEnabled(false)
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
				new ResourceLocation("minecraft", "textures/items/fish_clownfish_raw.png")).setIsEnabled(true)
						.addHoverText("Manage Students").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudents())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .8), 30, 30,
				new ResourceLocation("minecraft", "textures/items/cookie.png")).setIsEnabled(true)
						.addHoverText("See Students' Usernames and Passwords").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new UsernamesAndPasswords())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .35), 30, 30,
				new ResourceLocation("minecraft", "textures/items/emerald.png")).setIsEnabled(true)
						.addHoverText("Give Items").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new GiveItem())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .5), 30, 30,
				new ResourceLocation("minecraft", "textures/items/sugar.png")).setIsEnabled(true)
						.addHoverText("Remove Items").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new RemoveItem())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .65), 30, 30,
				new ResourceLocation("minecraft", "textures/items/ender_eye.png")).setIsEnabled(true)
						.addHoverText("Award Achievements").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new GiveAchievement())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .8), 30, 30,
				new ResourceLocation("minecraft", "textures/items/book_writable.png")).setIsEnabled(true)
						.addHoverText("Check Achievements").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new CheckPlayerAchievements())));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : AdminUI.roster) {
			rlist.add(new StringEntry(s));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .35), (int) (height * .3), (int) (width / 3.3),
				100, 15, rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		// gui main area
		registerComponent(new CheckBox((int) (width * .4), (int) (height * .22), "Set Creative Mode", isCreative)
				.setStatusChangedListener(btn -> toggleCreative()));

		// registerComponent(new Button((int) (width * .2), (int) (height * .2),
		// 150, 20, "Write Out Achievements")
		// .setClickListener(but -> PacketDispatcher.sendToServer(new
		// HaveServerWriteAchievementsMessage())));

		// time of day
		registerComponent(new TextLabel(width / 6, (int) (height * .75), width / 3, 20, "Set the Time of Day",
				TextAlignment.CENTER));

		registerComponent(new Slider((width / 6) + 15, (int) (height * .8), 120, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos))
				.setProgress(
						mapClamp((Minecraft.getMinecraft().theWorld.getWorldTime() + 6000) % 24000, 0, 24000, 0, 1))
				.setId("tod"));

		// speed slider
		registerComponent(new TextLabel((int) (width * .5), (int) (height * .75), width / 3, 20,
				"Set your movement speed", TextAlignment.CENTER));

		registerComponent(new Slider((int) ((width * .5) + 15), (int) (height * .8), 120, 20, 10)
				.setProgressChangedListener((Slider s, float pos) -> sliderChanged(s, pos)).setId("speed"));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				new ResourceLocation("dyn", "textures/gui/background.png")));
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

	private void toggleCreative() {
		admin.sendChatMessage("/gamemode " + (isCreative ? "0" : "1"));
		isCreative = !isCreative;
	}
}
