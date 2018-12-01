package club.kid7.bannermaker.command;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.Language;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.DyeColorUtil;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.bannermaker.util.MessageUtil;
import club.kid7.pluginutilities.command.CommandComponent;
import club.kid7.pluginutilities.kitemstack.KItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class HandCommand extends CommandComponent {
    //名稱
    private static final String name = "Hand";
    //介紹
    private static final String description = "Show banner info of the banner in hand";
    //權限
    private static final String permission = "BannerMaker.hand";
    //使用方法
    private static final String usage = "/bm hand";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    public HandCommand(BannerMaker bm) {
        super(bm, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        BannerMaker bm = (BannerMaker) plugin;
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!BannerUtil.isBanner(itemStack)) {
            player.sendMessage(MessageUtil.format(true, "&c" + Language.tl("command.not-banner-hand")));
            return true;
        }
        //複製旗幟，僅保留底色與樣式
        BannerMeta originalBannerMeta = (BannerMeta) itemStack.getItemMeta();
        KItemStack banner = new KItemStack(DyeColorUtil.toBannerMaterial(DyeColorUtil.of(itemStack.getType())))
            .setPatterns(originalBannerMeta.getPatterns());
        //顯示旗幟
        InventoryMenuUtil.showBannerInfo(player, banner);
        return true;
    }
}
