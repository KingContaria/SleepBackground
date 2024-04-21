package com.redlimerl.sleepbackground.mixin;

import com.redlimerl.sleepbackground.SleepBackground;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Framebuffer.class)
public class MixinFramebuffer {

    @Inject(method = {"beginWrite", "endWrite", "draw(II)V"}, at = @At("HEAD"), cancellable = true)
    private void cancelFrameBuffer(CallbackInfo ci) {
        if (SleepBackground.shouldNotRender) ci.cancel();
    }
}
