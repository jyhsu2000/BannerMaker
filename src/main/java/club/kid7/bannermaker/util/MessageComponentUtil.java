package club.kid7.bannermaker.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.inventory.ItemStack;

// 訊息組件工具類，用於將 Bukkit 物品轉換為 Adventure 的文本組件和懸停事件
public class MessageComponentUtil {

    // 獲取物品的可翻譯組件
    // 透過物品的本地化鍵 (translation key) 來創建一個可翻譯的文本組件
    public static TranslatableComponent getTranslatableComponent(ItemStack itemStack) {
        return Component.translatable(itemStack.getTranslationKey());
    }

    // 獲取物品的懸停事件
    // 暫時僅使用物品類型和數量，不包含 NBT 數據 (如附魔、名稱等)
    // TODO: 解決 BukkitAdapter 依賴問題後，恢復完整的 NBT 支援。
    public static HoverEvent<HoverEvent.ShowItem> getHoverEventItem(ItemStack itemStack) {
        // 手動構建 Key (NamespacedKey -> Adventure Key)
        Key key = Key.key(itemStack.getType().getKey().toString());
        // 獲取數量
        int amount = itemStack.getAmount();

        // 返回 HoverEvent 物件
        return HoverEvent.showItem(HoverEvent.ShowItem.of(key, amount));
    }
}
