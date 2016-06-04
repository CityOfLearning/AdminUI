package com.dyn.admin.gui;

import java.util.ArrayList;

import com.dyn.admin.AdminUI;
import com.dyn.names.manager.NamesManager;
import com.dyn.server.database.DBManager;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

import net.minecraft.util.ResourceLocation;

public class UsernamesAndPasswords extends Show {

	private ScrollableDisplayList userNamesAndPasswords;

	public UsernamesAndPasswords() {
		setBackground(new DefaultBackground());
		title = "Admin Gui Usernames and Passwords";
	}

	@Override
	public void setup() {
		super.setup();

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Usernames and Passwords",
				TextAlignment.CENTER));

		ArrayList<ListEntry> list = new ArrayList<ListEntry>();

		for (String s : AdminUI.roster) {
			String dynusername = NamesManager.getDYNUsername(s.split("-")[0]);
			String dynpassword = DBManager.getPasswordFromDYNUsername(dynusername);
			list.add(new StringEntry(dynusername + " : " + dynpassword));
		}

		userNamesAndPasswords = new ScrollableDisplayList((int) (width * .35), (int) (height * .25), width / 3, 150, 15,
				list);
		userNamesAndPasswords.setId("usernamesandpasswords");
		registerComponent(userNamesAndPasswords);

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
				new ResourceLocation("minecraft", "textures/items/cookie.png")).setIsEnabled(false)
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

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				new ResourceLocation("dyn", "textures/gui/background.png")));
	}
}