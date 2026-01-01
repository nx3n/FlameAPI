package nx3n.flameapi.engine;

import net.minecraft.client.Minecraft;
import nx3n.flameapi.visual.Api;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Optional mixin hooks.
 *
 * Why this exists:
 * - Forge events are enough for most visuals.
 * - If you want the visuals engine to run even when events are blocked/modified,
 *   you can enable mixins and keep the engine ticking from Minecraft#tick.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void flame$tickTail(CallbackInfo ci) {
        // Make sure visuals are initialized.
        Api.bootstrap();
        // Api already subscribes to Forge tick; this is a safe fallback.
        // If you want to rely only on mixins, you can disable Forge subscriber later.
    }
}
