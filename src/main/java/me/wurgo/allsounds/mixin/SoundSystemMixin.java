package me.wurgo.allsounds.mixin;

import me.wurgo.allsounds.AllSounds;
import net.minecraft.client.sound.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Shadow @Final private SoundManager loader;
    @Shadow private boolean started;

    @Inject(at = @At("TAIL"), method = "play(Lnet/minecraft/client/sound/SoundInstance;)V")
    public void allsounds_ss_play(SoundInstance soundInstance, CallbackInfo ci) {
        if (this.started) {
            if (soundInstance.canPlay()) {
                WeightedSoundSet weightedSoundSet = soundInstance.getSoundSet(this.loader);

                if (!(weightedSoundSet == null)) {
                    Text subtitle = weightedSoundSet.getSubtitle();

                    if (!(subtitle == null)) {
                        String strSubtitle = subtitle.toString().split("'")[1];

                        try {
                            if (!AllSounds.getSounds().contains(strSubtitle)) {
                                AllSounds.addSound(strSubtitle);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
