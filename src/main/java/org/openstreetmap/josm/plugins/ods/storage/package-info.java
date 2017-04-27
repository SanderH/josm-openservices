/**
 * This packed contains the storage system.
 * This storage system is intended to be independent of this
 * openstreetmap plug-in, so it can be used for a broader
 * range of applications.
 * 
 * The main class of the storage is Repository.class. The repository
 * contains an ObjectStore for every Class of which at least one
 * object has been added to the repository. As well as for each of
 * their superclasses/interfaces.
 * Added objects are stored in the ObjectStore of their class. This implies
 * that object stores for interfaces and abstract classes don't contain
 * any objects. They do however hold references to every subclass and
 * can have one or more indexes. This makes it possible to do index-
 * based queries for all objects that implement a certain interface or
 * extend a certain class.
 *
 * @author Gertjan Idema
 *
 */
package org.openstreetmap.josm.plugins.ods.storage;