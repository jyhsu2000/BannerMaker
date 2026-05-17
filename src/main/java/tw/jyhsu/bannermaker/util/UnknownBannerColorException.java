package tw.jyhsu.bannermaker.util;

/**
 * 旗幟字串引用了不存在的 {@link org.bukkit.DyeColor}（v2：色名字面不匹配 enum；
 * v1：數字色碼超出 0-15 範圍）。
 */
public class UnknownBannerColorException extends BannerDeserializationException {
    private static final long serialVersionUID = 1L;

    public UnknownBannerColorException(String message) {
        super(message);
    }

    public UnknownBannerColorException(String message, Throwable cause) {
        super(message, cause);
    }
}
