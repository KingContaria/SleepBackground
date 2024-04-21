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

    public static SleepBackgroundConfig config;

    public static int CLIENT_WORLD_TICK_COUNT = 0;
    public static boolean LATEST_LOCK_FRAME = false;
    public static boolean LOCK_FILE_EXIST = false;
    private static final File LOCK_FILE = new File(FileUtils.getUserDirectory(), "sleepbg.lock");
    private static long lastRenderTime;

    public static boolean shouldRenderInBackground() {
        long currentTime = Util.getMeasuringTimeMs();
        long timeSinceLastRender = currentTime - lastRenderTime;

        Integer targetFPS = getBackgroundFPS();
        if (targetFPS == null) return true;

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
        waitMillis = Math.min(waitMillis, 30L);
        LockSupport.parkNanos("waiting to render", waitMillis * 1000000L);
    }

    @Nullable
    private static Integer getBackgroundFPS() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!client.isWindowFocused() && !isHoveredWindow()) {
            if (client.world != null) {
                if (SleepBackground.LOCK_FILE_EXIST) {
                    Integer value = SleepBackground.config.LOCKED_INSTANCE_FRAME_RATE.getFrameLimit();
                    if (value != null) return value;
                }

                if (SleepBackground.config.WORLD_SETUP_FRAME_RATE.getMaxTicks() > CLIENT_WORLD_TICK_COUNT) {
                    Integer value = SleepBackground.config.WORLD_SETUP_FRAME_RATE.getFrameLimit();
                    if (value != null) return value;
                }

                return SleepBackground.config.BACKGROUND_FRAME_RATE.getFrameLimit();
            } else if (client.currentScreen instanceof LevelLoadingScreen) {
                return SleepBackground.config.LOADING_SCREEN_FRAME_RATE.getFrameLimit();
            }
        }
        return null;
    }

    private static boolean isHoveredWindow() {
        return GLFW.glfwGetWindowAttrib(MinecraftClient.getInstance().getWindow().getHandle(), 131083) != 0;
    }

    private static int lockTick = 0;
    public static void checkLock() {
        if (SleepBackground.config.LOCKED_INSTANCE_FRAME_RATE.isEnabled()) {
            if (++lockTick >= SleepBackground.config.LOCKED_INSTANCE_FRAME_RATE.getTickInterval()) {
                SleepBackground.LOCK_FILE_EXIST = LOCK_FILE.exists();
                lockTick = 0;
            }
        } else {
            SleepBackground.LOCK_FILE_EXIST = false;
        }
    }
}
