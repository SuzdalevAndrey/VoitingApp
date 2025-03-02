package ru.andreyszdlv.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Getter
public class ServerConfiguration {

    private int port;

    public ServerConfiguration(String propertiesFileName) {
        Properties properties = new Properties();
        log.info("Loading configuration from file: {}", propertiesFileName);
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFileName)) {
            if (input == null) {
                log.error("Configuration file '{}' not found", propertiesFileName);
                return;
            }
            properties.load(input);
            this.port = Integer.parseInt(properties.getProperty("server.port"));
            log.info("Successfully loaded server configuration. Port: {}", this.port);
        } catch (Exception e) {
            log.error("Error reading configuration file \"{}\": {}", propertiesFileName, e.getMessage());
        }
    }
}