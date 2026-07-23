package com.NeyDillo.WaterAutoLogging;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
public class WaterAutoLoggingClient implements ClientModInitializer {
    public static ModConfig CONFIG;
    @Override
    public void onInitializeClient() {
        CONFIG = ConfigManager.loadConfig();
        MessageProcessor.initialize();
        if (CONFIG.ignoreJavaWarning) {
            return;
        }
        String javaVersion = System.getProperty("java.version");
        if (!javaVersion.startsWith("21") && !javaVersion.startsWith("21.")) {
            MinecraftClient client = MinecraftClient.getInstance();
            String modName = FabricLoader.getInstance()
                    .getModContainer("water_autologging")
                    .map(container -> container.getMetadata().getName())
                    .orElse("自动登录");
            client.execute(() -> {
                client.setScreen(new JavaVersionWarningScreen(
                        Text.literal("§c警告"),
                        Text.literal("模组§6" + modName + "§r推荐的Java版本为§a21§r, 而当前是§c" + javaVersion + "§r\n这可能会导致§cBug§r甚至是§c游戏崩溃"),
                        () -> {
                            CONFIG.ignoreJavaWarning = true;
                            ConfigManager.saveConfig(CONFIG);
                        }
                ));
            });
        }
    }
    private static class JavaVersionWarningScreen extends Screen {
        private final Text message;
        private final Runnable onDontShowAgain;
        protected JavaVersionWarningScreen(Text title, Text message, Runnable onDontShowAgain) {
            super(title);
            this.message = message;
            this.onDontShowAgain = onDontShowAgain;
        }
        @Override
        protected void init() {
            super.init();
            this.addDrawableChild(ButtonWidget.builder(Text.literal("不再提示"), button -> {
                onDontShowAgain.run();
                MinecraftClient.getInstance().setScreen(null);
            }).tooltip(Tooltip.of(Text.literal("§c我知道我在做什么")))
            .dimensions(this.width / 2 - 110, this.height / 2 + 30, 100, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("§a退出游戏"), button -> {
                MinecraftClient.getInstance().stop();
            }).dimensions(this.width / 2 + 10, this.height / 2 + 30, 100, 20).build());
        }
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 70, 0xFF5555);
            String[] lines = this.message.getString().split("\n");
            int y = this.height / 2 - 30;
            for (String line : lines) {
                context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(line), this.width / 2, y, 0xFFFFFF);
                y += 12;
            }
            super.render(context, mouseX, mouseY, delta);
        }
        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }
    }
}