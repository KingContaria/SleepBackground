package com.redlimerl.sleepbackground.config;

import org.mcsr.speedrunapi.config.api.annotations.Config;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class WorldPreviewRenderIntervalConfigValue extends ConfigValue {

    @Config.Numbers.Whole.Bounds(min = 1, max = 100, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    private int renderInterval = 5;

    public WorldPreviewRenderIntervalConfigValue(int defaultRenderInterval) {
        this.renderInterval = defaultRenderInterval;
    }

    public int getRenderInterval() {
        return this.isEnabled() ? this.renderInterval : 1;
    }
}
