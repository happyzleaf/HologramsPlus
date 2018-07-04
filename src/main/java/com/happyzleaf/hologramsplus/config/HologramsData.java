package com.happyzleaf.hologramsplus.config;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author happyzleaf
 * @since 04/07/2018
 */
public class HologramsData {
	private static File file;
	private static ConfigurationLoader<CommentedConfigurationNode> loader;
	private static CommentedConfigurationNode node;
	
	public static int updateRate = 60; //in seconds
	private static ListMultimap<UUID, SerializableHologram> worldHolograms = MultimapBuilder.treeKeys().arrayListValues(1).build(); //World to holograms map
	
	public static void init(File configFile, ConfigurationLoader<CommentedConfigurationNode> configLoader) {
		file = configFile;
		loader = configLoader;
		
		if (!file.exists()) {
			saveData();
		}
		
		loadData();
	}
	
	public static void loadData() {
		load();
		
		try {
			updateRate = node.getNode("general", "updateRate").getInt();
			
			worldHolograms.clear();
			for (Map.Entry<Object, ? extends CommentedConfigurationNode> e : node.getNode("holograms").getChildrenMap().entrySet()) {
				worldHolograms.putAll(UUID.fromString((String) e.getKey()), e.getValue().getList(TypeToken.of(SerializableHologram.class)));
			}
		} catch (ObjectMappingException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void saveData() {
		load();
		
		try {
			node.getNode("general", "updateRate").setComment("How often the holograms (and their placeholders) should be refreshed, in seconds.").setValue(updateRate);
			
			ConfigurationNode holograms = node.getNode("holograms");
			for (UUID world : worldHolograms.keys()) {
				holograms.getNode(world.toString()).setValue(new TypeToken<List<SerializableHologram>>() {
				}, worldHolograms.get(world));
			}
		} catch (ObjectMappingException e1) {
			e1.printStackTrace();
		}
		
		save();
	}
	
	private static void load() {
		try {
			node = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void save() {
		try {
			loader.save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Optional<SerializableHologram> getHologram(UUID world, UUID hologram) {
		return worldHolograms.get(world).stream().filter(h -> h.uuid.equals(hologram)).findFirst();
	}
	
	public static void addHologram(UUID world, SerializableHologram hologram) {
		worldHolograms.put(world, hologram);
	}
	
	public static void removeHologram(UUID world, UUID hologram) {
		worldHolograms.remove(world, worldHolograms.get(world).stream().filter(h -> h.uuid.equals(hologram)).findFirst().get());
	}
}
