package club.kid7.bannermaker.command.acf;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.gui.MainMenuGUI;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;

@CommandAlias("bm|bannermaker")
public class BannerMakerCommand extends BaseCommand {

    private final BannerMaker plugin;

    public BannerMakerCommand(BannerMaker plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("{@@command.description.default}")
    @CommandPermission("BannerMaker.use")
    public void onDefault(Player player) {
        // 開啟選單
        // TODO: (GUI 遷移) 未來若有需要，可考慮整合 PlayerData 中的頁碼記憶功能。
        MainMenuGUI.show(player);
    }

    @HelpCommand
    @Description("{@@command.description.help}")
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("BannerMaker.reload")
    @Description("{@@command.description.reload}")
    public void onReload(CommandSender sender) {
        plugin.reload();
        plugin.getMessageService().send(sender, tl(NamedTextColor.GREEN, "general.reload"));
    }

    @Subcommand("see")
    @CommandPermission("BannerMaker.see")
    @Description("{@@command.description.see}")
    public void onSee(Player player) {
        Block block = player.getTargetBlockExact(20);
        if (block == null || !BannerUtil.isBanner(block.getType())) {
            plugin.getMessageService().send(player, tl(NamedTextColor.RED, "command.not-banner-see"));
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
    @Description("{@@command.description.hand}")
    public void onHand(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!BannerUtil.isBanner(itemStack)) {
            plugin.getMessageService().send(player, tl(NamedTextColor.RED, "command.not-banner-hand"));
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
    @Description("{@@command.description.view}")
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
