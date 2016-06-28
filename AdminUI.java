package com.dyn.admin;

import java.util.ArrayList;
import java.util.List;

import com.dyn.DYNServerMod;
import com.dyn.admin.proxy.Proxy;
import com.dyn.admin.reference.MetaData;
import com.dyn.admin.reference.Reference;
import com.dyn.utils.PlayerLevel;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class AdminUI {
	
	public static List<String> adminSubRoster = new ArrayList<String>();
	
	@Mod.Instance(Reference.MOD_ID)
	public static AdminUI instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.Metadata(Reference.MOD_ID)
	public ModMetadata metadata;

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {
		if (DYNServerMod.status == PlayerLevel.ADMIN) {
			proxy.init();
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		metadata = MetaData.init(metadata);
	}
}