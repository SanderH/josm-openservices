/**
 * Extensions to Josm primitives.
 * Because the Josm primitives are final classes, we need our own classes
 * to achieve this and a HashMap to find the corresponding Ods primitive for
 * a josm primitive;
 * ODS primitives are created for all objects that are relevant for the
 * current ODS module and for their member primitives.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
package org.openstreetmap.josm.plugins.ods.primitives;