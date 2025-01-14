/**
 * UUID Package
 */
package com.junix.utils.unique;

import java.util.UUID;

/**
 * <p>
 * The {@link Identifiable} class is meant to be able to identify different
 * classes, or different instances of a class. Each {@link Identifiable} child
 * will have a unique identifier from the {@link UniqueID} class that will
 * identify that certain child. To access that identifier, you must call the
 * {@link #uniqueIdentifier()} method. This method will return the identifier
 * assigned to that {@link Identifiable} child.
 */
public interface Identifiable extends java.io.Serializable {
	
	/**
	 * <p>
	 * This method should return the unique identifier of each child that implements
	 * this interface. Each {@link Identifiable} child should have their own form of
	 * implementation for this method. What I would recommend doing, is creating a
	 * field for the {@link UniqueID unique identifier} of the child class, and
	 * using that as the identifier returned in this method. Also, I would recommend
	 * using the {@link Object#hashCode() hashCode()} of the class so that the
	 * unique identifier will be different for each instance of the class. If you
	 * want a consistent identifier, you do not have to do so. It should look
	 * something like this:
	 * </p>
	 * 
	 * <pre>
	 * 	private {@link UniqueID} id = new {@link UniqueID}({@link System#identityHashCode(Object) System.identityHashCode(this)});
	 * 
	 * 	public {@link UniqueID} {@link #uniqueIdentifier()} {
	 * 		return id;
	 * 	}
	 * </pre>
	 * 
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * 	private {@link UniqueID} id = new {@link UniqueID}({@link Class#getName() getClass().getName()});
	 * 
	 * 	public {@link UniqueID} {@link #uniqueIdentifier()} {
	 * 		return id;
	 * 	}
	 * </pre>
	 * 
	 * <p>
	 * I only suggest doing this if you want the identifier to stay consistent every
	 * call. If you do not want to do all this work, you may let the
	 * {@link UniqueID} class generator a unique identifier that you can use for
	 * your classes by using it's empty constructor.
	 * 
	 * @see UniqueID
	 * @return The unique identifier for the class that implements this interface.
	 */
	UniqueID uniqueIdentifier();
	
	/**
	 * Compares this {@link #uniqueIdentifier()} to the specified object.
	 * <p>
	 * The given object must be either a {@link UUID} string, a {@link UniqueID}, or
	 * a {@link Identifiable} class to comparable. If it is neither of these, this
	 * method will return -2, which signifies "null" or "is not qualified".
	 * Although, if it is one of those, this method will return -1 if this
	 * {@link #uniqueIdentifier()} is less than the given object's identifier, 0 if
	 * it is equal, and 1 if it greater.
	 *
	 * @param obj Object to which this {@link #uniqueIdentifier()} is to be
	 *            compared.
	 *
	 * @return -2, -1, 0 or 1 as this {@link #uniqueIdentifier()} is "null", less
	 *         than, equal to, or greater than {@code obj}.
	 * 		
	 */
	default int compareTo(Object obj) {
		if (obj instanceof String) {
			String objString = (String) obj;
			UUID toUUID;
			
			if (uniqueIdentifier().isRaw())
				toUUID = UUID.nameUUIDFromBytes(objString.getBytes());
			else
				try {
					toUUID = UUID.fromString(objString);
				} catch (Exception e) {
					return -2;
				}
			
			return compareTo(toUUID);
		} else if (obj instanceof UniqueID) {
			return compareTo(((UniqueID) obj).toUUID());
		} else if (obj instanceof Identifiable) {
			return compareTo(((Identifiable) obj).uniqueIdentifier());
		} else if (obj instanceof UUID) {
			return uniqueIdentifier().toUUID().compareTo((UUID) obj);
		}
		
		return -2;
	}
}
