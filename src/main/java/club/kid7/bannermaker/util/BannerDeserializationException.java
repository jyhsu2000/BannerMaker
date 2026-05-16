package club.kid7.bannermaker.util;

/**
 * 旗幟反序列化失敗時拋出的例外基底。
 * <p>
 * 子類別代表不同失敗原因（格式損壞、未知 pattern、未知 color），方便未來 API caller
 * 給予結構化錯誤回應。本插件的 {@code /bm view} 指令對玩家仍回傳通用「無效旗幟字串」
 * 訊息，因為刻意不向使用者揭露 wire format 細節。
 */
public class BannerDeserializationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BannerDeserializationException(String message) {
        super(message);
    }

    public BannerDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
