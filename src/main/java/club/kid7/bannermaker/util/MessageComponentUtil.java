package club.kid7.bannermaker.util;

import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.inventory.ItemStack;

public class MessageComponentUtil {
    public static TranslatableComponent getTranslatableComponent(ItemStack itemStack) {
        return new TranslatableComponent(itemStack.getTranslationKey());
    }

    public static Item getHoverEventItem(ItemStack itemStack) {
        ItemTag itemTag = ItemTag.ofNbt(itemStack.getItemMeta() == null ? null : itemStack.getItemMeta().getAsString());
        return new Item(itemStack.getType().getKey().toString(), itemStack.getAmount(), itemTag);
    }
}
