package com.dyn.admin.gui;

import com.dyn.DYNServerConstants;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.show.Show;

public class SideButtons {

	private static PictureButton but1;
	private static PictureButton but2;
	private static PictureButton but3;
	private static PictureButton but4;
	private static PictureButton but5;
	private static PictureButton but6;
	private static PictureButton but7;

	public static void init(Show show, int pageNum) {

		show.registerComponent(but1 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNServerConstants.BUTTON_LOCATION_1.getLeft()),
				(int) (show.getHeight() * DYNServerConstants.BUTTON_LOCATION_1.getRight()), 30, 30,
				DYNServerConstants.STUDENTS_IMAGE).setIsEnabled(true).addHoverText("Manage Classroom")
						.doesDrawHoverText(true).setClickListener(but -> show.getStage().display(new Home())));

		show.registerComponent(but2 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNServerConstants.BUTTON_LOCATION_2.getLeft()),
				(int) (show.getHeight() * DYNServerConstants.BUTTON_LOCATION_2.getRight()), 30, 30,
				DYNServerConstants.ROSTER_IMAGE).setIsEnabled(true).addHoverText("Student Rosters")
						.doesDrawHoverText(true).setClickListener(but -> show.getStage().display(new Roster())));

		show.registerComponent(but3 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNServerConstants.BUTTON_LOCATION_3.getLeft()),
				(int) (show.getHeight() * DYNServerConstants.BUTTON_LOCATION_3.getRight()), 30, 30,
				DYNServerConstants.STUDENT_IMAGE).setIsEnabled(true).addHoverText("Manage a Student")
						.doesDrawHoverText(true).setClickListener(but -> show.getStage().display(new ManageStudent())));

		show.registerComponent(but4 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNServerConstants.BUTTON_LOCATION_4.getLeft()),
				(int) (show.getHeight() * DYNServerConstants.BUTTON_LOCATION_4.getRight()), 30, 30,
				DYNServerConstants.INVENTORY_IMAGE).setIsEnabled(true).addHoverText("Manage Inventory")
						.doesDrawHoverText(true)
						.setClickListener(but -> show.getStage().display(new ManageStudentsInventory())));

		show.registerComponent(but5 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNServerConstants.BUTTON_LOCATION_5.getLeft()),
				(int) (show.getHeight() * DYNServerConstants.BUTTON_LOCATION_5.getRight()), 30, 30,
				DYNServerConstants.ACHIEVEMENT_IMAGE).setIsEnabled(true).addHoverText("Award Achievements")
						.doesDrawHoverText(true)
						.setClickListener(but -> show.getStage().display(new MonitorAchievements())));

		show.registerComponent(but6 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNServerConstants.BUTTON_LOCATION_6.getLeft()),
				(int) (show.getHeight() * DYNServerConstants.BUTTON_LOCATION_6.getRight()), 30, 30,
				DYNServerConstants.WARP_IMAGE).setIsEnabled(true).addHoverText("Permission Checker")
						.doesDrawHoverText(true).setClickListener(but -> show.getStage().display(new PermissionGui())));

		show.registerComponent(but7 = (PictureButton) new PictureButton(
				(int) (show.getWidth() * DYNServerConstants.BUTTON_LOCATION_7.getLeft()),
				(int) (show.getHeight() * DYNServerConstants.BUTTON_LOCATION_7.getRight()), 30, 30,
				DYNServerConstants.PYTHON_IMAGE).setIsEnabled(true).addHoverText("Roster Script Options")
						.doesDrawHoverText(true).setClickListener(but -> {
						}));

		switch (pageNum) {
		case 1:
			but1.setIsEnabled(false);
			break;
		case 2:
			but2.setIsEnabled(false);
			break;
		case 3:
			but3.setIsEnabled(false);
			break;
		case 4:
			but4.setIsEnabled(false);
			break;
		case 5:
			but5.setIsEnabled(false);
			break;
		case 6:
			but6.setIsEnabled(false);
			break;
		case 7:
			but7.setIsEnabled(false);
			break;
		case 8:
			break;
		case 9:
			break;
		case 10:
			break;
		}

	}
}
