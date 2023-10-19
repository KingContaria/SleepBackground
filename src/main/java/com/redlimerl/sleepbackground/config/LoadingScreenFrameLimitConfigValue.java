package com.redlimerl.sleepbackground.config;

import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.annotations.Config;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class LoadingScreenFrameLimitConfigValue extends ConfigValue {

    @Config.Name("sleepbackground.config.fpsLimit")
    @Config.Numbers.Whole.Bounds(min = 15, max = 60, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    private int frameLimit;

    public LoadingScreenFrameLimitConfigValue(int defaultFrameLimit) {
        this.frameLimit = defaultFrameLimit;
    }

    @Nullable
    public Integer getFrameLimit() {
        return this.isEnabled() ? this.frameLimit : null;
    }
}
