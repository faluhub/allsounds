package me.wurgo.allsounds.mixin;

import me.wurgo.allsounds.RemainingSoundsScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    private static final Identifier ARROW = new Identifier("textures/item/arrow.png");
    private static ButtonWidget remainingButton;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void allsounds_gms_init(CallbackInfo ci) {
        remainingButton = new ButtonWidget(
                this.width / 2 + 102 + 5,
                this.height / 4 + 24 - 16,
                20, 20,
                new LiteralText(""),
                (button) -> {
                    if (this.client != null) { this.client.openScreen(new RemainingSoundsScreen(this)); }
                }
        );
        this.addButton(remainingButton);
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void allsounds_gms_render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.client != null) {
            this.client.getTextureManager().bindTexture(ARROW);
            drawTexture(
                    matrices,
                    remainingButton.x + 2,
                    remainingButton.y + 2,
                    0.0F, 0.0F,
                    16, 16, 16, 16
            );
        }
    }
}
