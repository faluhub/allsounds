package me.wurgo.allsounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class RemainingSoundsScreen extends Screen {
    private final Screen parent;
    private RemainingSoundsListWidget remainingSoundsListWidget;

    public RemainingSoundsScreen(String title, Screen parent) {
        super(new LiteralText(title));

        this.parent = parent;
    }

    @Override
    protected void init() {
        try {
            this.remainingSoundsListWidget = new RemainingSoundsListWidget(this.client);
            this.children.add(this.remainingSoundsListWidget);
        }
        catch (IOException e) { e.printStackTrace(); }

        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 38, 150, 20, new LiteralText("Clear Sounds"), (buttonWidget) -> {
            try {
                AllSounds.clearSounds();
                if (this.client != null) {
                    this.client.openScreen(this.parent);
                }
            } catch (IOException ignored) {}
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, ScreenTexts.DONE, (buttonWidget) -> {
            if (this.client != null) {
                this.client.openScreen(this.parent);
            }
        }));

        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.remainingSoundsListWidget != null) { this.remainingSoundsListWidget.render(matrices, mouseX, mouseY, delta); }

        this.drawCenteredText(matrices, textRenderer, this.title, this.width / 2, 13, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(EnvType.CLIENT)
    class RemainingSoundsListWidget extends AlwaysSelectedEntryListWidget<RemainingSoundsListWidget.SoundEntry> {
        public RemainingSoundsListWidget(MinecraftClient client) throws IOException {
            super(
                    client,
                    RemainingSoundsScreen.this.width,
                    RemainingSoundsScreen.this.height,
                    32,
                    RemainingSoundsScreen.this.height - 65 + 4,
                    18
            );

            for (String s : AllSounds.getRemainingSounds()) {
                SoundEntry soundEntry = new SoundEntry(s);
                this.addEntry(soundEntry);
            }

            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }

        protected int getScrollbarPositionX() { return super.getScrollbarPositionX() + 20; }
        public int getRowWidth() { return super.getRowWidth() + 50; }
        public void setSelected(@Nullable RemainingSoundsScreen.RemainingSoundsListWidget.SoundEntry soundEntry) { super.setSelected(soundEntry); }
        protected void renderBackground(MatrixStack matrices) {
            RemainingSoundsScreen.this.renderBackground(matrices);
        }
        protected boolean isFocused() {
            return RemainingSoundsScreen.this.getFocused() == this;
        }

        @Environment(EnvType.CLIENT)
        public class SoundEntry extends AlwaysSelectedEntryListWidget.Entry<RemainingSoundsListWidget.SoundEntry> {
            private final String key;

            public SoundEntry(String key) { this.key = key; }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                RemainingSoundsScreen.this.textRenderer.drawWithShadow(
                        matrices,
                        new TranslatableText(this.key).getString(),
                        (float) (RemainingSoundsScreen.this.width / 2 - RemainingSoundsScreen.this.textRenderer.getWidth(new TranslatableText(this.key).getString()) / 2),
                        y + 1,
                        16777215,
                        true
                );
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.onPressed();
                    return true;
                }
                else { return false; }
            }

            public void onPressed() {
                RemainingSoundsListWidget.this.setSelected(this);
            }
        }
    }
}
