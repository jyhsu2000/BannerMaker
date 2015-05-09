package tw.kid7.BannerMaker.configuration;

import com.google.common.collect.Maps;
import org.bukkit.configuration.file.FileConfiguration;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.*;

public class DefaultConfig {
    HashMap<String, HashMap<String, Object>> defaultConfigs = Maps.newHashMap();

    public DefaultConfig() {
        //設定檔的預設值
        HashMap<String, Object> defaultOptions = Maps.newHashMap();
        defaultOptions.put("Language", "en");
        defaultOptions.put("Economy.Enable", true);
        defaultOptions.put("Economy.Price", 100);
        defaultConfigs.put("config", defaultOptions);
    }

    public void checkConfig() {
        for (String configName : defaultConfigs.keySet()) {
            String configFileName = configName + ".yml";
            FileConfiguration config = ConfigManager.get(configFileName);
            HashMap<String, Object> defaultOptions = defaultConfigs.get(configName);

            int i = 0;
            for (Map.Entry<String, Object> entry : defaultOptions.entrySet()) {
                if (!config.contains(entry.getKey())) {
                    config.set(entry.getKey(), entry.getValue());
                    i++;
                }
            }
            if (i > 0) {
                ConfigManager.save(configFileName);
                BannerMaker.getInstance().getServer().getConsoleSender().sendMessage(MessageUtil.format(true, Language.get("config.add-setting", i)));
            }
        }
    }
}
