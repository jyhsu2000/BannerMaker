package club.kid7.bannermaker.util;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class SerializationUtil {
    /**
     * A method to serialize one object to Base64 String.
     *
     * @param object to turn into a Base64 String.
     * @return Base64 string of the object.
     * @throws IllegalStateException if the item cannot be serialized.
     */
    public static String objectToBase64(Object object) throws IllegalStateException {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(object);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to serialize item stack.", e);
        }
    }

    /**
     * Gets one object from Base64 string.
     *
     * @param data Base64 string to convert to object.
     * @return object created from the Base64 string.
     * @throws IOException if unable to decode the Base64 string.
     */
    @SuppressWarnings("unchecked")
    public static <T> T objectFromBase64(String data) throws IOException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
        try (final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (T) dataInput.readObject();
        } catch (final ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
