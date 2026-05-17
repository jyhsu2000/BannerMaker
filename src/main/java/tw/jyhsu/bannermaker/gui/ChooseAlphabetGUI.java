package tw.jyhsu.bannermaker.gui;

import tw.jyhsu.bannermaker.AlphabetBanner;
import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.PlayerData;
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

import java.util.List;

import static tw.jyhsu.bannermaker.configuration.Language.tl;

public class ChooseAlphabetGUI {

    public static void show(Player player) {
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        ChestGui gui = GuiUtil.createChestGui("gui.title.alphabet-and-number");
        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        // 清除當前編輯中的字母旗幟
        playerData.setCurrentAlphabetBanner(null);

        boolean alphabetBorder = playerData.isAlphabetBannerBordered();

        // 填充字母物品（覆蓋 row 0-4，row 5 留給工具列）
        List<String> characters = AlphabetBanner.SUPPORTED_CHARACTERS;
        for (int i = 0; i < characters.size() && i < 45; i++) {
            final AlphabetBanner alphabetBanner = new AlphabetBanner(characters.get(i), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
            ItemStack alphabetItem = alphabetBanner.toItemStack();
            mainPane.addItem(new GuiItem(alphabetItem, event -> {
                playerData.setCurrentAlphabetBanner(alphabetBanner);
                CreateAlphabetGUI.show(player);
            }), i % 9, i / 9);
        }

        // 工具列風格：row 5 先填灰玻璃、覆蓋字母列表落到 row 5 部分（字母至 slot 38 不會碰到 row 5、純為視覺一致）
        GuiUtil.fillToolbarRow(mainPane, 5);

        // slot 0: 返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        GuiUtil.putAt(mainPane, 0, 5, btnBackToMenu, event -> {
            MainMenuGUI.show(player);
        });

        // slot 4: 切換邊框
        ItemStack btnBorderedBanner = new ItemBuilder(Material.WHITE_BANNER)
            .name(tl(NamedTextColor.GREEN, "gui.toggle-border"))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER)).build();
        GuiUtil.putAt(mainPane, 4, 5, btnBorderedBanner, event -> {
            playerData.setAlphabetBannerBordered(!playerData.isAlphabetBannerBordered());
            ChooseAlphabetGUI.show(player); // 刷新以顯示變更
        });

        gui.show(player);
    }
}
