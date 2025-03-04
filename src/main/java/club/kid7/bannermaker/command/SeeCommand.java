package club.kid7.bannermaker.command;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.bannermaker.util.MessageUtil;
import club.kid7.pluginutilities.command.CommandComponent;
import club.kid7.pluginutilities.kitemstack.KItemStack;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static club.kid7.bannermaker.configuration.Language.tl;

public class SeeCommand extends CommandComponent {
    //名稱
    private static final String name = "See";
    //介紹
    private static final String description = "Show banner info of the banner you're looking at";
    //權限
    private static final String permission = "BannerMaker.see";
    //使用方法
    private static final String usage = "/bm see";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    public SeeCommand(BannerMaker bm) {
        super(bm, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 20);
        if (!block.getType().name().endsWith("_BANNER")) {
            player.sendMessage(MessageUtil.format(true, "&c" + tl("command.not-banner-see")));
            return true;
        }
        //根據方塊建立旗幟
        Banner blockState = (Banner) block.getState();
        KItemStack banner = new KItemStack(DyeColorRegistry.getBannerMaterial(blockState.getBaseColor()))
            .setPatterns(blockState.getPatterns());
        //顯示旗幟
        InventoryMenuUtil.openBannerInfo(player, banner);
        return true;
    }
}
