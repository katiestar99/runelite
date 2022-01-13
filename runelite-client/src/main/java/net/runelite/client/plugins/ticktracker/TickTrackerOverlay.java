package net.runelite.client.plugins.ticktracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class TickTrackerOverlay extends OverlayPanel
{

	private final Client client;
	private final TickTrackerPlugin plugin;
	private final TickTrackerPluginConfiguration config;

	private static final int Y_OFFSET = 1;
	private static final int X_OFFSET = 1;

	@Inject
	private TickTrackerOverlay(Client client, TickTrackerPlugin plugin, TickTrackerPluginConfiguration config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGH);
		this.plugin = plugin;
		this.client = client;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.drawLargeOverlay())
		{
			drawExtraInformation(graphics);
		}
		if (config.drawSmallOverlay())
		{
			drawSmallOverlay(graphics);
		}
		return null;
	}

	private Color colorSelection()
	{
		double ticksWithinRange = (plugin.getTickWithinRange() * 1.0 / plugin.getAllTickCounter()) * 100;
		Color colorSelection;
		if (ticksWithinRange > config.warningColorThreshold())
		{
			colorSelection = Color.GREEN;
		}
		else if (ticksWithinRange > config.warningColorThreshold() - 2)
		{
			colorSelection = Color.YELLOW;
		}
		else
		{
			colorSelection = Color.RED;
		}
		return colorSelection;
	}

	private void drawExtraInformation(Graphics2D graphics)
	{
		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Ticks")
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.right(plugin.getTickOverThresholdHigh() + " (" + String.format("%.2f", (plugin.getTickOverThresholdHigh() * 1.0 / plugin.getAllTickCounter()) * 100) + " %)")
			.left(">" + config.getThresholdHigh())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.right(plugin.getTickOverThresholdMedium() + " (" + String.format("%.2f", (plugin.getTickOverThresholdMedium() * 1.0 / plugin.getAllTickCounter()) * 100) + " %)")
			.left(">" + config.getThresholdMedium())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.right((plugin.getTickOverThresholdLow()) + " (" + String.format("%.2f", (plugin.getTickOverThresholdLow() * 1.0 / plugin.getAllTickCounter()) * 100) + " %)")
			.left(">" + config.getThresholdLow())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.right(plugin.getTickWithinRange() + " (" + String.format("%.2f", (plugin.getTickWithinRange() * 1.0 / plugin.getAllTickCounter()) * 100) + " %)")
			.left("Good")
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.right(String.valueOf(plugin.getAllTickCounter()))
			.left("Total")
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.right(String.valueOf(plugin.getRunningTickAverage()))
			.left("Tick Average ms")
			.build());
		panelComponent.setPreferredSize(new Dimension(
			graphics.getFontMetrics().stringWidth(String.valueOf(plugin.getRunningTickAverage())),
			0));
		super.render(graphics);
	}

	private void drawSmallOverlay(Graphics2D graphics)
	{
		if (config.drawSmallOverlay())
		{
			Widget logoutButton = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_LOGOUT_BUTTON);
			int xOffset = X_OFFSET;
			if (logoutButton != null && !logoutButton.isHidden())
			{
				xOffset += logoutButton.getWidth();
			}
			double ticksWithinRangePercent = (plugin.getTickWithinRange() * 1.0 / plugin.getAllTickCounter()) * 100;
			final String text = String.format("%.2f",ticksWithinRangePercent) + "%";
			final int textWidth = graphics.getFontMetrics().stringWidth(text);
			final int textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();

			final int width = (int) client.getRealDimensions().getWidth();
			final Point point = new Point(width - textWidth - xOffset, textHeight + Y_OFFSET);
			OverlayUtil.renderTextLocation(graphics, point, text, colorSelection());
		}
	}
}
