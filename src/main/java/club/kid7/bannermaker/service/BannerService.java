package club.kid7.bannermaker.service;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import club.kid7.bannermaker.util.MessageComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static club.kid7.bannermaker.configuration.Language.tl;
import static club.kid7.bannermaker.util.TagUtil.tag;

public class BannerService {

    /**
     * 使用材料合成旗幟
     *
     * @param player 要給予物品的玩家
     * @param banner 要給予的旗幟
     * @return 是否成功給予
     */
    public boolean craft(Player player, ItemStack banner) {
        //檢查材料
        if (!BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
            return false;
        }
        //移除材料
        if (!removeMaterials(player, banner)) {
            return false;
        }

        InventoryUtil.give(player, banner);
        return true;
    }

    /**
     * 購買旗幟
     *
     * @param player 要給予物品的玩家
     * @param banner 要給予的旗幟
     * @return 是否成功給予
     */
    public boolean buy(Player player, ItemStack banner) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        EconomyService economyService = BannerMaker.getInstance().getEconomyService();
        //檢查是否啟用經濟
        if (!economyService.isAvailable()) {
            //未啟用經濟，強制失敗
            messageService.send(player, Component.text("Error: Economy not supported", NamedTextColor.RED));
            return false;
        }
        //價格
        double price = economyService.getPrice(banner);
        //檢查財產是否足夠
        if (!economyService.has(player, price)) {
            //財產不足
            messageService.send(player, tl(NamedTextColor.RED, "general.no-money"));
            return false;
        }
        //扣款
        EconomyResponse response = economyService.withdraw(player, price);
        //檢查交易是否成功
        if (!response.transactionSuccess()) {
            //交易失敗
            messageService.send(player, Component.text("Error: " + response.errorMessage, NamedTextColor.RED));
            return false;
        }
        InventoryUtil.give(player, banner);
        messageService.send(player, tl(NamedTextColor.GREEN, "general.money-transaction",
            tag("amount", economyService.format(response.amount)),
            tag("balance", economyService.format(response.balance))));
        return true;
    }

    /**
     * 展示旗幟給附近玩家
     *
     * @param sender 發送者
     * @param banner 要展示的旗幟
     * @param maxDistance 最大距離
     */
    public void showToNearby(Player sender, ItemStack banner, double maxDistance) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        Component msgBannerName = buildBannerMessageComponent(banner);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("BannerMaker.show.receive") && !p.equals(sender)) {
                continue;
            }
            if (!p.getWorld().equals(sender.getWorld())) {
                continue;
            }
            if (p.getLocation().distanceSquared(sender.getLocation()) > maxDistance * maxDistance) {
                continue;
            }
            messageService.send(p, buildShowMessage(sender, msgBannerName));
        }
    }

    /**
     * 展示旗幟給所有玩家
     *
     * @param sender 發送者
     * @param banner 要展示的旗幟
     */
    public void showToAll(Player sender, ItemStack banner) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        Component msgBannerName = buildBannerMessageComponent(banner);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("BannerMaker.show.receive") && !p.equals(sender)) {
                continue;
            }
            messageService.send(p, buildShowMessage(sender, msgBannerName));
        }
    }

    /**
     * 建立旗幟展示訊息組件（含懸停與點擊事件）
     */
    private Component buildBannerMessageComponent(ItemStack banner) {
        String bannerString = BannerUtil.serialize(banner);
        return MessageComponentUtil.getTranslatableComponent(banner)
            .hoverEvent(MessageComponentUtil.getHoverEventItem(banner))
            .clickEvent(ClickEvent.runCommand("/bm view " + bannerString));
    }

    /**
     * 建立展示訊息的完整文字
     */
    private Component buildShowMessage(Player sender, Component bannerName) {
        return Component.text(sender.getDisplayName()).color(NamedTextColor.YELLOW)
            .append(Component.text(" shows you the banner ").color(NamedTextColor.GRAY))
            .append(bannerName)
            .append(Component.text(" (Click to view)").color(NamedTextColor.DARK_GRAY));
    }

    /**
     * 發送旗幟分享指令給玩家
     *
     * @param player 玩家
     * @param banner 要分享的旗幟
     */
    public void sendShareCommand(Player player, ItemStack banner) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        String bannerString = BannerUtil.serialize(banner);
        Component msg = Component.text("[Click here to copy command to clipboard]")
            .hoverEvent(HoverEvent.showText(Component.text("Copy command to clipboard")))
            .clickEvent(ClickEvent.copyToClipboard("/bm view " + bannerString));
        messageService.send(player, msg);
    }

    /**
     * 從物品欄移除材料
     */
    private boolean removeMaterials(Player player, ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return false;
        }
        if (!BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
            return false;
        }
        List<ItemStack> materials = BannerUtil.getMaterials(banner);
        //過濾材料，不須消耗旗幟圖形
        materials.removeIf(BannerUtil::isBannerPatternItemStack);
        HashMap<Integer, ItemStack> itemCannotRemoved = player.getInventory().removeItem(materials.toArray(new ItemStack[0]));
        return itemCannotRemoved.isEmpty();
    }
}
