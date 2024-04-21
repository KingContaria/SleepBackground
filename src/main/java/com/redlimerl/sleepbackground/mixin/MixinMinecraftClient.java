package com.redlimerl.sleepbackground.mixin;

import com.redlimerl.sleepbackground.SleepBackground;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow @Nullable public ClientWorld world;

    @Inject(method = "render", at = @At("HEAD"))
    private void updateLatestLockFrame(CallbackInfo ci) {
        SleepBackground.LATEST_LOCK_FRAME = !SleepBackground.shouldRenderInBackground();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void updateClientWorldTickCount(CallbackInfo ci) {
        SleepBackground.CLIENT_WORLD_TICK_COUNT = this.world == null ? 0 :
                Math.min(SleepBackground.CLIENT_WORLD_TICK_COUNT + 1, SleepBackground.config.WORLD_SETUP_FRAME_RATE.getMaxTicks());

        SleepBackground.checkLock();
    }


    @Inject(method = "drawProfilerResults", at = @At("HEAD"), cancellable = true, expect = 0, require = 0)
    private void cancelDrawProfilerResults(CallbackInfo ci) {
        if (SleepBackground.LATEST_LOCK_FRAME) ci.cancel();
    }

    @ModifyArg(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/RegistryTracker$Modifiable;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V"))
    private long modifyLoadingScreenTickInterval(long delay) {
        Integer tickInterval = SleepBackground.config.LOADING_SCREEN_TICK_INTERVAL.getTickInterval();
        return tickInterval != null ? tickInterval : delay;
    }
}
