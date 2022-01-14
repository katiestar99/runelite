
package net.runelite.client.plugins.ticktracker;

import com.google.inject.Provides;
import java.util.Date;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Tick tracker",
	description = "Display tick timing variance in an overlay",
	tags = {"tick", "timers", "skill", "pvm", "lag"},
	enabledByDefault = false
)

public class TickTrackerPlugin extends Plugin
{
	//id "com.github.tatters654.shadow" version "6.1.0"

	private void sendChatMessage(String chatMessage)
	{
		final String message = new ChatMessageBuilder()
			.append(ChatColorType.HIGHLIGHT)
			.append(chatMessage)
			.build();

		chatMessageManager.queue(
			QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(message)
				.build());
	}

	@Inject
	private Client client;

	@Inject
	private TickTrackerPluginConfiguration config;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TickTrackerOverlay overlay;

	@Getter
	private long lastTickTime;
	@Getter
	private int tickTimePassed = 0;
	@Getter
	private int tickOverThresholdLow = 0;
	@Getter
	private int tickOverThresholdMedium = 0;
	@Getter
	private int tickOverThresholdHigh = 0;
	@Getter
	private int tickWithinRange = 0;
	@Getter
	private int allTickCounter = 0;
	@Getter
	private int currentTick = 600;
	@Getter
	private int runningTickAverage = 0;
	@Getter
	private double tickWithinRangePercent = 0;
	@Getter
	int disregardCounter = 0;

	@Provides
	TickTrackerPluginConfiguration provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TickTrackerPluginConfiguration.class);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (disregardCounter < 10)
		{
			disregardCounter += 1; //waiting 10 ticks, because ticks upon login or hopping are funky
		}
		else
		{
			long tickTime = new Date().getTime();
			int tickDiff = (int) (tickTime - lastTickTime);
			if (tickDiff > 2500)
			{
				tickDiff = 600;
				if (config.warnLargeTickDiff())
				{
					sendChatMessage("Tick set to 600ms because it was over 2500ms long, probably from login or hopping");
				}
			}
			currentTick = tickDiff;
			lastTickTime = new Date().getTime();

			allTickCounter += 1;
			tickTimePassed += tickDiff;
			runningTickAverage = tickTimePassed / allTickCounter;
			tickWithinRangePercent = (tickWithinRange * 1.0 / allTickCounter) * 100;

			if (tickDiff > config.getThresholdHigh())
			{
				tickOverThresholdHigh += 1;
			}
			else if (tickDiff > config.getThresholdMedium())
			{
				tickOverThresholdMedium += 1;
			}
			else if (tickDiff > config.getThresholdLow())
			{
				tickOverThresholdLow += 1;
			}
			else
			{
				tickWithinRange += 1;
			}
		}
	}
//test test
	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGGED_IN:
			case HOPPING:
				tickOverThresholdHigh = 0;
				tickOverThresholdMedium = 0;
				tickOverThresholdLow = 0;
				tickWithinRange = 0;
				runningTickAverage = 0;
				allTickCounter = 0;
		}
	}
}
