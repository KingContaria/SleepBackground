package com.redlimerl.sleepbackground.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.redlimerl.sleepbackground.SleepBackground;
import net.minecraft.server.WorldGenerationProgressLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldGenerationProgressLogger.class)
public class MixinWorldGenerationProgressLogger {

    /**
     * @author DuncanRuns, jojoe77777
     */
    @ModifyExpressionValue(method = "setChunkStatus", at = @At(value = "CONSTANT", args = "longValue=500"))
    private long modifyNextMessageTime(long increment) {
        Integer logInterval = SleepBackground.config.LOG_INTERVAL.getLogInterval();
        return logInterval != null ? logInterval : increment;
    }
}
