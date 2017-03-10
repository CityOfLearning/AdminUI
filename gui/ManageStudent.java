package com.dyn.admin.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.admin.AdminUI;
import com.dyn.server.database.DBManager;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.server.FeedPlayerMessage;
import com.dyn.server.network.packets.server.RemoveEffectsMessage;
import com.dyn.server.network.packets.server.RequestFreezePlayerMessage;
import com.dyn.server.network.packets.server.RequestUserStatusMessage;
import com.dyn.server.network.packets.server.RequestUserlistMessage;
import com.dyn.utils.BooleanChangeListener;
import com.google.gson.JsonObject;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBoxButton;
import com.rabbit.gui.component.control.CheckBoxPictureButton;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.control.PictureToggleButton;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.SelectListEntry;
import com.rabbit.gui.component.list.entries.SelectStringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

public class ManageStudent extends Show {

	private EntityPlayerSP admin;
	private SelectListEntry selectedEntry;
	private ScrollableDisplayList userDisplayList;
	private ArrayList<String> userlist = new ArrayList<>();

	private boolean isFrozen;
	private boolean isMuted;
	private boolean isStudentInCreative;

	private String muteText;
	private String freezeText;
	private String modeText;
	private String dynUsername;
	private String dynPassword;
	private PictureToggleButton muteButton;
	private CheckBoxPictureButton freezeButton;
	private CheckBoxButton modeButton;
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

		BooleanChangeListener listener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				isFrozen = DYNServerMod.playerStatus.get("frozen").getAsBoolean();
				freezeButton.setToggle(isFrozen);
				isMuted = DYNServerMod.playerStatus.get("muted").getAsBoolean();
				muteButton.setToggle(isMuted);
				isStudentInCreative = DYNServerMod.playerStatus.get("mode").getAsBoolean();
				modeButton.setToggle(isStudentInCreative);
			}
		};

		DYNServerMod.playerStatusReturned.addBooleanChangeListener(listener, this);
	}

	private void entryClicked(SelectListEntry entry, DisplayList list, int mouseX, int mouseY) {
		selectedEntry = entry;
		for (ListEntry listEntry : list.getContent()) {
			if (!listEntry.equals(entry)) {
				listEntry.setSelected(false);
			}
		}
		NetworkManager.sendToServer(new RequestUserStatusMessage(selectedEntry.getTitle()));
		usernameAndPassword();
	}

	private void feedStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				NetworkManager.sendToServer(new FeedPlayerMessage(selectedEntry.getTitle()));
			}
		}
	}

	private void freezeUnfreezeStudent() {
		if (selectedEntry != null) {
			isFrozen = !isFrozen;
			NetworkManager.sendToServer(new RequestFreezePlayerMessage(selectedEntry.getTitle(), isFrozen));
			if (isFrozen) {
				freezeText = "UnFreeze Student";
				List<String> text = freezeButton.getHoverText();
				text.clear();
				text.add(freezeText);
				freezeButton.setHoverText(text);
			} else {
				freezeText = "Freeze Student";
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
				admin.sendChatMessage("/heal " + selectedEntry.getTitle());
			}
		}
	}

	private void muteUnmuteStudent() {
		if (selectedEntry != null) {
			if (!isMuted) {
				admin.sendChatMessage("/mute " + selectedEntry.getTitle());
			} else {
				admin.sendChatMessage("/unmute " + selectedEntry.getTitle());
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
	public void onClose() {
		DYNServerMod.playerStatusReturned.removeBooleanChangeListener(this);
	}

	@Override
	public void setup() {
		super.setup();

		admin = Minecraft.getMinecraft().thePlayer;

		SideButtons.init(this, 3);

		for (String s : AdminUI.adminSubRoster) {
			userlist.add(s);
		}

		registerComponent(
				new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Manage a Student", TextAlignment.CENTER));

		// The students not on the Roster List for this class
		ArrayList<ListEntry> ulist = new ArrayList<>();

		for (String s : userlist) {
			ulist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		registerComponent(new TextBox((int) (width * .23), (int) (height * .25), width / 4, 20, "Search for User")
				.setId("rostersearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<>();

		for (String s : AdminUI.adminSubRoster) {
			rlist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

//		for (String s : AdminUI.adminSubRoster) {
//			rlist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
//					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
//		}

		userDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), width / 3, 100, 15,
				rlist);
		userDisplayList.setId("roster");
		registerComponent(userDisplayList);

		// GUI main section
		registerComponent(
				new PictureButton((int) (width * .15), (int) (height * .25), 20, 20, DYNServerConstants.REFRESH_IMAGE)
						.addHoverText("Refresh").setDoesDrawHoverText(true).setClickListener(
								but -> NetworkManager.sendToServer(new RequestUserlistMessage())));

		freezeButton = new CheckBoxPictureButton((int) (width * .55), (int) (height * .25), 50, 25,
				DYNServerConstants.FREEZE_IMAGE, false);
		freezeButton.setIsEnabled(true).addHoverText(freezeText).setDoesDrawHoverText(true)
				.setClickListener(but -> freezeUnfreezeStudent());
		registerComponent(freezeButton);

		muteButton = new PictureToggleButton((int) (width * .55), (int) (height * .365), 50, 25,
				DYNServerConstants.UNMUTE_IMAGE, DYNServerConstants.MUTE_IMAGE, false);
		muteButton.setIsEnabled(true).addHoverText(muteText).setDoesDrawHoverText(true)
				.setClickListener(but -> muteUnmuteStudent());
		registerComponent(muteButton);

		modeButton = new CheckBoxButton((int) (width * .55), (int) (height * .5), (int) (width / 3.3), 20,
				"Toggle Creative", false);
		modeButton.setIsEnabled(true).addHoverText(modeText).setDoesDrawHoverText(true)
				.setClickListener(but -> switchMode());
		registerComponent(modeButton);

		registerComponent(
				new PictureButton((int) (width * .7), (int) (height * .25), 50, 25, DYNServerConstants.HEART_IMAGE)
						.setIsEnabled(true).addHoverText("Heal Students").setDoesDrawHoverText(true)
						.setClickListener(but -> healStudent()));

		registerComponent(new PictureButton((int) (width * .7), (int) (height * .365), 50, 25,
				new ResourceLocation("minecraft", "textures/items/chicken_cooked.png")).setIsEnabled(true)
						.addHoverText("Feed Students").setDoesDrawHoverText(true)
						.setClickListener(but -> feedStudent()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .6), (int) (width / 3.3), 20, "Teleport to Student")
						.setClickListener(but -> teleportToStudent()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .7), (int) (width / 3.3), 20, "Teleport Student to Me")
						.setClickListener(but -> teleportStudentTo()));

		registerComponent(
				new Button((int) (width * .55), (int) (height * .8), (int) (width / 3.3), 20, "Remove Effects")
						.addHoverText("Removes effects like poison and invisibility").setDoesDrawHoverText(true)
						.setClickListener(but -> {
							if ((selectedEntry != null) && !selectedEntry.getTitle().isEmpty()) {
								NetworkManager.sendToServer(new RemoveEffectsMessage(selectedEntry.getTitle()));
							}
						}));

		dynUsernameLabel = new TextLabel((int) (width * .15), (int) (height * .8), (int) (width / 2.5), 20, Color.black,
				"Username: " + dynUsername);
		dynPasswordLabel = new TextLabel((int) (width * .15), (int) (height * .85), (int) (width / 2.5), 20,
				Color.black, "Password: " + dynPassword);
		registerComponent(dynUsernameLabel);
		registerComponent(dynPasswordLabel);

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}

	private void switchMode() {
		if (selectedEntry != null) {
			admin.sendChatMessage("/gamemode " + (isStudentInCreative ? "0 " : "1 ") + selectedEntry.getTitle());
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
				admin.sendChatMessage("/tp " + selectedEntry.getTitle() + " " + admin.getDisplayNameString());
			}
		}
	}

	private void teleportToStudent() {
		if (selectedEntry != null) {
			if (!selectedEntry.getTitle().isEmpty()) {
				admin.sendChatMessage("/tp " + admin.getDisplayNameString() + " " + selectedEntry.getTitle());
			}
		}
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "rostersearch") {
			userDisplayList.clear();
			for (String student : AdminUI.adminSubRoster) {
				if (student.contains(textbox.getText())) {
					userDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}
	}

	private void usernameAndPassword() {
		if (selectedEntry != null) {
			for (String student : AdminUI.adminSubRoster) {
				if (student.equals(selectedEntry.getTitle())) {

					JsonObject info = DBManager.getInfoFromUserAccount(DBManager.getUserIDFromMCUsername(student));
					if ((info != null) && info.has("username") && info.has("password")) {
						dynUsername = info.get("username").getAsString();
						dynPassword = info.get("password").getAsString();
					}
				}
			}
		} else {
			dynUsername = "";
			dynPassword = "";
		}
	}
}
