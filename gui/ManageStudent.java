package com.dyn.admin.gui;

import java.util.ArrayList;
import java.util.List;

import com.dyn.admin.AdminUI;
import com.dyn.names.manager.NamesManager;
import com.dyn.server.ServerMod;
import com.dyn.server.database.DBManager;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.FeedPlayerMessage;
import com.dyn.server.packets.server.RemoveEffectsMessage;
import com.dyn.server.packets.server.RequestFreezePlayerMessage;
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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

public class ManageStudent extends Show {

	private EntityPlayerSP admin;
	private SelectStringEntry selectedEntry;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<String> userlist = new ArrayList<String>();

	private boolean isFrozen;
	private boolean isMuted;
	private boolean isStudentInCreative;

	private String muteText;
	private String freezeText;
	private String modeText;
	private String dynUsername;
	private String dynPassword;
	private PictureButton muteButton;
	private PictureButton freezeButton;
	private PictureButton modeButton;
	private TextLabel dynUsernameLabel;
	private TextLabel dynPasswordLabel;

	public ManageStudent() {
		setBackground(new DefaultBackground());
		title = "Admin GUI Manage A Student";
		freezeText = "Freeze Students";
		muteText = "Mute Students";
		modeText = "Creative Mode";
		isFrozen = false;
		isMuted = false;
		isStudentInCreative = false;
		dynUsername = "";
		dynPassword = "";
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = entry;
		for (ListEntry listEntry : list.getContent()) {
			if (!listEntry.equals(entry)) {
				listEntry.setSelected(false);
			}
		}
		usernameAndPassword();
	}

	private void feedStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				PacketDispatcher.sendToServer(new FeedPlayerMessage(selectedEntry.getTitle().split("-")[0]));
			}
		}
	}

	private void freezeUnfreezeStudent() {
		if (selectedEntry != null) {
			if (isFrozen) {
				admin.sendChatMessage("/p user " + selectedEntry.getTitle().split("-")[0] + " group add _FROZEN_");
			} else {
				admin.sendChatMessage("/p user " + selectedEntry.getTitle().split("-")[0] + " group remove _FROZEN_");
			}

			PacketDispatcher
					.sendToServer(new RequestFreezePlayerMessage(selectedEntry.getTitle().split("-")[0], isFrozen));

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
	}

	private void healStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				admin.sendChatMessage("/heal " + selectedEntry.getTitle().split("-")[0]);
			}
		}
	}

	private void muteUnmuteStudent() {
		if (selectedEntry != null) {
			if (isMuted) {
				admin.sendChatMessage("/mute " + selectedEntry.getTitle().split("-")[0]);
			} else {
				admin.sendChatMessage("/unmute " + selectedEntry.getTitle().split("-")[0]);
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
	}

	@Override
	public void setup() {
		super.setup();

		admin = Minecraft.getMinecraft().thePlayer;

		for (String s : ServerMod.usernames) {
			if (!AdminUI.roster.contains(s) && (s != Minecraft.getMinecraft().thePlayer.getDisplayNameString())) {
				userlist.add(s);
			}
		}

		registerComponent(
				new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Manage a Student", TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList<ListEntry>();

		for (String s : userlist) {
			ulist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		registerComponent(new TextBox((int) (width * .23), (int) (height * .25), width / 4, 20, "Search for User")
				.setId("rostersearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : AdminUI.roster) {
			rlist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		// the side buttons
		registerComponent(new PictureButton((int) (width * .03), (int) (height * .5), 30, 30,
				new ResourceLocation("dyn", "textures/gui/group.png")).setIsEnabled(true)
						.addHoverText("Manage Classroom").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .65), 30, 30,
				new ResourceLocation("dyn", "textures/gui/roster.png")).setIsEnabled(true)
						.addHoverText("Student Rosters").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new Roster())));

		registerComponent(new PictureButton((int) (width * .03), (int) (height * .8), 30, 30,
				new ResourceLocation("dyn", "textures/gui/user.png")).setIsEnabled(false)
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

		// GUI main section
		registerComponent(new Button((int) (width * .15), (int) (height * .25), 20, 20, "<>").addHoverText("Refresh")
				.doesDrawHoverText(true).setClickListener(but -> updateUserList()));

		freezeButton = new PictureButton((int) (width * .55), (int) (height * .25), 50, 25,
				new ResourceLocation("dyn", "textures/gui/user.png"));
		freezeButton.setIsEnabled(true).addHoverText(freezeText).doesDrawHoverText(true)
				.setClickListener(but -> freezeUnfreezeStudent());
		registerComponent(freezeButton);

		muteButton = new PictureButton((int) (width * .55), (int) (height * .365), 50, 25,
				new ResourceLocation("dyn", "textures/gui/unmute.png"));
		muteButton.setIsEnabled(true).addHoverText(muteText).doesDrawHoverText(true)
				.setClickListener(but -> muteUnmuteStudent());
		registerComponent(muteButton);

		modeButton = new PictureButton((int) (width * .55), (int) (height * .48), 50, 25,
				new ResourceLocation("minecraft", "textures/items/bread.png"));
		modeButton.setIsEnabled(true).addHoverText(modeText).doesDrawHoverText(true)
				.setClickListener(but -> switchMode());
		registerComponent(modeButton);

		registerComponent(new PictureButton((int) (width * .7), (int) (height * .25), 50, 25,
				new ResourceLocation("dyn", "textures/gui/heart.png")).setIsEnabled(true)
						.addHoverText("Heal Students").doesDrawHoverText(true).setClickListener(but -> healStudent()));

		registerComponent(new PictureButton((int) (width * .7), (int) (height * .365), 50, 25,
				new ResourceLocation("minecraft", "textures/items/chicken_cooked.png")).setIsEnabled(true)
						.addHoverText("Feed Students").doesDrawHoverText(true).setClickListener(but -> feedStudent()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .6), (int) (width / 3.3), 20, "Teleport to Student")
						.setClickListener(but -> teleportToStudent()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .7), (int) (width / 3.3), 20, "Teleport Student to Me")
						.setClickListener(but -> teleportStudentTo()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .8), (int) (width / 3.3), 20, "Remove Effects")
						.addHoverText("Removes effects like poison and invisibility").doesDrawHoverText(true)
						.setClickListener(but -> {
							if ((selectedEntry != null) && !selectedEntry.getTitle().isEmpty()) {
								PacketDispatcher
										.sendToServer(new RemoveEffectsMessage(selectedEntry.getTitle().split("-")[0]));
							}
						}));

		dynUsernameLabel = new TextLabel((int) (width * .15), (int) (height * .8), (int) (width / 2.5), 20,
				"Username: " + dynUsername);
		dynPasswordLabel = new TextLabel((int) (width * .15), (int) (height * .85), (int) (width / 2.5), 20,
				"Password: " + dynPassword);
		registerComponent(dynUsernameLabel);
		registerComponent(dynPasswordLabel);

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void switchMode() {
		if (selectedEntry != null) {
			admin.sendChatMessage(
					"/gamemode " + (isStudentInCreative ? "0 " : "1 ") + selectedEntry.getTitle().split("-")[0]);
			isStudentInCreative = !isStudentInCreative;
			if (isStudentInCreative) {
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
	}

	private void teleportStudentTo() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				admin.sendChatMessage(
						"/tp " + selectedEntry.getTitle().split("-")[0] + " " + admin.getDisplayNameString());
			}
		}
	}

	private void teleportToStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				admin.sendChatMessage(
						"/tp " + admin.getDisplayNameString() + " " + selectedEntry.getTitle().split("-")[0]);
			}
		}
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "rostersearch") {
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
		getStage().display(new Home());
	}

	private void usernameAndPassword() {
		if (selectedEntry != null) {
			dynUsername = NamesManager.getDYNUsername(selectedEntry.getTitle().split("-")[0]);
			dynPassword = DBManager.getPasswordFromDYNUsername(dynUsername);
		} else {
			dynUsername = "";
			dynPassword = "";
		}
	}
}
