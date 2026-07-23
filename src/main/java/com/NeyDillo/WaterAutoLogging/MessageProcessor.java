package com.NeyDillo.WaterAutoLogging;
import net.minecraft.client.MinecraftClient;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Pattern;
public class MessageProcessor {
    private static final Deque<String> history = new ConcurrentLinkedDeque<>();
    private static final int MAX_HISTORY = 100;
    private static final Map<TriggerAction, Long> lastTrigger = new HashMap<>();
    private static final long COOLDOWN_MS = 1000;
    private static final Pattern COLOR_PATTERN = Pattern.compile("§[0-9a-fk-or]");
    private static String stripColors(String input) {
        if (input == null) return null;
        return COLOR_PATTERN.matcher(input).replaceAll("");
    }
    enum TriggerAction {
        LOGIN(
                Arrays.asList(
                        "滑滑水 | Water",
                        "您还没有登录账户,请执行以下命令进行登录",
                        "登录 /Login <密码>",
                        "按T或回车打开聊天栏操作"
                ),
                () -> {
                    String pwd = WaterAutoLoggingClient.CONFIG.password;
                    if (pwd == null || pwd.isEmpty()) {
                        return null;
                    }
                    return "/l " + pwd;
                }
        ),
        HUB(
                Arrays.asList(
                        "滑滑水 | Water",
                        "已成功登录,",
                        "请右键前方NPC加入游戏吧!"
                ),
                () -> "/hub"
        ),
        FLY(
                Arrays.asList(
                        "正在加载您的数据...",
                        "数据加载完成."
                ),
                () -> WaterAutoLoggingClient.CONFIG.autoFly ? "/fly enable" : null
        );
        final List<String> fragments;
        final CommandSupplier commandSupplier;
        TriggerAction(List<String> fragments, CommandSupplier commandSupplier) {
            this.fragments = fragments;
            this.commandSupplier = commandSupplier;
        }
        interface CommandSupplier {
            String get();
        }
    }
    public static void initialize() {
        history.clear();
        lastTrigger.clear();
    }
    public static void processMessage(String msg) {
        String stripped = stripColors(msg);
        String trimmed = stripped.trim();
        history.addLast(trimmed);
        if (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }
        int currentIndex = history.size() - 1;
        for (TriggerAction action : TriggerAction.values()) {
            boolean matched = false;
            for (String frag : action.fragments) {
                if (trimmed.contains(frag)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) continue;
            int start = Math.max(0, currentIndex - 5);
            int end = Math.min(history.size(), currentIndex + 6);
            List<String> sub = new ArrayList<>(history).subList(start, end);
            boolean allPresent = true;
            for (String frag : action.fragments) {
                boolean found = false;
                for (String line : sub) {
                    if (line.contains(frag)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    allPresent = false;
                    break;
                }
            }
            if (!allPresent) continue;
            long now = System.currentTimeMillis();
            Long last = lastTrigger.get(action);
            if (last != null && (now - last) < COOLDOWN_MS) {
                continue;
            }
            String command = action.commandSupplier.get();
            if (command == null) continue;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.player.networkHandler != null) {
                client.player.networkHandler.sendCommand(command.substring(1));
                lastTrigger.put(action, now);
            }
        }
    }
}