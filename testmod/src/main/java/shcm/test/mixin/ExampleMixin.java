package shcm.test.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.test.Nah;
import shcm.test.TestShit;
import shcm.test.Yes;

@Mixin(
        value = {MinecraftServer.class, MinecraftServer.ServerResourcePackProperties.class, MinecraftClient.class, MinecraftClient.ChatRestriction.class},
        targets = {"net.minecraft.client.render.model.json.ModelOverrideList$InlinedCondition"}
)
public class ExampleMixin implements TestShit, Yes, Nah {
    @Override
    public String getCoolTestShit() {
        return "whar.";
    }
}