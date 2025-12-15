package club.kid7.bannermaker.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.inventory.ItemStack;

// 訊息組件工具類，用於將 Bukkit 物品轉換為 Adventure 的文本組件和懸停事件
public class MessageComponentUtil {

    // 獲取物品的可翻譯組件
    // 透過物品的本地化鍵 (translation key) 來創建一個可翻譯的文本組件
    public static TranslatableComponent getTranslatableComponent(ItemStack itemStack) {
        return Component.translatable(itemStack.getTranslationKey());
    }

    // 獲取物品用於懸停事件的 ItemStack
    // 直接返回 Bukkit 的 ItemStack，由呼叫方進行 Adventure 轉換
    public static ItemStack getHoverEventItem(ItemStack itemStack) {
        return itemStack; // 直接返回原始的 ItemStack
    }
}
