package club.kid7.bannermaker.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Objects;

/*
 * 無框字母
 * https://www.youtube.com/watch?v=PncIlxt8TtI
 * 有框字母
 * https://www.youtube.com/watch?v=jLYvPD_vbZg
 * 數字＆問號＆驚嘆號＆句號
 * https://www.youtube.com/watch?v=xODU7WWTXYE
 */
public class AlphabetBanner {

    private final String alphabet;
    public DyeColor baseColor;
    public DyeColor dyeColor;
    public boolean bordered;

    /*
     * 建構子
     */
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

    public static ItemStack get(String alphabet, DyeColor baseColor, DyeColor dyeColor, boolean bordered) {
        AlphabetBanner alphabetBanner = new AlphabetBanner(alphabet, baseColor, dyeColor, bordered);
        return alphabetBanner.toItemStack();
    }

    /*
     * 建立ItemStack
     */
    public ItemStack toItemStack() {
        //建立旗幟
        ItemStack banner = new ItemStack(DyeColorUtil.toBannerMaterial(baseColor));
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        Objects.requireNonNull(bannerMeta).setDisplayName(MessageUtil.format("&a" + alphabet));
        boolean invertBanner = false;
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
                case "B":
                case "8":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    break;
                case "C":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    break;
                case "D":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.RHOMBUS_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    break;
                case "E":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    break;
                case "F":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    break;
                case "G":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    break;
                case "H":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    break;
                case "I":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_CENTER));
                    break;
                case "J":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    break;
                case "K":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    break;
                case "L":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    break;
                case "M":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.TRIANGLE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.TRIANGLES_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    break;
                case "N":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.TRIANGLE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    break;
                case "O":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    break;
                case "P":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    break;
                case "Q":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    break;
                case "R":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_VERTICAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    break;
                case "S":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.TRIANGLE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.TRIANGLE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_TOP_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.RHOMBUS_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    break;
                case "T":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_CENTER));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    break;
                case "U":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    break;
                case "V":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.TRIANGLES_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    break;
                case "W":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.TRIANGLE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.TRIANGLES_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    break;
                case "X":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    break;
                case "Y":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    break;
                case "Z":
                case "2":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.TRIANGLE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.TRIANGLE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_TOP_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.RHOMBUS_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    break;
                case "1":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_TOP_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_CENTER));
                    break;
                case "3":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    break;
                case "4":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    break;
                case "5":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_VERTICAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.DIAGONAL_RIGHT_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    break;
                case "6":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    break;
                case "7":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.DIAGONAL_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    break;
                case "9":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    break;
                case "0":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    break;
                case "?":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    break;
                case "!":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_VERTICAL_MIRROR));
                    break;
                case ".":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    break;
            }

        } else {
            //有框
            switch (alphabet) {
                case "A":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "B":
                case "8":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_CENTER));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "C":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "D":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.CURLY_BORDER));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "E":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "F":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "G":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "H":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "I":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_CENTER));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "J":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "K":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_VERTICAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.CROSS));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "L":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "M":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.TRIANGLE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.TRIANGLES_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "N":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.DIAGONAL_RIGHT_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "O":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "P":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "Q":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_RIGHT));
                    break;
                case "R":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "S":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.RHOMBUS_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.CURLY_BORDER));
                    break;
                case "T":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_CENTER));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "U":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "V":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.TRIANGLES_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "W":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.TRIANGLE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.TRIANGLES_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "X":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_CENTER));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.CROSS));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "Y":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.CROSS));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_VERTICAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "Z":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "1":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_TOP_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_CENTER));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "2":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.RHOMBUS_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "3":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "4":
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "5":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNRIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.CURLY_BORDER));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "6":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "7":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.DIAGONAL_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "9":
                    invertBanner = true;
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "0":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_LEFT));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_DOWNLEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "?":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_RIGHT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_HORIZONTAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case "!":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.HALF_HORIZONTAL));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.STRIPE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.HALF_VERTICAL_MIRROR));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
                case ".":
                    bannerMeta.addPattern(new Pattern(dyeColor, PatternType.SQUARE_BOTTOM_LEFT));
                    bannerMeta.addPattern(new Pattern(baseColor, PatternType.BORDER));
                    break;
            }
        }
        if (invertBanner) {
//            bannerMeta.setBaseColor(dyeColor);
            banner = new ItemStack(DyeColorUtil.toBannerMaterial(dyeColor));
        }
        banner.setItemMeta(bannerMeta);
        return banner;
    }
}
