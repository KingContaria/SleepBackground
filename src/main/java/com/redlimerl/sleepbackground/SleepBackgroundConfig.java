package com.redlimerl.sleepbackground;

import com.redlimerl.sleepbackground.config.*;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.api.annotations.InitializeOn;

@InitializeOn(InitializeOn.InitPoint.PRELAUNCH)
public class SleepBackgroundConfig implements SpeedrunConfig {

    @Config.Category("backgroundFrameRate")
    public final FrameLimitConfigValue BACKGROUND_FRAME_RATE = new FrameLimitConfigValue(1);

    @Config.Category("loadingScreenFrameRate")
    public final LoadingScreenFrameLimitConfigValue LOADING_SCREEN_FRAME_RATE = new LoadingScreenFrameLimitConfigValue(30);

    @Config.Category("worldSetupFrameRate")
    public final WorldSetupConfigValue WORLD_SETUP_FRAME_RATE = new WorldSetupConfigValue(10, 30);

    @Config.Category("lockedFrameRate")
    public final LockedInstanceConfigValue LOCKED_INSTANCE_FRAME_RATE = new LockedInstanceConfigValue(1, 20, 10);

    @Config.Category("loadingScreenTickInterval")
    public final LoadingScreenTickConfigValue LOADING_SCREEN_TICK_INTERVAL = new LoadingScreenTickConfigValue(1);

    @Config.Category("worldPreviewRenderInterval")
    public final WorldPreviewRenderIntervalConfigValue WORLD_PREVIEW_RENDER_INTERVAL = new WorldPreviewRenderIntervalConfigValue(5);

    @Config.Category("logInterval")
    public final LogIntervalConfigValue LOG_INTERVAL = new LogIntervalConfigValue(500);

    {
        SleepBackground.config = this;
    }

    @Override
    public String modID() {
        return "sleepbackground";
    }
}
