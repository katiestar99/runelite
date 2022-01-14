
package net.runelite.client.plugins.ticktracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("TickTracker")
public interface TickTrackerPluginConfiguration extends Config
{
	@ConfigItem(
		keyName = "thresholdHigh",
		name = "Threshold High",
		description = "Threshold for high tick latency",
		position = 2
	)
	default int getThresholdHigh()
	{
		return 750;
	}

	@ConfigItem(
		keyName = "thresholdMedium",
		name = "Threshold Medium",
		description = "Threshold for medium tick latency",
		position = 3
	)
	default int getThresholdMedium()
	{
		return 700;
	}

	@ConfigItem(
		keyName = "thresholdLow",
		name = "Threshold Low",
		description = "Threshold for low tick latency",
		position = 4
	)
	default int getThresholdLow()
	{
		return 650;
	}

	@ConfigItem(
		keyName = "drawLargeOverlay",
		name = "Show extra information",
		description = "Show set thresholds and each category's quantity of ticks and percentage of total ticks",
		position = 1
	)
	default boolean drawLargeOverlay()
	{
		return false;
	}

	@ConfigItem(
		keyName = "drawSmallOverlay",
		name = "Draw Small overlay",
		description = "Whether to draw a small overlay",
		position = 5
	)
	default boolean drawSmallOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "warningText",
		name = "Small overlay text color threshold",
		description = "Above threshold = Green, Above Threshold -2 = Yellow, Below that = Red",
		position = 6
	)
	default int warningColorThreshold()
	{
		return 90;
	}

	@ConfigItem(
		keyName = "warnLargeTickDiff",
		name = "Warn in chat about large tick lags",
		description = "Print notification in chat of ticks over 2500ms",
		position = 7
	)
	default boolean warnLargeTickDiff()
	{
		return false;
	}
}
