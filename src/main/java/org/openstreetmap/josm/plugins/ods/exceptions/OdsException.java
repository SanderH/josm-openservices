package org.openstreetmap.josm.plugins.ods.exceptions;

import java.util.List;

import org.openstreetmap.josm.tools.I18n;

public class OdsException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsException(String message) {
        super(message);
    }

    public OdsException(Throwable cause) {
        super(cause);
    }

    public OdsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OdsException(String mainMessage, List<String> childMessages) {
        super(buildMessage(mainMessage, childMessages));
    }
    
    /**
     * Create a new instance from a list of messages.
     * Best practice is to log the originating exceptions in the calling method, so we can find
     * 
     * @param messages
     */
    public OdsException(List<String> messages) {
        super(buildMessage(messages));
    }

    /**
     * Create a single string message from a list of messages
     * 
     * @param messages
     * @return The first message, if there is only 1 messages.
     *  Otherwise: Each message on a separate line. Prefixed with an extra line.
     */
    private static String buildMessage(List<String> messages) {
        if (messages.size() == 1) {
            return new String(messages.get(0));
        }
        return buildMessage(I18n.tr("Multiple issues:"), messages);
    }
    
    /**
     * Create a single String message from a list.
     * @param prefix
     * @param messages
     * @return
     */
    private static String buildMessage(String prefix, List<String> messages) {
        return prefix + "\n" + String.join("\n", messages);
    }

}
