package com.cjron128.chatbot.config;

import java.util.Properties;
import java.util.logging.*;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;


import java.sql.*;

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

    // Database connection values
    public static final String DB_URL = configFile.getString("DB_URL");
    public static final String DB_USER = configFile.getString("DB_USER");
    public static final String DB_PASSWORD = configFile.getString("DB_PASSWORD");


    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
