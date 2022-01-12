package net.runelite.client.plugins.ticktracker;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.LineComponent;

public class TickTrackerOverlay extends OverlayPanel {

    private final Client client;
    private final TickTrackerPlugin plugin;
    private final TickTrackerPluginConfiguration config;

    @Inject
    private TickTrackerOverlay(Client client, TickTrackerPlugin plugin, TickTrackerPluginConfiguration config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_CENTER);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        panelComponent.setOrientation(ComponentOrientation.VERTICAL);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
       double ticksWithinRange = (plugin.getTickWithinRange() * 1.0 / plugin.getAllTickCounter()) * 100;

       Color colorSelection;
       if (ticksWithinRange > config.warningColorThreshold()) {
           colorSelection = Color.GREEN;
       }
       else if (ticksWithinRange > config.warningColorThreshold() - 2) {
           colorSelection = Color.YELLOW;
       }
       else colorSelection = Color.RED;

        if (config.drawLargeOverlay()) {
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
            }
            if (config.drawSmallOverlay()) {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(String.format("%.1f", ticksWithinRange))
                        .color(colorSelection)
                        .build());
                panelComponent.setPreferredSize(new Dimension(
                        graphics.getFontMetrics().stringWidth((String.valueOf(20))),
                        0));
            }
        return super.render(graphics);
    }
}
