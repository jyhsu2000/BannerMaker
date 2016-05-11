package tw.kid7.BannerMaker.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/*
 * 無框字母
 * https://www.youtube.com/watch?v=jLYvPD_vbZg
 * 有框字母
 * https://www.youtube.com/watch?v=PncIlxt8TtI
 * 數字＆問號＆驚嘆號＆句號
 * https://www.youtube.com/watch?v=xODU7WWTXYE
 */
public class AlphabetBanner {

    public String alphabet;
    public DyeColor baseColor;
    public DyeColor dyeColor;
    public boolean bordered;

    /*
     * 建構子
     */
    public AlphabetBanner(String alphabet) {
        this.alphabet = ChatColor.stripColor(alphabet.toUpperCase()).substring(0, 1);
        this.baseColor = DyeColor.WHITE;
        this.dyeColor = DyeColor.BLACK;
        this.bordered = true;
    }

    public AlphabetBanner(String alphabet, DyeColor baseColor, DyeColor dyeColor) {
        this.alphabet = ChatColor.stripColor(alphabet.toUpperCase()).substring(0, 1);
        this.baseColor = baseColor;
        this.dyeColor = dyeColor;
        this.bordered = true;
    }

    public AlphabetBanner(String alphabet, DyeColor baseColor, DyeColor dyeColor, boolean bordered) {
        this.alphabet = ChatColor.stripColor(alphabet.toUpperCase()).substring(0, 1);
        this.baseColor = baseColor;
        this.dyeColor = dyeColor;
        this.bordered = bordered;
    }

    /*
     * 直接建立並取得旗幟ItemStack
     */
    public static ItemStack get(String alphabet) {
        return get(alphabet, DyeColor.WHITE, DyeColor.BLACK, true);
    }

    public static ItemStack get(String alphabet, DyeColor baseColor, DyeColor dyeColor) {
        return get(alphabet, baseColor, dyeColor, true);
    }

    public static ItemStack get(String alphabet, DyeColor baseColor, DyeColor dyeColor, boolean bordered) {
        AlphabetBanner alphabetBanner = new AlphabetBanner(alphabet, baseColor, dyeColor, bordered);
        return alphabetBanner.toItemStack();
    }

    /*
     * 建立ItemStack
     */
    public ItemStack toItemStack() {
        //建立旗幟
        ItemStack banner = new ItemStack(Material.BANNER, 1, (short) (15 - baseColor.getData()));
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        bannerMeta.setDisplayName(MessageUtil.format("&a" + alphabet));
        //製字母
        if (!bordered) {
            //無框
            switch (alphabet) {
                case "A":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    break;
                //TODO: 完成其餘字母
                //TODO: 完成數字
            }

        } else {
            //有框
            switch (alphabet) {
                case "A":
                    banner = new ItemStack(Material.BANNER, 1, (short) (15 - dyeColor.getData()));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                //TODO: 完成其餘字母
                //TODO: 完成數字
            }
        }
        banner.setItemMeta(bannerMeta);
        return banner;
    }
}
