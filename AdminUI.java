package com.dyn.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dyn.admin.proxy.Proxy;
import com.dyn.admin.reference.MetaData;
import com.dyn.admin.reference.Reference;
import com.dyn.utils.BooleanListener;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:dyn|server")
public class AdminUI {

	public static List<String> adminSubRoster = new ArrayList<String>();
	public static List<String> groups = new ArrayList<String>();
	public static BooleanListener groupsMessageRecieved = new BooleanListener(false);
	public static Map<Integer, String> zones = new HashMap<Integer, String>();
	public static BooleanListener zonesMessageRecieved = new BooleanListener(false);
	public static List<String> permissions = new ArrayList<String>();
	public static BooleanListener permissionsMessageRecieved = new BooleanListener(false);

	@Mod.Instance(Reference.MOD_ID)
	public static AdminUI instance;

	@SidedProxy(modId = Reference.MOD_ID, clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {
		proxy.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MetaData.init(event.getModMetadata());
	}
}