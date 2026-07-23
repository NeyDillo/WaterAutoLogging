package com.NeyDillo.WaterAutoLogging.mixin;
import com.NeyDillo.WaterAutoLogging.MessageProcessor;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "addMessage", at = @At("HEAD"), cancellable = false)
    private void onAddMessage(Text message, CallbackInfo ci) {
        String raw = message.getString();
        if (raw != null && !raw.isEmpty()) {
            MessageProcessor.processMessage(raw);
        }
    }
}