package com.dyn.admin.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dyn.admin.AdminUI;
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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

public class GiveItem extends Show {

	private ScrollableDisplayList itemDisplayList;
	private ScrollableDisplayList rosterDisplayList;
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private TextBox userBox;
	private TextBox itemBox;
	private TextBox amountBox;

	public GiveItem() {
		setBackground(new DefaultBackground());
		title = "Admin Gui";
	}

	private void entryClicked(SelectStringEntry entry, DisplayList list, int mouseX, int mouseY) {
		if (list.getId() == "itms") {
			itemBox.setText(entry.getTitle());
		} else if (list.getId() == "roster") {
			userBox.setText(entry.getTitle());
		}

	}

	private void giveItemToPlayer() {
		if (userBox.getText().isEmpty() || itemBox.getText().isEmpty()) {
			return;
		}
		Item tItem = null;
		ItemStack itmSt = null;
		for (Item i : itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList<ItemStack>();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						if (is.getDisplayName().contentEquals(itemBox.getText())) {
							tItem = i;
							itmSt = is;
						}
					}
				} else {
					ItemStack is = new ItemStack(i);
					if (is.getDisplayName().contentEquals(itemBox.getText())) {
						tItem = i;
					}
				}
			}
		}
		if (tItem == null) {
			return;
		}
		String itemMod = "";
		if (itmSt != null) {
			itemMod = " " + itmSt.getItemDamage();
		}
		String amt = (amountBox.getText() == null) || (amountBox.getText().isEmpty()) ? "1" : amountBox.getText();
		Minecraft.getMinecraft().thePlayer.sendChatMessage(
				"/give " + userBox.getText().split("-")[0] + " " + tItem.getRegistryName() + " " + amt + " " + itemMod);
	}

	@Override
	public void setup() {
		super.setup();

		registerComponent(
				new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Give Items", TextAlignment.CENTER));

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
				new ResourceLocation("minecraft", "textures/items/cookie.png")).setIsEnabled(true)
						.addHoverText("See Students' Usernames and Passwords").doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new UsernamesAndPasswords())));

		registerComponent(new PictureButton((int) (width * .9), (int) (height * .35), 30, 30,
				new ResourceLocation("minecraft", "textures/items/emerald.png")).setIsEnabled(false)
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

		// get all the items in the registry
		FMLControlledNamespacedRegistry<Block> blockRegistry = GameData.getBlockRegistry();
		Iterator<?> iterator = blockRegistry.iterator();

		List<Item> blockList = new ArrayList<Item>();

		while (iterator.hasNext()) {
			Block blocks = (Block) iterator.next();
			blockList.add(Item.getItemFromBlock(blocks));
		}

		FMLControlledNamespacedRegistry<Item> itemRegistry = GameData.getItemRegistry();
		iterator = itemRegistry.iterator();

		List<Item> itemsList = new ArrayList<Item>();

		while (iterator.hasNext()) {
			Item items = (Item) iterator.next();
			itemsList.add(items);

		}

		for (Item i : blockList) {
			if (!itemList.contains(i)) {
				itemList.add(i);
			}
		}
		for (Item i : itemsList) {
			if (!itemList.contains(i)) {
				itemList.add(i);
			}
		}

		itemList.remove(null);

		ArrayList<ListEntry> dslist = new ArrayList<ListEntry>();

		for (Item i : itemList) {
			if (i != null) {
				if (i.getHasSubtypes()) {
					List<ItemStack> subItem = new ArrayList<ItemStack>();
					i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
					for (ItemStack is : subItem) {
						dslist.add(
								new SelectStringEntry(is.getDisplayName(), (SelectStringEntry entry, DisplayList dlist,
										int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
					}
				} else {
					ItemStack is = new ItemStack(i);
					dslist.add(new SelectStringEntry(is.getDisplayName(), (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		}

		registerComponent(new TextBox((int) (width * .2), (int) (height * .25), width / 4, 20, "Search for User")
				.setId("usersearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));
		registerComponent(new TextBox((int) (width * .55), (int) (height * .25), width / 4, 20, "Search for Item")
				.setId("itemsearch")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText)));

		itemDisplayList = new ScrollableDisplayList((int) (width * .5), (int) (height * .35), width / 3, 100, 15,
				dslist);
		itemDisplayList.setId("itms");

		registerComponent(itemDisplayList);

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : AdminUI.roster) {
			rlist.add(new SelectStringEntry(s, (SelectStringEntry entry, DisplayList dlist, int mouseX,
					int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
		}

		rlist.add(new SelectStringEntry(Minecraft.getMinecraft().thePlayer.getDisplayNameString(),
				(SelectStringEntry entry, DisplayList dlist, int mouseX, int mouseY) -> entryClicked(entry, dlist,
						mouseX, mouseY)));

		rosterDisplayList = new ScrollableDisplayList((int) (width * .15), (int) (height * .35), width / 3, 100, 15,
				rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		userBox = new TextBox((int) (width * .15), (int) (height * .8), width / 4, 20, "User").setId("user")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		registerComponent(userBox);

		amountBox = new TextBox((int) (width * .45) - 16, (int) (height * .8), 30, 20, "Amt").setId("amt")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		registerComponent(amountBox);

		itemBox = new TextBox((int) (width * .5), (int) (height * .8), width / 4, 20, "Item").setId("item")
				.setTextChangedListener((TextBox textbox, String previousText) -> textChanged(textbox, previousText));
		registerComponent(itemBox);

		registerComponent(new Button((int) (width * .8) - 10, (int) (height * .8), 30, 20, "Give")
				.setClickListener(but -> giveItemToPlayer()));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				new ResourceLocation("dyn", "textures/gui/background.png")));
	}

	private void textChanged(TextBox textbox, String previousText) {
		if (textbox.getId() == "itemsearch") {
			itemDisplayList.clear();
			for (Item i : itemList) {
				if (i != null) {
					if (i.getHasSubtypes()) {
						List<ItemStack> subItem = new ArrayList<ItemStack>();
						i.getSubItems(i, CreativeTabs.tabAllSearch, subItem);
						for (ItemStack is : subItem) {
							if (is.getDisplayName().toLowerCase().contains(textbox.getText().toLowerCase())) {
								itemDisplayList.add(new SelectStringEntry(is.getDisplayName(),
										(SelectStringEntry entry, DisplayList dlist, int mouseX,
												int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
							}
						}
					} else {
						ItemStack is = new ItemStack(i);
						if (is.getDisplayName().toLowerCase().contains(textbox.getText().toLowerCase())) {
							itemDisplayList.add(new SelectStringEntry(is.getDisplayName(),
									(SelectStringEntry entry, DisplayList dlist, int mouseX,
											int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
						}
					}
				}
			}
		} else if (textbox.getId() == "usersearch") {
			rosterDisplayList.clear();
			for (String student : AdminUI.roster) {
				if (student.toLowerCase().contains(textbox.getText().toLowerCase())) {
					rosterDisplayList.add(new SelectStringEntry(student, (SelectStringEntry entry, DisplayList dlist,
							int mouseX, int mouseY) -> entryClicked(entry, dlist, mouseX, mouseY)));
				}
			}
		} else if (textbox.getId() == "amt") {
			try {
				int num = Integer.parseInt(textbox.getText());
				int cnum = Math.max(Math.min(num, 64), 0);
				if (num != cnum) {
					textbox.setText("" + cnum);
				}
			} catch (NumberFormatException e) {
				textbox.setText("");
			}
		}
	}
}
