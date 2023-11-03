package ltm;

import ltm.exceptions.TestManagerException;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import org.jasypt.properties.EncryptableProperties;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import java.util.logging.Logger;

public class PropertyManager {
    private static final Logger logger = Logger.getLogger(PropertyManager.class.getName());

    private static final ClassLoader loader;
    private static final String PROPERTY_FILE_NAME = "config.properties";

    private static final ThreadLocal<Properties> properties = new ThreadLocal<>();

    static {
        loader = PropertyManager.class.getClassLoader();
    }

    private PropertyManager() {
    }

    private static Properties getProperties() {
        if (properties.get() == null) {
            loadProperties();
        }

        return properties.get();
    }

    public static boolean isPropertiesFilePresent() {
        return getProperties() != null;
    }

    public static String getProperty(String propertyKey) {
        return getProperties().getProperty(propertyKey);
    }

    public static boolean isPropertyPresentAndNotEmpty(String propertyKey) {
        return getProperties().containsKey(propertyKey) && !getProperties().getProperty(propertyKey).isEmpty();
    }

    private static void loadProperties() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        Properties clientProperties = new EncryptableProperties(encryptor);

        if (isResourceLoadable()) {
            InputStream inputStream = PropertyManager.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME);
            try {
                clientProperties.load(inputStream);
            } catch (IOException e) {
                return;
            }

            properties.set(new EncryptableProperties(new StandardPBEStringEncryptor()));
            properties.get().putAll(clientProperties);
        }
    }

    private static boolean isResourceLoadable() {
        return loader.getResourceAsStream(PROPERTY_FILE_NAME) != null;
    }

}