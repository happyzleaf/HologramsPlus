package com.happyzleaf.hologramsplus.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.happyzleaf.hologramsplus.HologramsBridge.papi;

/**
 * @author happyzleaf
 * @since 04/07/2018
 */
@ConfigSerializable
public class SerializableHologram {
	@Setting
	public UUID uuid;
	
	@Setting
	public List<String> lines;
	
	public Text getOriginalText() {
		Text.Builder builder = Text.builder();
		lines.forEach(l -> builder.append(TextSerializers.FORMATTING_CODE.deserialize(l)));
		return builder.build();
	}
	
	public Text getUpdatedText() {
		Text.Builder builder = Text.builder();
		lines.forEach(l -> builder.append(papi.replacePlaceholders(l, null, null)));
		return builder.build();
	}
	
	public SerializableHologram() {}
	
	public SerializableHologram(UUID uuid, Text text) {
		this.uuid = uuid;
		this.lines = Arrays.asList(TextSerializers.FORMATTING_CODE.serialize(text).split("\\r?\\n"));
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SerializableHologram && uuid.equals(((SerializableHologram) obj).uuid);
	}
}
