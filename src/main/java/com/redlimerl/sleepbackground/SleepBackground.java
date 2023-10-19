package com.redlimerl.sleepbackground;

import me.voidxwalker.worldpreview.WorldPreview;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.util.Window;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.concurrent.locks.LockSupport;

public class SleepBackground implements ClientModInitializer {

    public static int CLIENT_WORLD_TICK_COUNT = 0;
    private static boolean HAS_WORLD_PREVIEW = false;
    private static boolean CHECK_FREEZE_PREVIEW = false;
    private static boolean LOCK_FREEZE_PREVIEW = false;
    public static boolean LATEST_LOCK_FRAME = false;
    public static boolean LOCK_FILE_EXIST = false;
    private static int LOADING_SCREEN_RENDER_COUNT = 0;
    private static final File LOCK_FILE = new File(FileUtils.getUserDirectory(), "sleepbg.lock");

    @Override
    public void onInitializeClient() {
        HAS_WORLD_PREVIEW = FabricLoader.getInstance().isModLoaded("worldpreview");
    }

    private static long lastRenderTime = 0;
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
                    Integer value = SleepBackgroundConfig.INSTANCE.LOCKED_INSTANCE_FRAME_RATE.getFrameLimit();
                    if (value != null) return value;
                }

                if (SleepBackgroundConfig.INSTANCE.WORLD_SETUP_FRAME_RATE.getMaxTicks() > CLIENT_WORLD_TICK_COUNT) {
                    Integer value = SleepBackgroundConfig.INSTANCE.WORLD_SETUP_FRAME_RATE.getFrameLimit();
                    if (value != null) return value;
                }

                return SleepBackgroundConfig.INSTANCE.BACKGROUND_FRAME_RATE.getFrameLimit();
            } else if (client.currentScreen instanceof LevelLoadingScreen) {
                return SleepBackgroundConfig.INSTANCE.LOADING_SCREEN_FRAME_RATE.getFrameLimit();
            }
        }
        return null;
    }

    private static boolean isHoveredWindow() {
        Window window = MinecraftClient.getInstance().getWindow();
        return GLFW.glfwGetWindowAttrib(window.getHandle(), 131083) != 0;
    }

    private static long checkTickRate = 0;
    public static void checkRenderWorldPreview() {
        if (!HAS_WORLD_PREVIEW || !WorldPreview.inPreview) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime > checkTickRate + 50) {
            checkTickRate = currentTime;
            checkLock();
        }
        boolean windowFocused = MinecraftClient.getInstance().isWindowFocused(), windowHovered = isHoveredWindow();
        int renderTimes = SleepBackground.LOCK_FILE_EXIST ? SleepBackgroundConfig.INSTANCE.LOCKED_INSTANCE_FRAME_RATE.getWorldPreviewRenderInterval() : SleepBackgroundConfig.INSTANCE.WORLD_PREVIEW_RENDER_INTERVAL.getRenderInterval();
        if (windowFocused || windowHovered
                || ++LOADING_SCREEN_RENDER_COUNT >= renderTimes) {
            LOADING_SCREEN_RENDER_COUNT = 0;
            if (windowFocused || windowHovered) {
                if (CHECK_FREEZE_PREVIEW) {
                    LOCK_FREEZE_PREVIEW = WorldPreview.freezePreview;
                }
                CHECK_FREEZE_PREVIEW = true;
            }
            WorldPreview.freezePreview = LOCK_FREEZE_PREVIEW;
        } else {
            CHECK_FREEZE_PREVIEW = false;
            WorldPreview.freezePreview = true;
        }
    }

    private static int lockTick = 0;
    public static void checkLock() {
        if (SleepBackgroundConfig.INSTANCE.LOCKED_INSTANCE_FRAME_RATE.isEnabled()) {
            if (++lockTick >= SleepBackgroundConfig.INSTANCE.LOCKED_INSTANCE_FRAME_RATE.getTickInterval()) {
                SleepBackground.LOCK_FILE_EXIST = LOCK_FILE.exists();
                lockTick = 0;
            }
        } else {
            SleepBackground.LOCK_FILE_EXIST = false;
        }
    }
}
