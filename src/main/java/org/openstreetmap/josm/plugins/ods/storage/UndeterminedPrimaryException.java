package org.openstreetmap.josm.plugins.ods.storage;

public class UndeterminedPrimaryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UndeterminedPrimaryException() {
        super("Error creating a primary index. This is a programming error, so please report a bug");
    }
}
