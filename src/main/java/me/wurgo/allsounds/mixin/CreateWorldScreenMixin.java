package me.wurgo.allsounds.mixin;

import me.wurgo.allsounds.AllSounds;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin extends Screen {
    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "createLevel")
    public void allsounds_cws_createLevel(CallbackInfo ci) throws IOException {
        AllSounds.clearSounds();
    }
}
