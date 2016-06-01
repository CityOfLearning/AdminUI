package com.dyn.admin.gui;

import java.util.ArrayList;
import java.util.List;

import com.dyn.achievements.achievement.AchievementPlus;
import com.dyn.achievements.achievement.RequirementType;
import com.dyn.achievements.achievement.Requirements.BaseRequirement;
import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.admin.AdminUI;
import com.dyn.admin.gui.CheckPlayerAchievements;
import com.dyn.admin.gui.GiveAchievement;
import com.dyn.admin.gui.GiveItem;
import com.dyn.admin.gui.Home;
import com.dyn.admin.gui.ManageStudent;
import com.dyn.admin.gui.ManageStudents;
import com.dyn.admin.gui.RemoveItem;
import com.dyn.admin.gui.Roster;
import com.dyn.admin.gui.UsernamesAndPasswords;
import com.dyn.server.ServerMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.RequestUserAchievementsProgressMessage;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.DropDown;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class CheckPlayerAchievements extends Show {

	private static ScrollableDisplayList infoDisplayList;
	private String user = "";

	public CheckPlayerAchievements() {
		setBackground(new DefaultBackground());
		title = "Teacher Gui";
	}

	private void entrySelected(DropDown<String> dropdown, String selected) {
		// lets pull up the achievement info when the selection is clicked
		if (dropdown.getId().equals("roster")) {
			user = selected;
			PacketDispatcher.sendToServer(new RequestUserAchievementsProgressMessage(user.split("-")[0]));
		} else if (!user.isEmpty() && dropdown.getId().equals("achs")) {
			AchievementPlus ach = AchievementManager.findAchievementByName(selected.split("-")[0]);
			ArrayList<ListEntry> ulist = new ArrayList<ListEntry>();

			if (ach.hasRequirementOfType(RequirementType.CRAFT)) {
				ulist.add(new StringEntry("-Craft-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.CRAFT)) {
				ulist.add(new StringEntry(
						r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
			}

			if (ach.hasRequirementOfType(RequirementType.SMELT)) {
				ulist.add(new StringEntry("-Smelt-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.SMELT)) {
				ulist.add(new StringEntry(
						r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
			}

			if (ach.hasRequirementOfType(RequirementType.PICKUP)) {
				ulist.add(new StringEntry("-Pickup-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.PICKUP)) {
				ulist.add(new StringEntry(
						r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
			}

			if (ach.hasRequirementOfType(RequirementType.STAT)) {
				ulist.add(new StringEntry("-Special-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.STAT)) {
				ulist.add(new StringEntry(
						r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
			}

			if (ach.hasRequirementOfType(RequirementType.KILL)) {
				ulist.add(new StringEntry("-Kill-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.KILL)) {
				ulist.add(new StringEntry(
						r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
			}

			if (ach.hasRequirementOfType(RequirementType.BREW)) {
				ulist.add(new StringEntry("-Brew-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.BREW)) {
				ulist.add(new StringEntry(
						r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
			}

			if (ach.hasRequirementOfType(RequirementType.PLACE)) {
				ulist.add(new StringEntry("-Place-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.PLACE)) {
				ulist.add(new StringEntry(
						r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
			}
			if (ach.hasRequirementOfType(RequirementType.BREAK)) {
				ulist.add(new StringEntry("-Break-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.BREAK)) {
				ulist.add(new StringEntry(
						r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
			}
			if (ach.hasRequirementOfType(RequirementType.LOCATION)) {
				ulist.add(new StringEntry("-Location-"));
			}
			for (BaseRequirement r : ServerMod.userAchievementProgress.get(selected.split("-")[0])
					.getRequirementsByType(RequirementType.LOCATION)) {
				ulist.add(new StringEntry((r.getTotalAquired() > 0 ? "[X]-" : "[ ]-") + r.getRequirementEntityName()));
			}
			if (ach.hasRequirementOfType(RequirementType.MENTOR)) {
				ulist.add(new StringEntry("-Mentor-"));
				ulist.add(new StringEntry("Only a mentor can"));
				ulist.add(new StringEntry("give this achievement"));
			}
			infoDisplayList.clear();
			for (ListEntry us : ulist) {
				StringEntry se = (StringEntry) us;
				infoDisplayList.add(se);
				System.out.println(se.getTitle());
			}
			/*
			 * infoDisplayList = new ScrollableDisplayList((int) (width * .5),
			 * (int) (height * .25), width / 3, 150, 15, ulist);
			 */

		}

	}

	@Override
	public void setup() {
		super.setup();

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Check Achievement Progress",
				TextAlignment.CENTER));

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
				new ResourceLocation("minecraft", "textures/items/book_writable.png")).setIsEnabled(false)
						.addHoverText("Check Achievements").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new CheckPlayerAchievements())));

		// The students on the Roster List for this class
		DropDown<String> rosterDrop = new DropDown<String>((int) (width * .2), (int) (height * .3), width / 4, "Users");

		for (String s : AdminUI.roster) {
			rosterDrop.add(s, s);
		}
		rosterDrop.setItemSelectedListener(
				(DropDown<String> dropdown, String selected) -> entrySelected(dropdown, selected));
		rosterDrop.setId("roster");

		// Achievements by name
		DropDown<String> achDrop = new DropDown<String>((int) (width * .2), (int) (height * .45), width / 4,
				"Achievements");

		for (AchievementPlus a : AchievementManager.getAllAchievements()) {
			achDrop.add(a.getName(), a.getName());
		}
		achDrop.setItemSelectedListener(
				(DropDown<String> dropdown, String selected) -> entrySelected(dropdown, selected));

		achDrop.setId("achs");

		PacketDispatcher.sendToServer(
				new RequestUserAchievementsProgressMessage(Minecraft.getMinecraft().thePlayer.getDisplayNameString()));

		registerComponent(rosterDrop);
		registerComponent(achDrop);

		List<ListEntry> content = new ArrayList<ListEntry>();
		content.add(new StringEntry(""));
		infoDisplayList = new ScrollableDisplayList((int) (width * .5), (int) (height * .25), width / 3, 150, 15,
				content);

		registerComponent(infoDisplayList);

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				new ResourceLocation("dyn", "textures/gui/background.png")));
	}
}
