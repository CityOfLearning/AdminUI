package com.dyn.admin.gui;

import java.util.Map.Entry;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.admin.AdminUI;
import com.dyn.server.network.NetworkManager;
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
	private DropDown<Integer> worlds;
	private DropDown<Integer> zones;
	private ScrollableDisplayList permDisplayList;
	private BooleanChangeListener grouplistener;
	private BooleanChangeListener worldlistener;
	private BooleanChangeListener zonelistener;
	private BooleanChangeListener permissionlistener;

	String group, world;
	int zone;

	public PermissionGui() {
		setBackground(new DefaultBackground());
		title = "Permission Management";

		NetworkManager.sendToServer(new RequestWorldListMessage());
		NetworkManager.sendToServer(new RequestGroupListMessage());
	}

	private void dropdownSelected(DropDown dropdown, String selected) {
		// this will determine the permission view
		if (dropdown.getId().equals("group")) {
			group = selected;
			NetworkManager.sendToServer(new RequestGroupPermissionsMessage(group.trim()));
		} else if (dropdown.getId().equals("world")) {
			world = selected;
			NetworkManager
					.sendToServer(new RequestWorldZonesMessage((int) dropdown.getSelectedElement().getValue(), false));
		} else if (dropdown.getId().equals("zone")) {
			zone = (int) dropdown.getSelectedElement().getValue();
			if (group != null) {
				NetworkManager.sendToServer(new RequestZonePermissionsMessage(zone, group));
			} else {
				NetworkManager.sendToServer(new RequestZonePermissionsMessage(zone));
			}
		}

	}

	@Override
	public void onClose() {
		AdminUI.groupsMessageRecieved.removeBooleanChangeListener(grouplistener);
		DYNServerMod.worldsMessageRecieved.removeBooleanChangeListener(worldlistener);
		AdminUI.zonesMessageRecieved.removeBooleanChangeListener(zonelistener);
		AdminUI.permissionsMessageRecieved.removeBooleanChangeListener(permissionlistener);
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

		worlds = new DropDown<Integer>((int) (width * .3875), (int) (height * .2), (int) (width / 4.5), 15)
				.setId("world").setDrawUnicode(true)
				.setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
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

		grouplistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				((PermissionGui) show).updateGroups();
				event.getDispatcher().setFlag(false);
			}
		};

		AdminUI.groupsMessageRecieved.setFlag(false);
		AdminUI.groupsMessageRecieved.addBooleanChangeListener(grouplistener, this);

		worldlistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				((PermissionGui) show).updateWorlds();
				event.getDispatcher().setFlag(false);
			}
		};

		DYNServerMod.worldsMessageRecieved.setFlag(false);
		DYNServerMod.worldsMessageRecieved.addBooleanChangeListener(worldlistener, this);

		zonelistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				((PermissionGui) show).updateZones();
				event.getDispatcher().setFlag(false);
			}
		};

		AdminUI.zonesMessageRecieved.setFlag(false);
		AdminUI.zonesMessageRecieved.addBooleanChangeListener(zonelistener, this);

		permissionlistener = (event, show) -> {
			if (event.getDispatcher().getFlag()) {
				((PermissionGui) show).updatePermissions();
				event.getDispatcher().setFlag(false);
			}
		};

		AdminUI.permissionsMessageRecieved.setFlag(false);
		AdminUI.permissionsMessageRecieved.addBooleanChangeListener(permissionlistener, this);
	}

	public void updateGroups() {
		groups.clear();
		for (String group : AdminUI.groups) {
			groups.add(group);
		}
	}

	public void updatePermissions() {
		permDisplayList.clear();
		for (String perm : AdminUI.permissions) {
			if (perm.contains("#") || perm.contains(":")) {
				permDisplayList.add(new StringEntry(perm));
			} else {
				permDisplayList.add(new StringEntry(perm).setTextAlignment(TextAlignment.LEFT));
			}

		}
	}

	public void updateWorlds() {
		worlds.clear();
		for (Entry<Integer, String> world : DYNServerMod.worlds.entrySet()) {
			worlds.add(world.getValue(), world.getKey());
		}
	}

	public void updateZones() {
		zones.clear();
		for (Integer group : AdminUI.zones.keySet()) {
			zones.add(AdminUI.zones.get(group), group);
		}
	}
}
