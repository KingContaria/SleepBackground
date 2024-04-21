package com.redlimerl.sleepbackground;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.concurrent.locks.LockSupport;

public class SleepBackground {

    private static final File LOCK_FILE = new File(FileUtils.getUserDirectory(), "sleepbg.lock");
    public static SleepBackgroundConfig config;

    public static int clientWorldTickCount;
    public static boolean shouldNotRender;
    private static long lastRenderTime;
    public static boolean lockExists;
    private static int lockTick = 0;

    public static boolean shouldRenderInBackground() {
        long currentTime = Util.getMeasuringTimeMs();
        long timeSinceLastRender = currentTime - lastRenderTime;

        Integer targetFPS = getBackgroundFPS();
        if (targetFPS == null) {
            return true;
        }

        long frameTime = 1000 / targetFPS;
        if (timeSinceLastRender < frameTime) {
            idle(frameTime);
            return false;
        }

        lastRenderTime = currentTime;
        return true;
    }

    /**
     * For decrease CPU usage
     * From mangohand's idle method
     */
    private static void idle(long waitMillis) {
        LockSupport.parkNanos("waiting to render", Math.min(waitMillis, 30L) * 1000000L);
    }

    @Nullable
    private static Integer getBackgroundFPS() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isWindowFocused() || isHoveredWindow()) {
            return null;
        }
        if (client.world == null) {
            if (client.currentScreen instanceof LevelLoadingScreen) {
                return SleepBackground.config.LOADING_SCREEN_FRAME_RATE.getFrameLimit();
            }
            return null;
        }
        if (SleepBackground.lockExists) {
            Integer value = SleepBackground.config.LOCKED_INSTANCE_FRAME_RATE.getFrameLimit();
            if (value != null) {
                return value;
            }
        }
        if (SleepBackground.config.WORLD_SETUP_FRAME_RATE.getMaxTicks() > clientWorldTickCount) {
            Integer value = SleepBackground.config.WORLD_SETUP_FRAME_RATE.getFrameLimit();
            if (value != null) {
                return value;
            }
        }
        return SleepBackground.config.BACKGROUND_FRAME_RATE.getFrameLimit();
    }

    private static boolean isHoveredWindow() {
        return GLFW.glfwGetWindowAttrib(MinecraftClient.getInstance().getWindow().getHandle(), 131083) != 0;
    }

    public static void tick() {
        SleepBackground.clientWorldTickCount = MinecraftClient.getInstance().world == null ? 0 : Math.min(SleepBackground.clientWorldTickCount + 1, SleepBackground.config.WORLD_SETUP_FRAME_RATE.getMaxTicks());

        if (SleepBackground.config.LOCKED_INSTANCE_FRAME_RATE.isEnabled()) {
            if (++lockTick >= SleepBackground.config.LOCKED_INSTANCE_FRAME_RATE.getTickInterval()) {
                SleepBackground.lockExists = LOCK_FILE.exists();
                lockTick = 0;
            }
        } else {
            SleepBackground.lockExists = false;
        }
    }
}
