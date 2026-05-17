package tw.jyhsu.bannermaker.gui;

import tw.jyhsu.bannermaker.banner.AlphabetBanner;
import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.PlayerData;
import tw.jyhsu.bannermaker.registry.DyeColorRegistry;
import tw.jyhsu.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static tw.jyhsu.bannermaker.configuration.Language.tl;

public class CreateAlphabetGUI {

    public static void show(Player player) {
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        ChestGui gui = GuiUtil.createChestGui("gui.title.alphabet-and-number");
        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        // 進入 Create 介面但 PlayerData 內沒有「正在編輯的字母」── 通常是直接從外部 caller 進來
        // 而非從 Choose 點字母選進來。引導回 Choose 讓玩家先挑字。
        final AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
        if (currentAlphabetBanner == null) {
            ChooseAlphabetGUI.show(player);
            return;
        }

        // Slot 0 (0,0): 預覽
        mainPane.addItem(new GuiItem(currentAlphabetBanner.toItemStack()), 0, 0);

        // 底色選擇 (Slots 1-8, 10-17，跳過第 9 格)
        for (int i = 0; i < 16; i++) {
            final ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(i)).build();
            int slot = GuiUtil.gridSlot(1, i);
            mainPane.addItem(new GuiItem(banner, event -> {
                currentAlphabetBanner.setBaseColor(DyeColorRegistry.getDyeColor(banner.getType()));
                CreateAlphabetGUI.show(player);
            }), slot % 9, slot / 9);
        }

        // 染料顏色選擇 (Slots 19-26, 28-35，跳過第 27 格)
        for (int i = 0; i < 16; i++) {
            final ItemStack dye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(i)).build();
            int slot = GuiUtil.gridSlot(19, i);
            mainPane.addItem(new GuiItem(dye, event -> {
                currentAlphabetBanner.setDyeColor(DyeColorRegistry.getDyeColor(dye.getType()));
                CreateAlphabetGUI.show(player);
            }), slot % 9, slot / 9);
        }

        // Slot 37 (1,4): 切換邊框（位置維持 row 4，不在工具列風格範圍內）
        ItemStack btnBorderedBanner = new ItemBuilder(Material.WHITE_BANNER)
            .name(tl(NamedTextColor.GREEN, "gui.toggle-border"))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER)).build();
        mainPane.addItem(new GuiItem(btnBorderedBanner, event -> {
            currentAlphabetBanner.setBordered(!currentAlphabetBanner.isBordered());
            CreateAlphabetGUI.show(player);
        }), 1, 4);

        // 工具列風格：row 5 先填灰玻璃
        GuiUtil.fillToolbarRow(mainPane, 5);

        // slot 0: 返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        GuiUtil.putAt(mainPane, 0, 5, btnBackToMenu, event -> {
            ChooseAlphabetGUI.show(player);
        });

        // slot 4: 旗幟資訊
        ItemStack btnBannerInfo = new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.banner-info")).build();
        GuiUtil.putAt(mainPane, 4, 5, btnBannerInfo, event -> {
            BannerInfoGUI.open(player, currentAlphabetBanner.toItemStack());
        });

        gui.show(player);
    }
}
