package club.kid7.bannermaker.util;

/**
 * 旗幟字串引用了當前 server 不認得的 pattern key。
 * 典型場景：v2 字串在 1.21.2+ server 產生（含新 pattern type），被 1.21.0 server 解析。
 */
public class UnknownBannerPatternException extends BannerDeserializationException {
    private static final long serialVersionUID = 1L;

    public UnknownBannerPatternException(String message) {
        super(message);
    }

    public UnknownBannerPatternException(String message, Throwable cause) {
        super(message, cause);
    }
}
