package tw.kid7.BannerMaker.util;

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

    public static ItemStack get(String alphabet) {
        return get(alphabet, DyeColor.WHITE, DyeColor.BLACK, true);
    }

    public static ItemStack get(String alphabet, DyeColor baseColor, DyeColor dyeColor) {
        return get(alphabet, baseColor, dyeColor, true);
    }

    public static ItemStack get(String alphabet, DyeColor baseColor, DyeColor dyeColor, boolean bordered) {
        //建立旗幟
        ItemStack banner = new ItemStack(Material.BANNER, 1, (short) (15 - baseColor.getData()));
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        bannerMeta.setDisplayName(MessageUtil.format("&a" + alphabet));
        //繪製字母
        if (!bordered) {
            //無框
            switch (alphabet) {
                case "A":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    break;
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
            }
        }
        banner.setItemMeta(bannerMeta);
        return banner;
    }
}
