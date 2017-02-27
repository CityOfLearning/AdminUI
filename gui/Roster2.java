package com.dyn.admin.gui;

import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.text.WordUtils;

import com.dyn.DYNServerConstants;
import com.dyn.admin.AdminUI;
import com.dyn.server.http.GetProgramRoster;
import com.dyn.server.http.GetPrograms;
import com.dyn.server.http.GetScheduledPrograms;
import com.dyn.server.keys.KeyManager;
import com.dyn.utils.BooleanChangeListener;
import com.dyn.utils.CCOLPlayerInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.DropDown;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.ProgressBar;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

public class Roster2 extends Show {

	private Map<UUID, CCOLPlayerInfo> tmpPlayerInfo = new HashMap<>();

	private DropDown<Integer> orgs;
	private DropDown<Integer> programs;
	private DropDown<Integer> scheduledProg;
	private ScrollableDisplayList rosterDisplayList;
	TextLabel numberOfStudentsOnRoster;
	TextLabel rosterStatus;
	ProgressBar progBar;

	int selectedOrg;
	int selectedProgram;

	public Roster2() {
		setBackground(new DefaultBackground());
		title = "Mentor Gui Roster Management";
	}

	private void dropdownSelected(DropDown<Integer> dropdown, String selected) {

		if (dropdown.getId().equals("org")) {
			selectedOrg = dropdown.getElement(selected).getValue();
			selectedProgram = 0;
			programs.clear();
			scheduledProg.clear();
			rosterDisplayList.clear();
			tmpPlayerInfo.clear();
			rosterStatus.setText("");

			GetPrograms progRequest = new GetPrograms(dropdown.getElement(selected).getValue(),
					KeyManager.getSecretKey(dropdown.getElement(selected).getValue()),
					KeyManager.getOrgKey(dropdown.getElement(selected).getValue()));

			BooleanChangeListener listener = (event, show) -> {
				if (event.getDispatcher().getFlag()) {
					JsonObject jObj = progRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							if (entryObj.has("id") && entryObj.has("name")) {
								programs.add(entryObj.get("name").getAsString(), entryObj.get("id").getAsInt());
							}
						}
					}
				}
			};

			progRequest.responseReceived.addBooleanChangeListener(listener, this);

		} else if (dropdown.getId().equals("program")) {

			scheduledProg.clear();
			rosterDisplayList.clear();
			tmpPlayerInfo.clear();
			rosterStatus.setText("");

			GetScheduledPrograms scheduleRequest = new GetScheduledPrograms(selectedOrg,
					dropdown.getElement(selected).getValue(), KeyManager.getSecretKey(selectedOrg),
					KeyManager.getOrgKey(selectedOrg));

			selectedProgram = dropdown.getElement(selected).getValue();

			BooleanChangeListener listener = (event, show) -> {
				if (event.getDispatcher().getFlag()) {
					JsonObject jObj = scheduleRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							if (entryObj.has("id") && entryObj.has("name") && entryObj.has("start_date")
									&& entryObj.has("end_date")
							/*
							 * && LocalDate.parse(entryObj.get("start_date").
							 * getAsString()) .isBefore(LocalDate.now())
							 */
									&& (LocalDate.parse(entryObj.get("end_date").getAsString())
											.compareTo(LocalDate.now()) >= 0)) {
								scheduledProg.add(entryObj.get("claim_code").getAsString() + " - "
										+ entryObj.get("name").getAsString(), entryObj.get("id").getAsInt());
							}
						}
					}
				}
			};

			scheduleRequest.responseReceived.addBooleanChangeListener(listener, this);

		} else if (dropdown.getId().equals("schedule")) {

			rosterDisplayList.clear();
			tmpPlayerInfo.clear();
			rosterStatus.setText("");

			GetProgramRoster rosterRequest = new GetProgramRoster(selectedOrg, selectedProgram,
					dropdown.getElement(selected).getValue(), KeyManager.getSecretKey(selectedOrg),
					KeyManager.getOrgKey(selectedOrg));

			BooleanChangeListener listener = (event, show) -> {
				if (event.getDispatcher().getFlag()) {
					JsonObject jObj = rosterRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						rosterDisplayList.add(new StringEntry("Student Name"));
						rosterDisplayList.add(new StringEntry("------------------------------------------"));
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							JsonObject userEntryObj = entryObj.get("user").getAsJsonObject();
							if (userEntryObj.has("username") && userEntryObj.has("full_name")) {
								if (!tmpPlayerInfo
										.containsKey(UUID.fromString(userEntryObj.get("uuid").getAsString()))) {
									String name = WordUtils.capitalizeFully(userEntryObj.get("full_name").isJsonNull()
											? "Unavailable" : userEntryObj.get("full_name").getAsString());
									tmpPlayerInfo.put(UUID.fromString(userEntryObj.get("uuid").getAsString()),
											new CCOLPlayerInfo(UUID.fromString(userEntryObj.get("uuid").getAsString()),
													Integer.parseInt(entryObj.get("link_id").getAsString()), name,
													userEntryObj.get("username").getAsString(), false));
									rosterDisplayList.add(new StringEntry(name));
								}
							}
						}
						numberOfStudentsOnRoster
								.setText("Roster Count: " + (rosterDisplayList.getContent().size() - 2));
					}
				}
			};

			rosterRequest.responseReceived.addBooleanChangeListener(listener, this);
		}
	}

	@Override
	public void setup() {
		super.setup();

		SideButtons.init(this, 2);

		registerComponent(
				new TextLabel((int) (width * .15), (int) (height * .2), (int) (width / 3.3), 20, Color.black, "Orgs"));

		orgs = new DropDown<Integer>((int) (width * .15), (int) (height * .25), (int) (width / 3.3), 20).add("DYN", 42)
				.setId("org").setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(orgs);

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .35), (int) (width / 3.3), 20, Color.black,
				"Programs"));

		programs = new DropDown<Integer>((int) (width * .15), (int) (height * .40), (int) (width / 3.3), 20)
				.setId("program").setDrawUnicode(true)
				.setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(programs);

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .5), (int) (width / 3.3), 20, Color.black,
				"Scheduled Programs"));

		scheduledProg = new DropDown<Integer>((int) (width * .15), (int) (height * .55), (int) (width / 3.3), 20)
				.setId("schedule").setDrawUnicode(true)
				.setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(scheduledProg);

		registerComponent(rosterStatus = new TextLabel((int) (width * .145), (int) (height * .65), width / 3, 20,
				Color.black, ""));

		registerComponent(progBar = new ProgressBar((int) (width * .15), (int) (height * .7), (int) (width / 3.3), 20));

		registerComponent(
				new Button((int) (width * .15), (int) (height * .8), (int) (width / 3.3), 20, "Set this as my Roster")
						.setClickListener(but -> {
							if (tmpPlayerInfo.size() > 0) {
								but.setIsEnabled(false);
								AdminUI.programRoster.addAll(tmpPlayerInfo.values());
								rosterStatus.setText("Adding Students to Roster");
								progBar.getProgressChangedListener().setValue(0);

								Runnable task = () -> {
									// this blocks and so we gotta thread it

									int progress = 0;
									for (CCOLPlayerInfo player : AdminUI.programRoster) {
										progBar.getProgressChangedListener()
												.setValue(((float) progress++) / AdminUI.programRoster.size());
										player.grabMissingData();
									}
									but.setIsEnabled(true);
									progBar.setVisible(false);
									rosterStatus.setText("Added Students to Roster");
								};
								progBar.setVisible(true);
								new Thread(task).start();

							}
						}));

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Roster Management",
				TextAlignment.CENTER));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<>();

		for (CCOLPlayerInfo user : AdminUI.programRoster) {
			rlist.add(new StringEntry(user.getCCOLName()));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .475), (int) (height * .25), (int) (width / 2.75),
				150, 15, rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		numberOfStudentsOnRoster = new TextLabel((int) (width * .5) + 20, (int) (height * .2), 90, 20, Color.black,
				"Roster Count: " + AdminUI.programRoster.size(), TextAlignment.LEFT);
		registerComponent(numberOfStudentsOnRoster);

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}
}
