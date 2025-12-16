package club.kid7.bannermaker.util;

import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 現代化的物品建構器，整合 XSeries 與 Adventure Component。
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(XMaterial xMaterial) {
        this(xMaterial.parseItem());
    }

    public ItemBuilder(ItemStack itemStack) {
        if (itemStack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }
        this.itemStack = itemStack.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder name(Component name) {
        if (itemMeta != null) {
            // 目前 Spigot API (非 Paper) 的 ItemMeta 尚未全面支援 Component，
            // 為了最大相容性，我們先序列化為 Legacy String。
            // 未來若全面轉向 Paper，可直接使用 name(Component) (如果有該 API)。
            // 註：Paper 的 ItemMeta 支援 displayName(Component)。
            // 這裡我們暫時用 Legacy 轉換以確保安全。
            String legacyName = LegacyComponentSerializer.legacySection().serialize(name);
            itemMeta.setDisplayName(legacyName);
        }
        return this;
    }

    public ItemBuilder name(String name) {
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
        }
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<Component> lore) {
        if (itemMeta != null) {
            List<String> legacyLore = lore.stream()
                .map(c -> LegacyComponentSerializer.legacySection().serialize(c))
                .collect(Collectors.toList());
            itemMeta.setLore(legacyLore);
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (itemMeta != null) {
            itemMeta.setLore(Arrays.asList(lore));
        }
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        if (itemMeta != null) {
            List<String> currentLore = itemMeta.getLore();
            if (currentLore == null) {
                currentLore = new ArrayList<>();
            }
            currentLore.addAll(Arrays.asList(lore));
            itemMeta.setLore(currentLore);
        }
        return this;
    }

    public ItemBuilder addLore(Component... lore) {
        if (itemMeta != null) {
            List<String> currentLore = itemMeta.getLore();
            if (currentLore == null) {
                currentLore = new ArrayList<>();
            }
            for (Component c : lore) {
                currentLore.add(LegacyComponentSerializer.legacySection().serialize(c));
            }
            itemMeta.setLore(currentLore);
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (itemMeta != null) {
            itemMeta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        if (itemMeta != null) {
            itemMeta.addItemFlags(flags);
        }
        return this;
    }

    public ItemBuilder customModelData(int data) {
        if (itemMeta != null) {
            // 需要較新版本 Bukkit 支援
            try {
                itemMeta.setCustomModelData(data);
            } catch (NoSuchMethodError ignored) {
                // 舊版本不支援 CustomModelData
            }
        }
        return this;
    }

    public ItemBuilder pattern(org.bukkit.block.banner.Pattern pattern) {
        if (itemMeta instanceof org.bukkit.inventory.meta.BannerMeta) {
            ((org.bukkit.inventory.meta.BannerMeta) itemMeta).addPattern(pattern);
        }
        return this;
    }

    public ItemBuilder setPatterns(List<org.bukkit.block.banner.Pattern> patterns) {
        if (itemMeta instanceof org.bukkit.inventory.meta.BannerMeta) {
            ((org.bukkit.inventory.meta.BannerMeta) itemMeta).setPatterns(patterns);
        }
        return this;
    }

    public ItemStack build() {
        if (itemMeta != null) {
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
