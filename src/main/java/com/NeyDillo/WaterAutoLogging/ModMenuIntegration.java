package com.NeyDillo.WaterAutoLogging;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModConfig config = ConfigManager.loadConfig();
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("登录设置"));
            ConfigCategory category = builder.getOrCreateCategory(Text.literal("一般"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            category.addEntry(entryBuilder.startStrField(Text.literal("登陆密码"), config.password)
                    .setDefaultValue("")
                    .setTooltip(Text.literal("自动登陆时使用的密码"))
                    .setSaveConsumer(newValue -> config.password = newValue)
                    .build()
            );
            category.addEntry(entryBuilder.startBooleanToggle(Text.literal("自动飞行"), config.autoFly)
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("进区后自动开启飞行\n默认: §c关"))
                    .setYesNoTextSupplier(bool -> bool ? Text.literal("§a开") : Text.literal("§c关"))
                    .setSaveConsumer(newValue -> config.autoFly = newValue)
                    .build()
            );
            builder.setSavingRunnable(() -> {
                ConfigManager.saveConfig(config);
                WaterAutoLoggingClient.CONFIG = config;
            });
            return builder.build();
        };
    }
}