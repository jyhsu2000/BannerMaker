package club.kid7.bannermaker.util;

/**
 * 旗幟字串結構不符預期（base64 解碼失敗、缺少分隔符、欄位個數錯誤、版本號不支援等）。
 */
public class InvalidBannerFormatException extends BannerDeserializationException {
    private static final long serialVersionUID = 1L;

    public InvalidBannerFormatException(String message) {
        super(message);
    }

    public InvalidBannerFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
