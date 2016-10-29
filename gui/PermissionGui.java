package com.dyn.admin.gui;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.admin.AdminUI;
import com.dyn.mentor.gui.SideButtons;
import com.dyn.server.network.NetworkDispatcher;
import com.dyn.server.network.packets.server.RequestGroupListMessage;
import com.dyn.server.network.packets.server.RequestGroupPermissionsMessage;
import com.dyn.server.network.packets.server.RequestWorldListMessage;
import com.dyn.server.network.packets.server.RequestWorldZonesMessage;
import com.dyn.server.network.packets.server.RequestZonePermissionsMessage;
import com.dyn.utils.BooleanChangeListener;
import com.google.common.collect.Lists;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.DropDown;
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

		NetworkDispatcher.sendToServer(new RequestWorldListMessage());
		NetworkDispatcher.sendToServer(new RequestGroupListMessage());

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
				for (String group : DYNServerMod.worlds.values()) {
					worlds.add(group);
				}
				event.getDispatcher().setFlag(false);
			}
		};

		DYNServerMod.worldsMessageRecieved.addBooleanChangeListener(worldlistener);

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
			NetworkDispatcher.sendToServer(new RequestGroupPermissionsMessage(group));
		} else if (dropdown.getId().equals("world")) {
			world = selected;
			NetworkDispatcher.sendToServer(new RequestWorldZonesMessage(world));
		} else if (dropdown.getId().equals("zone")) {
			zone = (int) dropdown.getSelectedElement().getValue();
			System.out.println("Requesting Zone Perms");
			NetworkDispatcher.sendToServer(new RequestZonePermissionsMessage(zone));
		}

	}

	@Override
	public void setup() {
		super.setup();

		SideButtons.init(this, 6);

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

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}
}
