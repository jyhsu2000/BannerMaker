package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.DyeColorUtil;
import club.kid7.bannermaker.util.IOUtil;
import club.kid7.bannermaker.util.MessageUtil;
import club.kid7.pluginutilities.gui.CustomGUIInventory;
import club.kid7.pluginutilities.gui.CustomGUIManager;
import club.kid7.pluginutilities.gui.CustomGUIMenu;
import club.kid7.pluginutilities.kitemstack.KItemStack;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import static club.kid7.bannermaker.configuration.Language.tl;

public class CreateBannerMenu implements CustomGUIMenu {
    @Override
    public CustomGUIInventory build(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        String title = MessageUtil.format(tl("gui.prefix") + tl("gui.create-banner"));
        CustomGUIInventory menu = new CustomGUIInventory(title);
        //返回
        KItemStack btnBackToMenu = new KItemStack(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back")));
        menu.setClickableItem(45, btnBackToMenu).set(ClickType.LEFT, event -> CustomGUIManager.open(player, MainMenu.class));
        //取得當前編輯中的旗幟
        final ItemStack currentBanner = playerData.getCurrentEditBanner();
        if (currentBanner == null) {
            //剛開始編輯，先選擇底色
            for (int i = 0; i < 16; i++) {
                final KItemStack banner = new KItemStack(Material.BANNER).durability(i);
                menu.setClickableItem(i + 1 + (i / 8), banner).set(ClickType.LEFT, event -> {
                    playerData.setCurrentEditBanner(banner);
                    CustomGUIManager.openPrevious(player);
                });
            }
            return menu;
        }
        //新增按鈕
        //當前旗幟
        menu.setItem(0, currentBanner);
        //patterns過多的警告
        if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 6) {
            KItemStack warning = new KItemStack(Material.SIGN).amount(1).name(MessageUtil.format("&c" + tl("gui.uncraftable-warning")))
                .lore(tl("gui.more-than-6-patterns"));
            menu.setItem(9, warning);
        }
        //顏色
        for (int i = 0; i < 16; i++) {
            final KItemStack dye = new KItemStack(Material.INK_SACK).amount(1).durability(i);
            menu.setClickableItem(i + 1 + (i / 8), dye).set(ClickType.LEFT, event -> {
                playerData.setSelectedColor(DyeColorUtil.fromInt(dye.getDurability()));
                CustomGUIManager.openPrevious(player);
            });
        }
        //Pattern
        //選擇的顏色
        DyeColor selectedColor = playerData.getSelectedColor();
        for (int i = 0; i < 24; i++) {
            int patternIndex = i;
            if (playerData.isShowMorePatterns()) {
                patternIndex += 24;
            }
            if (patternIndex >= BannerUtil.getPatternTypeList().size()) {
                break;
            }
            //預覽旗幟
            PatternType patternType = BannerUtil.getPatternTypeList().get(patternIndex);
            final KItemStack banner = new KItemStack(Material.BANNER)
                .durability(currentBanner.getDurability())
                .pattern(new Pattern(selectedColor, patternType));
            menu.setClickableItem(i + 19 + (i / 8), banner).set(ClickType.LEFT, event -> {
                //新增Pattern
                BannerMeta bm = (BannerMeta) banner.getItemMeta();
                Pattern pattern = bm.getPattern(bm.numberOfPatterns() - 1);
                BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
                currentBm.addPattern(pattern);
                currentBanner.setItemMeta(currentBm);
                playerData.setCurrentEditBanner(currentBanner);
                CustomGUIManager.openPrevious(player);
            });
        }
        //更多Pattern
        KItemStack btnMorePattern = new KItemStack(Material.NETHER_STAR).amount(1).name(MessageUtil.format("&a" + tl("gui.more-patterns")));
        menu.setClickableItem(51, btnMorePattern).set(ClickType.LEFT, event -> {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
            CustomGUIManager.openPrevious(player);
        });
        //建立旗幟
        KItemStack btnCreate = new KItemStack(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + tl("gui.create")));
        menu.setClickableItem(53, btnCreate).set(ClickType.LEFT, event -> {
            IOUtil.saveBanner(player, currentBanner);
            playerData.setCurrentEditBanner(null);
            CustomGUIManager.open(player, MainMenu.class);
        });
        //刪除
        KItemStack btnDelete = new KItemStack(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + tl("gui.delete")));
        menu.setClickableItem(47, btnDelete).set(ClickType.LEFT, event -> {
            playerData.setCurrentEditBanner(null);
            CustomGUIManager.openPrevious(player);
        });
        if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 0) {
            //移除Pattern
            KItemStack btnRemovePattern = new KItemStack(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + tl("gui.remove-last-pattern")));
            menu.setClickableItem(49, btnRemovePattern).set(ClickType.LEFT, event -> {
                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                bm.removePattern(bm.numberOfPatterns() - 1);
                currentBanner.setItemMeta(bm);
                playerData.setCurrentEditBanner(currentBanner);
                CustomGUIManager.openPrevious(player);
            });
        }
        return menu;
    }
}
