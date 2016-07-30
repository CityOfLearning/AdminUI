package com.dyn.admin.gui;

import com.dyn.DYNServerConstants;
import com.dyn.admin.AdminUI;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.RequestGroupListMessage;
import com.dyn.server.packets.server.RequestGroupPermissionsMessage;
import com.dyn.server.packets.server.RequestWorldListMessage;
import com.dyn.server.packets.server.RequestWorldZonesMessage;
import com.dyn.server.packets.server.RequestZonePermissionsMessage;
import com.dyn.utils.BooleanChangeListener;
import com.google.common.collect.Lists;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.DropDown;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

public class PermissionGui extends Show {

	private DropDown<String> groups;
	private DropDown<String> worlds;
	private DropDown<Integer> zones;
	private ScrollableDisplayList permDisplayList;

	String group, world;
	int zone;

	public PermissionGui() {
		setBackground(new DefaultBackground());
		title = "Permission Management";

		PacketDispatcher.sendToServer(new RequestWorldListMessage());
		PacketDispatcher.sendToServer(new RequestGroupListMessage());

		BooleanChangeListener grouplistener = event -> {
			if (event.getDispatcher().getFlag()) {
				groups.clear();
				for (String group : AdminUI.groups) {
					groups.add(group);
				}
				event.getDispatcher().setFlag(false);
			}
		};

		AdminUI.groupsMessageRecieved.addBooleanChangeListener(grouplistener);

		BooleanChangeListener worldlistener = event -> {
			if (event.getDispatcher().getFlag()) {
				worlds.clear();
				for (String group : AdminUI.worlds) {
					worlds.add(group);
				}
				event.getDispatcher().setFlag(false);
			}
		};

		AdminUI.worldsMessageRecieved.addBooleanChangeListener(worldlistener);

		BooleanChangeListener zonelistener = event -> {
			if (event.getDispatcher().getFlag()) {
				zones.clear();
				for (Integer group : AdminUI.zones.keySet()) {
					zones.add(AdminUI.zones.get(group), group);
				}
				event.getDispatcher().setFlag(false);
			}
		};

		AdminUI.zonesMessageRecieved.addBooleanChangeListener(zonelistener);

		BooleanChangeListener permissionlistener = event -> {
			if (event.getDispatcher().getFlag()) {
				permDisplayList.clear();
				for (String perm : AdminUI.permissions) {
					if (perm.contains("#")) {
						permDisplayList.add(new StringEntry(perm));
					} else {
						permDisplayList.add(new StringEntry(perm).setTextAlignment(TextAlignment.LEFT));
					}

				}
				event.getDispatcher().setFlag(false);
			}
		};

		AdminUI.permissionsMessageRecieved.addBooleanChangeListener(permissionlistener);
	}

	private void dropdownSelected(DropDown dropdown, String selected) {
		// this will determine the permission view
		System.out.println("Drop Down Selected " + dropdown.getId());
		if (dropdown.getId().equals("group")) {
			group = selected;
			PacketDispatcher.sendToServer(new RequestGroupPermissionsMessage(group));
		} else if (dropdown.getId().equals("world")) {
			world = selected;
			PacketDispatcher.sendToServer(new RequestWorldZonesMessage(world));
		} else if (dropdown.getId().equals("zone")) {
			zone = (int) dropdown.getSelectedElement().getValue();
			System.out.println("Requesting Zone Perms");
			PacketDispatcher.sendToServer(new RequestZonePermissionsMessage(zone));
		}

	}

	@Override
	public void setup() {
		super.setup();

		// registerComponent(new TextLabel((int) (width * .15), (int) (height *
		// .2), (int) (width / 3.3), 20, Color.black,
		// "Groups"));

		groups = new DropDown<String>((int) (width * .15), (int) (height * .2), (int) (width / 4.5), 15).setId("group")
				.setDrawUnicode(true).setItemSelectedListener((DropDown<String> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(groups);

		// registerComponent(new TextLabel((int) (width * .15), (int) (height *
		// .35), (int) (width / 3.3), 20, Color.black,
		// "Worlds"));

		worlds = new DropDown<String>((int) (width * .3875), (int) (height * .2), (int) (width / 4.5), 15)
				.setId("world").setDrawUnicode(true)
				.setItemSelectedListener((DropDown<String> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(worlds);

		// registerComponent(
		// new TextLabel((int) (width * .15), (int) (height * .5), (int) (width
		// / 3.3), 20, Color.black, "Zones"));

		zones = new DropDown<Integer>((int) (width * .625), (int) (height * .2), (int) (width / 4.5), 15).setId("zone")
				.setDrawUnicode(true).setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(zones);

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Permission Visualization",
				TextAlignment.CENTER));

		permDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .3), (int) (width / 1.45), 150,
				15, Lists.newArrayList());
		permDisplayList.setId("roster");
		registerComponent(permDisplayList);

		// the side buttons
		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_1.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_1.getRight()), 30, 30,
				DYNServerConstants.STUDENTS_IMAGE).setIsEnabled(true).addHoverText("Manage Classroom")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_2.getLeft()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_2.getRight()), 30, 30,
				DYNServerConstants.ROSTER_IMAGE).setIsEnabled(false).addHoverText("Student Rosters")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new PermissionGui())));

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
				DYNServerConstants.ACHIEVEMENT_IMAGE).setIsEnabled(true).addHoverText("Award Achievements")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new MonitorAchievements())));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}
}
