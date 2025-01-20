package com.cjron128.chatbot.config;

import java.util.logging.*;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Config {

    private Config() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger LOGGER = Logger.getLogger( Config.class.getName() );
    private static final PropertiesConfiguration configFile = new PropertiesConfiguration();

    static {
        Configurations configurations = new Configurations();
        try {
            // Load the properties file from the resources directory
            configFile.copy(configurations.properties("config.properties"));
        } catch (ConfigurationException e) {
            LOGGER.log(Level.FINE, "Failed to load configuration file: ", e);
            System.exit(1);

        }
    }

    // Public static variables to access configuration values
    public static final String API_KEY = configFile.getString("OPENAI_API_KEY");

}
