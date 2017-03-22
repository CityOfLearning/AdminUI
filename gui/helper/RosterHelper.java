package com.dyn.admin.gui.helper;

import java.util.Map;

import com.dyn.admin.AdminUI;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class RosterHelper {

	public static String getFormattedPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
		return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText()
				: ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(),
						networkPlayerInfoIn.getGameProfile().getName());
	}

	public static Map<String, String> getFormattedPlayerNames() {
		NetHandlerPlayClient nethandlerplayclient = Minecraft.getMinecraft().thePlayer.sendQueue;
		Map<String, String> formattedNames = Maps.newHashMap();
		for (NetworkPlayerInfo networkplayerinfo : nethandlerplayclient.getPlayerInfoMap()) {
			if ((networkplayerinfo != null) && (networkplayerinfo.getGameProfile() != null)) {
				if (AdminUI.adminSubRoster.contains(networkplayerinfo.getGameProfile().getName())) {
					formattedNames.put(networkplayerinfo.getGameProfile().getName(),
							getFormattedPlayerName(networkplayerinfo));
				}
			}
		}
		return formattedNames;
	}

}
