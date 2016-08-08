package com.dyn.admin.gui;

import java.util.ArrayList;
import java.util.List;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.achievements.achievement.AchievementPlus;
import com.dyn.achievements.achievement.RequirementType;
import com.dyn.achievements.achievement.Requirements.BaseRequirement;
import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.MentorGivingAchievementMessage;
import com.dyn.server.packets.server.RequestUserAchievementsProgressMessage;
import com.dyn.server.packets.server.RequestUserlistMessage;
import com.dyn.utils.BooleanChangeListener;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

public class MonitorAchievements extends Show {

	private static ScrollableDisplayList infoDisplayList;
	private ScrollableDisplayList achDisplayList;
	private ScrollableDisplayList userDisplayList;
	private SelectStringEntry selectedUser;
	private SelectStringEntry selectedAchievement;

	public MonitorAchievements() {
		setBackground(new DefaultBackground());
		title = "Admin Gui";

		BooleanChangeListener listener = event -> {
			if (event.getDispatcher().getFlag()) {
				userDisplayList.clear();
				for (String student : DYNServerMod.usernames) {
					userDisplayList.add(new SelectStringEntry(student));
				}
			}
		};
		DYNServerMod.serverUserlistReturned.addBooleanChangeListener(listener);
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		for (ListEntry listEntry : list.getContent()) {
			if (!listEntry.equals(entry)) {
				listEntry.setSelected(false);
			}
		}
		if (list.getId() == "achs") {
			selectedAchievement = entry;
		} else if (list.getId() == "roster") {
			selectedUser = entry;
			PacketDispatcher.sendToServer(new RequestUserAchievementsProgressMessage(selectedUser.getTitle()));
		}

		if ((selectedUser != null) && (selectedAchievement != null)) {
			AchievementPlus ach = AchievementManager.findAchievementByName(selectedAchievement.getTitle());
			ArrayList<ListEntry> ulist = new ArrayList<ListEntry>();

			if (ach.hasRequirementOfType(RequirementType.CRAFT)) {
				ulist.add(new SelectStringEntry("-Craft-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.CRAFT)) {
					ulist.add(new SelectStringEntry(
							r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.SMELT)) {
				ulist.add(new SelectStringEntry("-Smelt-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.SMELT)) {
					ulist.add(new SelectStringEntry(
							r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.PICKUP)) {
				ulist.add(new SelectStringEntry("-Pickup-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.PICKUP)) {
					ulist.add(new SelectStringEntry(
							r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.STAT)) {
				ulist.add(new SelectStringEntry("-Special-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.STAT)) {
					ulist.add(new SelectStringEntry(
							r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.KILL)) {
				ulist.add(new SelectStringEntry("-Kill-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.KILL)) {
					ulist.add(new SelectStringEntry(
							r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.BREW)) {
				ulist.add(new SelectStringEntry("-Brew-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.BREW)) {
					ulist.add(new SelectStringEntry(
							r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.PLACE)) {
				ulist.add(new SelectStringEntry("-Place-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.PLACE)) {
					ulist.add(new SelectStringEntry(
							r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.BREAK)) {
				ulist.add(new SelectStringEntry("-Break-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.BREAK)) {
					ulist.add(new SelectStringEntry(
							r.getRequirementEntityName() + " - " + r.getTotalAquired() + "/" + r.getTotalNeeded()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.LOCATION)) {
				ulist.add(new SelectStringEntry("-Location-"));

				for (BaseRequirement r : DYNServerMod.userAchievementProgress.get(ach.getName())
						.getRequirementsByType(RequirementType.LOCATION)) {
					ulist.add(new SelectStringEntry(
							(r.getTotalAquired() > 0 ? "[X]-" : "[ ]-") + r.getRequirementEntityName()));
				}
			}
			if (ach.hasRequirementOfType(RequirementType.MENTOR)) {
				ulist.add(new SelectStringEntry("-Mentor-"));
				ulist.add(new SelectStringEntry("Only a mentor can"));
				ulist.add(new SelectStringEntry("give this achievement"));
			}
			infoDisplayList.clear();
			for (ListEntry us : ulist) {
				SelectStringEntry se = (SelectStringEntry) us;
				infoDisplayList.add(se);
			}
		}
	}

	@Override
	public void setup() {
		super.setup();

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Award Achievements",
				TextAlignment.CENTER));

		// the side buttons
		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_1.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_1.getRight()), 30, 30,
				DYNServerConstants.STUDENTS_IMAGE).setIsEnabled(true).addHoverText("Manage Classroom")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_2.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_2.getRight()), 30, 30,
				DYNServerConstants.ROSTER_IMAGE).setIsEnabled(true).addHoverText("Student Rosters")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Roster())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_3.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_3.getRight()), 30, 30,
				DYNServerConstants.STUDENT_IMAGE).setIsEnabled(true).addHoverText("Manage a Student")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new ManageStudent())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_4.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_4.getRight()), 30, 30,
				DYNServerConstants.INVENTORY_IMAGE).setIsEnabled(true).addHoverText("Manage Inventory")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudentsInventory())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_5.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_5.getRight()), 30, 30,
				DYNServerConstants.ACHIEVEMENT_IMAGE).setIsEnabled(false).addHoverText("Award Achievements")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new MonitorAchievements())));

		registerComponent(
				new PictureButton((int) (width * .15), (int) (height * .2), 20, 20, DYNServerConstants.REFRESH_IMAGE)
						.addHoverText("Refresh").doesDrawHoverText(true).setClickListener(
								but -> PacketDispatcher.sendToServer(new RequestUserlistMessage())));

		registerComponent(new TextBox((int) (width * .235), (int) (height * .2), width / 4, 20, "Search for User")
				.setId("usersearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));
		registerComponent(new TextBox((int) (width * .15), (int) (height * .55), width / 3, 20, "Search Achievements")
				.setId("achsearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		List<ListEntry> dslist = new ArrayList<ListEntry>();

		for (AchievementPlus a : AchievementManager.getAllAchievements()) {
			dslist.add(new SelectStringEntry(a.getName(), (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		achDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .65), width / 3, 50, 15,
				dslist);
		achDisplayList.setId("achs");

		registerComponent(achDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : DYNServerMod.usernames) {
			rlist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		userDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .3), width / 3, 50, 15, rlist);
		userDisplayList.setId("roster");
		registerComponent(userDisplayList);

		// we need a way to get the players DYN account too if possible...
		registerComponent(new Button((int) (width * .56), (int) (height * .8), width / 4, 20, "Award to Player")
				.setClickListener(but -> {
					if ((selectedUser != null) && (selectedAchievement != null) && !selectedUser.getTitle().isEmpty()
							&& !selectedAchievement.getTitle().isEmpty()) {
						PacketDispatcher.sendToServer(new MentorGivingAchievementMessage(selectedUser.getTitle(),
								AchievementManager.findAchievementByName(selectedAchievement.getTitle()).getId()));
					}
				}));

		List<ListEntry> content = new ArrayList<ListEntry>();
		content.add(new SelectStringEntry(""));
		infoDisplayList = new ScrollableDisplayList((int) (width * .53), (int) (height * .2), (int) (width / 3.2), 140,
				15, content);

		registerComponent(infoDisplayList);

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));

	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "achsearch") {
			achDisplayList.clear();
			for (AchievementPlus a : AchievementManager.getAllAchievements()) {
				if (a.getName().contains(textbox.getText().toLowerCase())) {
					achDisplayList.add(new SelectStringEntry(a.getName(), (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "usersearch") {
			userDisplayList.clear();
			for (String student : DYNServerMod.usernames) {
				if (student.toLowerCase().contains(textbox.getText().toLowerCase())) {
					userDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}
}
