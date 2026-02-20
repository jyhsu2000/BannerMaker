package club.kid7.bannermaker.service;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageService {

    private final MiniMessage miniMessage;
    // 傳統顏色代碼 '&' 序列化器，用於兼容舊版訊息或確保特定情況下的處理
    private final LegacyComponentSerializer legacySerializer;
    private BukkitAudiences audiences;

    /**
     * 建構 MessageService 實例。
     *
     * @param plugin JavaPlugin 實例，用於初始化 Adventure Audiences。
     */
    public MessageService(JavaPlugin plugin) {
        this.miniMessage = MiniMessage.miniMessage();
        // 配置 LegacyComponentSerializer 支援 '&' 符號
        this.legacySerializer = LegacyComponentSerializer.builder().character('&').build();
        // 在建構時就初始化 audiences
        this.audiences = BukkitAudiences.create(plugin);
    }

    /**
     * 關閉 Adventure Audiences。應在插件 onDisable 時呼叫。
     */
    public void closeAudiences() {
        if (this.audiences != null) {
            this.audiences.close();
            this.audiences = null;
        }
    }

    /**
     * 將 Adventure Component 發送給 CommandSender。
     * 對 Player 使用 JSON 序列化 + Spigot API，確保 ClickEvent / HoverEvent 正確保留。
     * Workaround: adventure-platform-bukkit 4.4.1 僅支援至 MC 1.21.6，
     * 在 Paper 1.21.7+ 上透過 BukkitAudiences 發送時會遺失互動事件。
     *
     * @param sender  訊息接收者。
     * @param message 訊息 Component。
     */
    public void send(CommandSender sender, Component message) {
        // Workaround: adventure-platform-bukkit 4.4.1 在 Paper 1.21.7+ 會遺失 ClickEvent / HoverEvent，
        // 改用 JSON 中介 + Spigot API 繞過此問題
        if (sender instanceof Player player) {
            try {
                String json = GsonComponentSerializer.gson().serialize(message);
                BaseComponent[] components = ComponentSerializer.parse(json);
                player.spigot().sendMessage(components);
                return;
            } catch (Exception ignored) {
                // JSON 序列化失敗時回退至 BukkitAudiences
            }
        }
        if (audiences == null) {
            sender.sendMessage(legacySerializer.serialize(message));
            return;
        }
        audiences.sender(sender).sendMessage(message);
    }

    /**
     * 將 MiniMessage 格式的訊息發送給 CommandSender。
     * MiniMessage 預設已支援 '&' 顏色代碼的轉換。
     *
     * @param sender  訊息接收者。
     * @param message 訊息字串，可為 MiniMessage 或 '&' 顏色格式。
     */
    public void send(CommandSender sender, String message) {
        if (audiences == null) {
            // 如果 audiences 未初始化 (不應該發生，但作為防禦性程式碼)，則使用傳統方式發送
            // 需要將 Component 轉換回 String 才能發送給原生的 Bukkit sender
            sender.sendMessage(legacySerializer.serialize(legacySerializer.deserialize(message)));
            return;
        }
        // 使用 LegacyComponentSerializer 解析 '&' 顏色代碼，確保舊版訊息正常顯示
        audiences.sender(sender).sendMessage(legacySerializer.deserialize(message));
    }

    /**
     * 格式化訊息字串，將 MiniMessage 或 '&' 顏色代碼轉換為 Adventure Component。
     *
     * @param message 訊息字串。
     * @return 格式化後的 Adventure Component。
     */
    public Component format(String message) {
        return legacySerializer.deserialize(message);
    }

    /**
     * 格式化訊息字串，並在前面加上一個前綴 Component。
     *
     * @param prefix  前綴 Adventure Component。
     * @param message 訊息字串。
     * @return 包含前綴並格式化後的 Adventure Component。
     */
    public Component formatWithPrefix(Component prefix, String message) {
        return prefix.append(legacySerializer.deserialize(message));
    }

    /**
     * 格式化訊息 Component，並在前面加上一個前綴 Component。
     *
     * @param prefix  前綴 Adventure Component。
     * @param message 訊息 Adventure Component。
     * @return 包含前綴並格式化後的 Adventure Component。
     */
    public Component formatWithPrefix(Component prefix, Component message) {
        return prefix.append(message);
    }

    /**
     * 將 MiniMessage 或 '&' 顏色代碼格式化為傳統的 '§' 顏色字串。
     * 用於兼容舊版 API 或不支援 Component 的地方。
     *
     * @param message 訊息字串。
     * @return 帶有 '§' 顏色代碼的字串。
     */
    public String formatToString(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
