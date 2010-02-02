package com.mysticcoders.mysticpaste.persistence.hibernate;

/**
 * @author Andrew Lombardi
 * @version $Revision$ $Date$
 */
public class EntityNotFoundException extends javax.persistence.EntityNotFoundException {

    public EntityNotFoundException(Class clazz, Long id) {
        super("An object of type " + clazz + " with ID " + id + " does not exist.");
    }

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String arg0) {
        super(arg0);
    }
}