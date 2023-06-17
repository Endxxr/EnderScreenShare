package dev.endxxr.enderss.common.utils;

import dev.endxxr.enderss.api.EnderSSProvider;
import lombok.SneakyThrows;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {

    @SneakyThrows
    public static YamlFile saveConfig(String fileName) {
        String folderPath = System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "EnderSS";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            Files.createDirectory(folder.toPath());
        }
        File file = new File(folder, fileName);
        if (!file.exists()) {
            try (InputStream in = FileUtils.class.getClassLoader().getResourceAsStream(fileName)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                LogUtils.prettyPrintException(e, "Could not save "+fileName+" to "+ file);
            }
        }

        YamlFile yamlFile = new YamlFile(file.getAbsolutePath());
        yamlFile.load();
        return yamlFile;
    }

    public static void updateFromLegacyConfig(YamlFile oldConfig) {

        File file = oldConfig.getConfigurationFile();
        try {
            Files.move(file.toPath(), new File(file.getParent(), "config.yml.old").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LogUtils.prettyPrintException(e, "There was an error while updating the configuration from the legacy version.");
        }

        FileUtils.saveConfig("config.yml");
        EnderSSProvider.getApi().getPlugin().getLog().warning("The plugin has updated the configuration file to the new format.");

    }

}
