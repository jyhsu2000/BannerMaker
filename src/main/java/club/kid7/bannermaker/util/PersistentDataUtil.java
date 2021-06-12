package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataUtil {
    static public void set(ItemMeta itemMeta, String keyName, String value) {
        set(itemMeta, keyName, PersistentDataType.STRING, value);
    }

    static public String get(ItemMeta itemMeta, String keyName) {
        return get(itemMeta, keyName, PersistentDataType.STRING);
    }

    static public <T, Z> void set(ItemMeta itemMeta, String keyName, PersistentDataType<T, Z> persistentDataType, Z value) {
        NamespacedKey namespacedKey = new NamespacedKey(BannerMaker.getInstance(), keyName);
        itemMeta.getPersistentDataContainer().set(namespacedKey, persistentDataType, value);
    }

    static public <T, Z> Z get(ItemMeta itemMeta, String keyName, PersistentDataType<T, Z> persistentDataType) {
        Z value;
        try {
            NamespacedKey namespacedKey = new NamespacedKey(BannerMaker.getInstance(), keyName);
            value = itemMeta.getPersistentDataContainer().get(namespacedKey, persistentDataType);
        } catch (Exception exception) {
            return null;
        }
        return value;
    }

    static public void remove(ItemMeta itemMeta, String keyName) {
        NamespacedKey namespacedKey = new NamespacedKey(BannerMaker.getInstance(), keyName);
        itemMeta.getPersistentDataContainer().remove(namespacedKey);
    }
}
