package org.openstreetmap.josm.plugins.ods.io;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.tools.I18n;

public class Status {
    private boolean failed = false;
    private boolean timedOut = false;
    private boolean cancelled = false;
    private String message = "";
    private Exception exception = null;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setException(Exception exception) {
        this.exception = exception;
        this.failed = true;
    }

    public boolean isSucces() {
        return !(failed || cancelled || timedOut);
    }

    public String getMessage() {
        if (message != null) {
            return message;
        }
        if (exception != null) {
            return exception.getMessage();
        }
        return I18n.tr("An unknown error occurred.");
    }

    public Exception getException() {
        return exception;
    }

    public void clear() {
        this.cancelled = false;
        this.failed = false;
        this.timedOut = false;
        this.exception = null;
        this.message = null;
    }
    
    public static Status getAggregate(List<Status> statusses) {
        List<String> failureMessages = new LinkedList<>();
        List<String> cancelMessages = new LinkedList<>();
        boolean timedOut = false;
        for (Status st : statusses) {
            if (!st.isSucces()) {
                if (st.isFailed()) {
                    failureMessages.add(st.getMessage());
                }
                if (st.isCancelled()) {
                    cancelMessages.add(st.getMessage());
                }
                if (st.isTimedOut()) {
                    timedOut = true;
                }
            }
        }
        Status status = new Status();
        if (!failureMessages.isEmpty()) {
            String message = String.join("\n", failureMessages);
            status.setFailed(true);
            status.setMessage(message);
        }
        else if (!cancelMessages.isEmpty()) {
            String message = String.join("\n", cancelMessages);
            status.setCancelled(true);
            status.setMessage(message);
        }
        else if (timedOut) {
            status.setTimedOut(true);
        }
        return status;
    }
}
