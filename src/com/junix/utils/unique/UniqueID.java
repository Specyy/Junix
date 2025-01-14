/**
 * UUID Package
 */
package com.junix.utils.unique;

import java.util.UUID;

/**
 * <p>
 * The class {@link UniqueID} can be used to create a unique identifier for each
 * class or each instance of a certain class. This class should not be used on
 * it's own and is usually used in conjunction with the {@link Identifiable}
 * class.
 * 
 * <p>
 * The same {@link UniqueID} can be generated multiple times by using the same
 * piece of text as the content. For example, if enter "newId" as a name and
 * "true" as the boolean (using the {@link #UniqueID(String, boolean)}
 * constructor), it will generate the same unique identifier every single time.
 */
public final class UniqueID implements java.io.Serializable {
	
	/**
	 * Serial version UID. Ignore this.
	 */
	private static final long serialVersionUID = -7180343886288836053L;
	
	/**
	 * The unique identifier for this class. It is volatile to ensure consistency
	 * between threads and cores.
	 */
	private volatile UUID uniqueID;
	
	/**
	 * Whether the unique identifier was parsed by from text.
	 * 
	 * @see #UniqueID(String, boolean)
	 */
	private boolean raw = false;
	
	/**
	 * <p>
	 * The string that was used to separate each region of text in a unique
	 * identifier. These separation can only be special characters (such as
	 * "<code>~!@#$%^&*()_+{}|:"<>?`-=[]\;',./</code>").
	 * <p>
	 * Note that these separations do not affect the identity of this
	 * {@code UniqueID} and will still be considered the same as an identical
	 * {@code UniqueID} but with a different separation.
	 */
	private char oldSeparation = (char) 0;
	
	/**
	 * <p>
	 * The string that will be used to separate each region of text in a unique
	 * identifier. These separation can only be special characters (such as
	 * "<code>~!@#$%^&*()_+{}|:"<>?`-=[]\;',./</code>").
	 * <p>
	 * Note that these separations do not affect the identity of this
	 * {@code UniqueID} and will still be considered the same as an identical
	 * {@code UniqueID} but with a different separation.
	 */
	private char separation = '-';
	
	/**
	 * Generates a random unique identifier that will be stored in this class. There
	 * is a possibility two identifiers might be the same although, that is a
	 * tremendously small chance.
	 */
	public UniqueID() {
		uniqueID = UUID.randomUUID();
	}
	
	/**
	 * Generates a unique identifier that derives from the given name. This name
	 * must be a valid unique identifier or an error will be thrown. See
	 * {@link UUID#fromString(String)} for more information.
	 * 
	 * @param name The name which the identifier should derive from.
	 * @see UUID#fromString(String)
	 */
	public UniqueID(String name) {
		this(name, false);
	}
	
	/**
	 * Generates a unique identifier that derives from the given name. With the
	 * {@code raw} boolean set to false, this name will have to be a valid unique
	 * identifier or an error will be thrown. On the other hand, if it is true, no
	 * error will be thrown, no matter the format. See
	 * {@link UUID#fromString(String)} for more information.
	 * 
	 * @param name The name which the identifier should derive from.
	 * @param raw  Whether the name should not be validated or not.
	 * @see UUID#fromString(String)
	 */
	public UniqueID(String name, boolean raw) {
		if (raw)
			uniqueID = UUID.nameUUIDFromBytes(name.getBytes());
		else
			uniqueID = UUID.fromString(name);
		
		this.raw = raw;
	}
	
	/**
	 * Constructs a new {@link UniqueID} using the specified data.
	 * {@code mostSigBits} is used for the most significant 64 bits of the
	 * {@link UniqueID} and {@code leastSigBits} becomes the least significant 64
	 * bits of the {@link UniqueID}.
	 *
	 * @param mostSigBits  The most significant bits of the {@link UniqueID}
	 *
	 * @param leastSigBits The least significant bits of the {@link UniqueID}
	 * @see UUID
	 */
	public UniqueID(long leastSigBits, long mostSigBits) {
		uniqueID = new UUID(mostSigBits, leastSigBits);
	}
	
	/**
	 * Static factory to retrieve a type 3 (name based) {@link UniqueID} based on
	 * the specified byte array.
	 *
	 * @param name A byte array to be used to construct a {@link UniqueID}
	 * @see UUID
	 */
	public UniqueID(byte[] name) {
		uniqueID = UUID.nameUUIDFromBytes(name);
	}
	
	/**
	 * Returns a {@link String} object representing this {@code UniqueID}.
	 *
	 * <p>
	 * The UniqueID string representation is as described by the following:
	 * <blockquote>
	 * 
	 * <pre>
	 * {@code
	 * UUID                   = <time_low> "-" <time_mid> "-"
	 *                          <time_high_and_version> "-"
	 *                          <variant_and_sequence> "-"
	 *                          <node>
	 * time_low               = 4*<hexOctet>
	 * time_mid               = 2*<hexOctet>
	 * time_high_and_version  = 2*<hexOctet>
	 * variant_and_sequence   = 2*<hexOctet>
	 * node                   = 6*<hexOctet>
	 * hexOctet               = <hexDigit><hexDigit>
	 * hexDigit               =
	 *       "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
	 *       | "a" | "b" | "c" | "d" | "e" | "f"
	 *       | "A" | "B" | "C" | "D" | "E" | "F"
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 *
	 * @return A string representation of this {@link UniqueID}
	 */
	public String getUniqueID() {
		return uniqueID.toString().replace(oldSeparation, separation);
	}
	
	/**
	 * Sets the string that will be used to separate each region of text in a unique
	 * identifier. These separation can only be special characters (such as
	 * "<code>~!@#$%^&*()_+{}|:"<>?`-=[]\;',./</code>").
	 * <p>
	 * Note that these separations do not affect the identity of this
	 * {@code UniqueID} and will still be considered the same as another
	 * {@code UniqueID} but with a different separation.
	 * 
	 * @param sep The character that will be used to separate each region of text.
	 * @throws IllegalArgumentException If the desired separation is not a special
	 *                                  character.
	 */
	public void setSeparation(char sep) {
		if (Character.isLetterOrDigit(sep))
			throw new IllegalArgumentException("UniqueID separation cannot be a letter or a digit; it must be a special character!");
		oldSeparation = separation;
		separation = sep;
	}
	
	/**
	 * Returns the string that is currently being used to separate each region of
	 * text in a unique identifier.
	 * 
	 * @return The string that is currently being used to separate each region of
	 *         text in a unique identifier.
	 */
	public char getSeparation() {
		return separation;
	}
	
	/**
	 * Whether the unique identifier was parsed by from text.
	 * 
	 * @return {@code true} if the unique identifier parsed from raw text;
	 *         {@code false} otherwise.
	 * @see #UniqueID(String, boolean)
	 */
	boolean isRaw() {
		return raw;
	}
	
	/**
	 * Converts this <code>UniqueID</code> to a {@link UUID}.
	 * 
	 * @return The unique identifier as a {@link UUID}.
	 */
	UUID toUUID() {
		return uniqueID;
	}
	
	/**
	 * Returns a the unique identifier for this class as a {@code byte}. It will
	 * usually look like the following as a {@link String}:
	 * <code>XXXXX-XXXXX-XXXXX-XXXXX-XXXXX</code>
	 * 
	 * @return A byte representation of this unique identifier.
	 */
	public byte[] getByteUniqueID() {
		return getUniqueID().getBytes();
	}
	
	/**
	 * Returns a {@link String} object representing this {@code UniqueID}.
	 *
	 * <p>
	 * The UniqueID string representation is as described by the following:
	 * <blockquote>
	 * 
	 * <pre>
	 * {@code
	 * UUID                   = <time_low> "-" <time_mid> "-"
	 *                          <time_high_and_version> "-"
	 *                          <variant_and_sequence> "-"
	 *                          <node>
	 * time_low               = 4*<hexOctet>
	 * time_mid               = 2*<hexOctet>
	 * time_high_and_version  = 2*<hexOctet>
	 * variant_and_sequence   = 2*<hexOctet>
	 * node                   = 6*<hexOctet>
	 * hexOctet               = <hexDigit><hexDigit>
	 * hexDigit               =
	 *       "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
	 *       | "a" | "b" | "c" | "d" | "e" | "f"
	 *       | "A" | "B" | "C" | "D" | "E" | "F"
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 *
	 * @return A string representation of this {@link UniqueID}
	 */
	@Override
	public String toString() {
		return getUniqueID();
	}
}
