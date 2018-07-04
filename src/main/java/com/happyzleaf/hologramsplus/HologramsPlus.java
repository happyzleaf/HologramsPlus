package com.happyzleaf.hologramsplus;

import com.google.inject.Inject;
import com.happyzleaf.hologramsplus.config.HologramsData;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

@Plugin(id = "hologramsplus", name = "HologramsPlus", version = "1.0.0", authors = {"happyzleaf"}, url = "https://www.happyzleaf.com/",
		description = "Adds support for PlaceholderAPI to RandomByte's holograms.",
		dependencies = {@Dependency(id = "holograms", version = "3.1.5"), @Dependency(id = "placeholderapi", version = "4.4")})
public class HologramsPlus {
	public static final Logger LOGGER = LoggerFactory.getLogger("HologramsPlus");
	
	public static HologramsPlus instance;
	private static boolean loaded = false;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private File configFile;
	
	@Listener
	public void preInit(GamePreInitializationEvent event) {
		if (!Sponge.getPluginManager().isLoaded("holograms") || !Sponge.getPluginManager().isLoaded("placeholderapi")) {
			LOGGER.error("Looks like you're missing holograms or placeholderapi. The mod cannot continue.");
			return;
		}
		
		instance = this;
		loaded = true;
		
		HologramsData.init(configFile, configLoader);
	}
	
	@Listener
	public void init(GameInitializationEvent event) {
		if (loaded) {
			HologramsBridge.load();
		}
		LOGGER.info("Loaded! Made with <3 by happyzleaf. (https://happyzleaf.com)");
	}
	
	@Listener
	public void onReload(GameReloadEvent event) {
		if (loaded) {
			HologramsData.loadData();
		}
	}
}
