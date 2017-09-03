package tw.kid7.BannerMaker.command;

import club.kid7.pluginutilities.gui.CustomGUIManager;
import club.kid7.pluginutilities.kitemstack.KItemStack;
import com.sk89q.intake.Command;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Require;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.Plugin;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.customMenu.MainMenu;
import tw.kid7.BannerMaker.util.BannerUtil;
import tw.kid7.BannerMaker.util.DyeColorUtil;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.Set;

import static tw.kid7.BannerMaker.configuration.Language.tl;

/**
 * 主要指令
 */
public class BannerMakerCommands {

    @Command(aliases = "", desc = "Show menu of BannerMaker")
    @Require("BannerMaker.use")
    public void main(CommandSender sender) throws CommandException {
        //限玩家使用
        if (!(sender instanceof Player)) {
            throw new CommandException(tl("command.player-only"));
        }
        Player player = (Player) sender;
        //開啟選單
        CustomGUIManager.openPrevious(player, MainMenu.class);
    }

    @Command(aliases = "help", desc = "Command list")
    public void help(Plugin plugin, CommandSender sender) {
        //FIXME: 似乎會被intake-spigot自動產生的help覆寫
        //插件資訊
        String pluginName = plugin.getName();
        String pluginVersion = plugin.getDescription().getVersion();
        //顯示標題
        sender.sendMessage(MessageUtil.format(true, pluginName + " - " + pluginVersion));
        //主要指令
        //TODO: 指令清單
    }

    @Command(aliases = "hand", desc = "Show banner info of the banner in hand")
    @Require("BannerMaker.hand")
    public void hand(Plugin plugin, CommandSender sender) throws CommandException {
        BannerMaker bm = (BannerMaker) plugin;
        //限玩家使用
        if (!(sender instanceof Player)) {
            throw new CommandException(tl("command.player-only"));
        }
        Player player = (Player) sender;
        ItemStack itemStack = bm.getVersionHandler().getItemInMainHand(player);
        if (!BannerUtil.isBanner(itemStack)) {
            player.sendMessage(MessageUtil.format(true, "&c" + tl("command.not-banner-hand")));
            return;
        }
        //複製旗幟，僅保留底色與樣式
        BannerMeta originalBannerMeta = (BannerMeta) itemStack.getItemMeta();
        KItemStack banner = new KItemStack(Material.BANNER)
            .durability(itemStack.getDurability())
            .setPatterns(originalBannerMeta.getPatterns());
        //顯示旗幟
        InventoryMenuUtil.showBannerInfo(player, banner);
    }

    @Command(aliases = "see", desc = "Show banner info of the banner you're looking at")
    @Require("BannerMaker.see")
    public void see(CommandSender sender) throws CommandException {
        //限玩家使用
        if (!(sender instanceof Player)) {
            throw new CommandException(tl("command.player-only"));
        }
        Player player = (Player) sender;
        Block block = player.getTargetBlock((Set<Material>) null, 20);
        if (block.getType() != Material.STANDING_BANNER && block.getType() != Material.WALL_BANNER) {
            player.sendMessage(MessageUtil.format(true, "&c" + tl("command.not-banner-see")));
            return;
        }
        //根據方塊建立旗幟
        Banner blockState = (Banner) block.getState();
        KItemStack banner = new KItemStack(Material.BANNER)
            .durability(DyeColorUtil.toShort(blockState.getBaseColor()))
            .setPatterns(blockState.getPatterns());
        //顯示旗幟
        InventoryMenuUtil.showBannerInfo(player, banner);
    }

    @Command(aliases = "reload", desc = "Reload all config")
    @Require("BannerMaker.reload")
    public void reload(Plugin plugin, CommandSender sender) {
        BannerMaker bm = (BannerMaker) plugin;
        bm.reload();
        sender.sendMessage(MessageUtil.format(true, tl("general.reload")));
    }
}
