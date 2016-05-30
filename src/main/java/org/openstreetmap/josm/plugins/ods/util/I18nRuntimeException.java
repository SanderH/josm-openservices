package org.openstreetmap.josm.plugins.ods.util;

import java.util.Locale;
import java.util.ResourceBundle;

@SuppressWarnings("serial")
public class I18nRuntimeException extends Exception {
    private ResourceBundle rootResourceBundle;
    private ResourceBundle localResourceBundle;
    private String key;
    private Object[] args;
    
    public I18nRuntimeException(String resourceBundle, String key, Object[] args) {
        this(resourceBundle, key, args, null);
    }
    
    public I18nRuntimeException(String baseName, String key, Object[] args, Throwable cause) {
        super(cause);
        this.rootResourceBundle = ResourceBundle.getBundle(baseName, Locale.ROOT);
        this.localResourceBundle = ResourceBundle.getBundle(baseName);
        this.key = key;
        this.args = args;
    }

    @Override
    public String getMessage() {
        String msg = rootResourceBundle.getString(key);
        return String.format(msg, args);
    }

    @Override
    public String getLocalizedMessage() {
        String msg = localResourceBundle.getString(key);
        return String.format(msg, args);
    }
}
