package win.baruna.blockmeter.mixin;

import net.minecraft.client.ClientGameSession;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.baruna.blockmeter.BlockMeter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ClientGameSession.class)
public class GameMixin {
    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onSessionStarted(ClientWorld clientWorld, ClientPlayerEntity clientPlayerEntity, ClientPlayNetworkHandler clientPlayNetworkHandler, CallbackInfo ci) {
        BlockMeter.instance.clear();
    }
}
