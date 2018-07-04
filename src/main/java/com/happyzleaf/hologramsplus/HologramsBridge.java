package com.happyzleaf.hologramsplus;

import com.happyzleaf.hologramsplus.config.HologramsData;
import com.happyzleaf.hologramsplus.config.SerializableHologram;
import de.randombyte.holograms.api.HologramsService;
import de.randombyte.holograms.shaded.kotlin.Pair;
import me.rojo8399.placeholderapi.PlaceholderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author happyzleaf
 * @since 04/07/2018
 */
public class HologramsBridge {
	private static HologramsService service;
	public static PlaceholderService papi;
	
	public static void load() {
		service = Sponge.getServiceManager().provideUnchecked(HologramsService.class);
		papi = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);
		
		Map<String, Boolean> selectChoices = new HashMap<>();
		selectChoices.put("enable", true);
		selectChoices.put("disable", false);
		CommandSpec select = CommandSpec.builder()
				.permission("hologramsplus.command.select")
				.arguments(GenericArguments.onlyOne(GenericArguments.choices(Text.of("choice"), selectChoices)))
				.executor((src, args) -> {
					if (!(src instanceof Player)) {
						throw new CommandException(Text.of(TextColors.RED, "[HologramsPlus] You must be a player to run this command."));
					}
					
					Player player = (Player) src;
					boolean enable = (boolean) args.getOne("choice").get();
					String action = enable ? "enable" : "disable";
					
					List<HologramsService.Hologram> nearHolograms = service.getHolograms(player.getLocation(), 10).stream().map(Pair::component1).collect(Collectors.toList());
					if (nearHolograms.size() == 0) {
						throw new CommandException(Text.of(TextColors.RED, "[HologramsPlus] There are no holograms in the range of 10 blocks around you."));
					}
					
					PaginationList.builder().title(Text.of("Choose the hologram to " + action))
							.contents(nearHolograms.stream().map(h ->
									Text.builder("Hologram: ").color(TextColors.DARK_GREEN)
											.append(TextSerializers.FORMATTING_CODE.deserialize("&f" + TextSerializers.FORMATTING_CODE.serialize(h.getText()).replaceAll("\\r?\\n", "&0\u2937&f")).toBuilder()
													.onHover(TextActions.showText(Text.of(TextColors.GREEN, "Click to " + action + " this hologram.")))
													.onClick(TextActions.executeCallback(source -> {
														SerializableHologram choice = HologramsData.getHologram(h.getWorldUuid(), h.getUuid()).orElse(null);
														if (enable) {
															if (choice == null) {
																HologramsData.addHologram(h.getWorldUuid(), choice = new SerializableHologram(h.getUuid(), h.getText()));
																h.setText(choice.getUpdatedText());
																HologramsData.saveData();
																source.sendMessage(Text.of(TextColors.GREEN, "[HologramsPlus] Successfully activated this hologram."));
															} else {
																source.sendMessage(Text.of(TextColors.RED, "[HologramsPlus] This hologram is already active."));
															}
														} else {
															if (choice == null) {
																source.sendMessage(Text.of(TextColors.RED, "[HologramsPlus] This hologram is not active."));
															} else {
																HologramsData.removeHologram(h.getWorldUuid(), h.getUuid());
																h.setText(choice.getOriginalText());
																HologramsData.saveData();
																source.sendMessage(Text.of(TextColors.GREEN, "[HologramsPlus] Successfully deactivated this hologram."));
															}
														}
													}))
													.build())
											.build())
									.collect(Collectors.toList()))
							.sendTo(src);
					
					return CommandResult.success();
				})
				.build();
		CommandSpec refresh = CommandSpec.builder()
				.permission("hologramsplus.command.refresh")
				.description(Text.of("Forces to refresh the holograms."))
				.executor((src, args) -> {
					Sponge.getScheduler().getTasksByName("hologramsrefresh").forEach(task -> task.getConsumer().accept(task));
					src.sendMessage(Text.of(TextColors.GREEN, "[HologramsPlus] Successfully refreshed the holograms."));
					return CommandResult.success();
				})
				.build();
		CommandSpec main = CommandSpec.builder()
				.child(select, "select")
				.child(refresh, "refresh")
				.build();
		Sponge.getCommandManager().register(HologramsPlus.instance, main, "hologramsplus");
		
		Task.builder().name("hologramsrefresh").interval(HologramsData.updateRate, TimeUnit.SECONDS)
				.execute((task) -> {
					for (HologramsService.Hologram rbh : Sponge.getServer().getWorlds().stream().flatMap(world -> service.getHolograms(world).stream()).collect(Collectors.toList())) {
						if (rbh.exists()) {
							HologramsData.getHologram(rbh.getWorldUuid(), rbh.getUuid()).ifPresent(h -> {
								rbh.setText(h.getUpdatedText());
							});
						}
					}
				})
				.submit(HologramsPlus.instance);
	}
}
