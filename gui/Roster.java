package com.dyn.admin.gui;

import java.util.ArrayList;

import com.dyn.admin.AdminUI;
import com.dyn.names.manager.NamesManager;
import com.dyn.server.ServerMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.RequestUserlistMessage;
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

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Roster extends Show {

	private SelectStringEntry selectedEntry;
	private DisplayList selectedList;
	private ScrollableDisplayList userDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<String> userlist = new ArrayList<String>();
	TextLabel numberOfStudentsOnRoster;

	public Roster() {
		setBackground(new DefaultBackground());
		title = "Admin Gui Roster Management";
	}

	private void addToRoster() {
		if ((selectedEntry != null) && (selectedList != null)) {
			if ((selectedList.getId() == "users") && !AdminUI.roster.contains(selectedEntry.getTitle())) {
				AdminUI.roster.add(selectedEntry.getTitle());
				selectedEntry.setSelected(false);
				rosterDisplayList.add(selectedEntry);
				userDisplayList.remove(selectedEntry);
			}
		}
		numberOfStudentsOnRoster.setText("Roster Count: " + AdminUI.roster.size());
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = entry;
		selectedList = list;

	}

	private void removeFromRoster() {
		if ((selectedEntry != null) && (selectedList != null)) {
			if ((selectedList.getId() == "roster") && AdminUI.roster.contains(selectedEntry.getTitle())) {
				AdminUI.roster.remove(selectedEntry.getTitle());
				selectedEntry.setSelected(false);
				rosterDisplayList.remove(selectedEntry);
				userDisplayList.add(selectedEntry);
			}
		}
		numberOfStudentsOnRoster.setText("Roster Count: " + AdminUI.roster.size());
	}

	@Override
	public void setup() {
		super.setup();

		for (String s : ServerMod.usernames) {
			if (!AdminUI.roster.contains(s + "-" + NamesManager.getDYNUsername(s))
					&& !s.equals(Minecraft.getMinecraft().thePlayer.getDisplayNameString())) {
				// Erin added this!
				s += "-" + NamesManager.getDYNUsername(s);
				userlist.add(s);
			}
		}

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Roster Management",
				TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList<ListEntry>();

		for (String s : userlist) {
			ulist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		registerComponent(
				new TextBox((int) (width * .15), (int) (height * .25), (int) (width / 3.3), 20, "Search for User")
						.setId("usersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));
		registerComponent(
				new TextBox((int) (width * .55), (int) (height * .25), (int) (width / 3.3), 20, "Search for User")
						.setId("rostersearch").setTextChangedListener(
								(TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		userDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), (int) (width / 3.3), 130,
				15, ulist);
		userDisplayList.setId("users");
		registerComponent(userDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : AdminUI.roster) {
			rlist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .55), (int) (height * .35), (int) (width / 3.3),
				130, 15, rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		// Buttons
		registerComponent(
				new Button((width / 2) - 10, (int) (height * .4), 20, 20, ">>").setClickListener(but -> addToRoster()));

		registerComponent(new Button((width / 2) - 10, (int) (height * .6), 20, 20, "<<")
				.setClickListener(but -> removeFromRoster()));

		registerComponent(new Button((width / 2) - 10, (int) (height * .8), 20, 20, "<>").addHoverText("Refresh")
				.doesDrawHoverText(true).setClickListener(but -> updateUserList()));

		numberOfStudentsOnRoster = new TextLabel((int) (width * .5) + 20, (int) (height * .2), 90, 20,
				"Roster Count: " + AdminUI.roster.size(), TextAlignment.LEFT);
		registerComponent(numberOfStudentsOnRoster);

		// the side buttons
		registerComponent(new PictureButton((int) (width * .03), (int) (height * .5), 30, 30,
				new ResourceLocation("dyn", "textures/gui/group.png")).setIsEnabled(true)
						.addHoverText("Manage Classroom").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .65), 30, 30,
				new ResourceLocation("dyn", "textures/gui/roster.png")).setIsEnabled(false)
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

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "usersearch") {
			userDisplayList.clear();
			for (String student : userlist) {
				if (student.contains(textbox.getText())) {
					userDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "rostersearch") {
			rosterDisplayList.clear();
			for (String student : AdminUI.roster) {
				if (student.contains(textbox.getText())) {
					rosterDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}

	private void updateUserList() {
		PacketDispatcher.sendToServer(new RequestUserlistMessage());
		getStage().display(new Roster());
	}
}
