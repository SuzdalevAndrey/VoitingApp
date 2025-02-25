package ru.andreyszdlv;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.Properties;

@Getter
@Setter
public class Config {

    private int port;

    public Config(String propertiesFileName) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFileName)) {
            if (input == null) {
                System.err.println("Файл конфигурации не найден");
                return;
            }
            properties.load(input);
            this.port = Integer.parseInt(properties.getProperty("server.port"));
        } catch (Exception e) {
            System.err.println("Ошибка при чтении файла конфигурации: " + e.getMessage());
        }
    }
}
