package com.redlimerl.sleepbackground.config;

import org.mcsr.speedrunapi.config.api.annotations.Config;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class LockedInstanceConfigValue extends FrameLimitConfigValue {

    @Config.Numbers.Whole.Bounds(min = 1, max = 100, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    private int tickInterval;

    @Config.Category("lockedWorldPreviewRenderInterval")
    private final WorldPreviewRenderIntervalConfigValue locked_worldPreviewRenderInterval;

    public LockedInstanceConfigValue(int defaultFrameLimit, int defaultTickInterval, int defaultWorldPreviewRenderInterval) {
        super(defaultFrameLimit);
        this.tickInterval = defaultTickInterval;
        this.locked_worldPreviewRenderInterval = new WorldPreviewRenderIntervalConfigValue(defaultWorldPreviewRenderInterval);
    }

    public int getTickInterval() {
        return this.isEnabled() ? this.tickInterval : 1;
    }

    public int getWorldPreviewRenderInterval() {
        return this.locked_worldPreviewRenderInterval.getRenderInterval();
    }
}
