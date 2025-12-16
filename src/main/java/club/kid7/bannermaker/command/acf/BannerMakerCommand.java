package club.kid7.bannermaker.command.acf;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.Language;
import club.kid7.bannermaker.gui.MainMenuGUI;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Objects;

@CommandAlias("bannermaker|bm")
public class BannerMakerCommand extends BaseCommand {

    private final BannerMaker plugin;

    public BannerMakerCommand(BannerMaker plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Show menu of BannerMaker")
    @CommandPermission("BannerMaker.use")
    public void onDefault(Player player) {
        // 開啟選單
        // TODO: (GUI 遷移) 未來若有需要，可考慮整合 PlayerData 中的頁碼記憶功能。
        MainMenuGUI.show(player);
    }

    @Subcommand("help")
    @Description("Show help message")
    public void onHelp(CommandSender sender) {
        // 顯示插件資訊
        String pluginName = plugin.getName();
        String pluginVersion = plugin.getDescription().getVersion();
        plugin.getMessageService().send(sender, "&e" + pluginName + " - " + pluginVersion);

        // 顯示指令列表 (手動列表，或後續整合 ACF Help)
        plugin.getMessageService().send(sender, "&b/bm &7- Open main menu");
        plugin.getMessageService().send(sender, "&b/bm help &7- Show command list");
        plugin.getMessageService().send(sender, "&b/bm reload &7- Reload config");
        // TODO: 補完其他指令的幫助訊息
    }

    @Subcommand("reload")
    @CommandPermission("BannerMaker.reload")
    @Description("Reload all config")
    public void onReload(CommandSender sender) {
        plugin.reload();
        plugin.getMessageService().send(sender, "&aConfiguration reloaded.");
    }

    @Subcommand("see")
    @CommandPermission("BannerMaker.see")
    @Description("Show banner info of the banner you're looking at")
    public void onSee(Player player) {
        Block block = player.getTargetBlockExact(20);
        if (block == null || !block.getType().name().endsWith("_BANNER")) {
            plugin.getMessageService().send(player, Component.empty().color(NamedTextColor.RED).append(Language.tl("command.not-banner-see")));
            return;
        }
        //根據方塊建立旗幟
        Banner blockState = (Banner) block.getState();
        ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(blockState.getBaseColor()))
            .setPatterns(blockState.getPatterns()).build();
        //顯示旗幟
        InventoryMenuUtil.openBannerInfo(player, banner);
    }

    @Subcommand("hand")
    @CommandPermission("BannerMaker.hand")
    @Description("Show banner info of the banner in hand")
    public void onHand(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!BannerUtil.isBanner(itemStack)) {
            plugin.getMessageService().send(player, Component.empty().color(NamedTextColor.RED).append(Language.tl("command.not-banner-hand")));
            return;
        }
        //複製旗幟，僅保留底色與樣式
        BannerMeta originalBannerMeta = (BannerMeta) itemStack.getItemMeta();
        ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(itemStack.getType()))
            .setPatterns(Objects.requireNonNull(originalBannerMeta).getPatterns()).build();
        //顯示旗幟
        InventoryMenuUtil.openBannerInfo(player, banner);
    }

    @Subcommand("view")
    @CommandPermission("BannerMaker.view")
    @Description("View banner info of the banner string")
    @Syntax("<bannerString>")
    public void onView(Player player, String bannerString) {
        try {
            ItemStack banner = BannerUtil.deserialize(bannerString);
            //顯示旗幟
            InventoryMenuUtil.openBannerInfo(player, banner);
        } catch (Exception e) {
            plugin.getMessageService().send(player, "&cInvalid banner string");
        }
    }
}
