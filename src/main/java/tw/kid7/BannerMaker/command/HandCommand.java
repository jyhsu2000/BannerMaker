package tw.kid7.BannerMaker.command;

import club.kid7.pluginutilities.kitemstack.KItemStack;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.BannerUtil;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

import static tw.kid7.BannerMaker.configuration.Language.tl;

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
        ItemStack itemStack = bm.getVersionHandler().getItemInMainHand(player);
        if (!BannerUtil.isBanner(itemStack)) {
            player.sendMessage(MessageUtil.format(true, "&c" + tl("command.not-banner-hand")));
            return true;
        }
        //複製旗幟，僅保留底色與樣式
        BannerMeta originalBannerMeta = (BannerMeta) itemStack.getItemMeta();
        KItemStack banner = new KItemStack(Material.BANNER)
            .durability(itemStack.getDurability())
            .setPatterns(originalBannerMeta.getPatterns());
        //顯示旗幟
        InventoryMenuUtil.showBannerInfo(player, banner);
        return true;
    }
}
