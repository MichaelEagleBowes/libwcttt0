/**
 * <p>Contains the data model.</p>
 *
 * <p>{@link wcttt.lib.model.Semester} is the central element of the model. Its
 * implementation {@link wcttt.lib.model.SemesterImpl} also represents the root
 * element of the XML files. Inheritance appears in two places:</p>
 *
 * <p>1. Abstract class {@link wcttt.lib.model.Room} is the superclass of
 * {@link wcttt.lib.model.InternalRoom} and {@link wcttt.lib.model.ExternalRoom}.
 * </p>
 * <p>2. Abstract class {@link wcttt.lib.model.Session} is the superclass of
 * {@link wcttt.lib.model.InternalSession} and {@link wcttt.lib.model.ExternalSession}.
 * </p>
 */
package wcttt.lib.model;
