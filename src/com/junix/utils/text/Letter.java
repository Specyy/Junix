/**
 * 
 */
package com.junix.utils.text;

/**
 * A <code>Letter</code> is a class that represents a character. This class
 * allows for that character to be modified in multiple ways (such as changing
 * the letter-case, changing the value of a character, comparing it to other
 * characters, etc.).
 * <p>
 * Note that this class is {@code java.io.Serializable} so that the contents of
 * this class can get cached and serialized to keep consistency. This will also
 * allow safe transportation through threads and servers. It is also
 * {@code Comparable} for the ease of comparing two different Letters. You can
 * easily discover whether one {@code Letter} is greater or less than another.
 * <p>
 * Also note that this class can only represents up to <code>65,535</code>
 * different letters (BMP characters) and does not support supplementary
 * characters.
 */
public class Letter implements java.io.Serializable, Comparable<Object> {
	
	/**
	 * Serial UID (Unique ID) for this class (not for each instance).
	 */
	private static final long serialVersionUID = 2692851334770425613L;
	
	/**
	 * The value of this {@code Letter}.
	 */
	private char value = 0x0;
	
	/**
	 * Creates a {@code Letter} assigning it the byte value of {@code value}.
	 * 
	 * @param value The {@code byte} value of this {@code Letter}.
	 */
	public Letter(byte value) {
		this(Math.abs(value));
	}
	
	/**
	 * Creates a {@code Letter} assigning it the int value of {@code value}. Note
	 * that this value must be below {@code 0xFFFF} (65535) or it will not be able
	 * to be cast to a {@code char}.
	 * 
	 * @param value The {@code byte} value of this {@code Letter}.
	 */
	public Letter(int ch) {
		this((char) ch);
	}
	
	/**
	 * Creates a {@code Letter} assigning it the value of {@code ch}.
	 * 
	 * @param ch The value of this {@code Letter}.
	 */
	public Letter(char ch) {
		this.value = ch;
	}
	
	/**
	 * Forms an array of Letters from the content of {@code chars}. These
	 * {@code Letters} will have the same content as the {@code chars} in their
	 * values.
	 * 
	 * @param chars The original chars that will be transformed into a
	 *              {@code Letter}.
	 * @return An array of Letters with the corresponding values.
	 */
	static Letter[] convert(char... chars) {
		Letter[] letters = new Letter[chars.length];
		
		for (int i = 0; i < chars.length; i++) {
			letters[i] = new Letter(chars[i]);
		}
		
		return letters;
	}
	
	/**
	 * Forms an array of characters from the content of {@code letters}. These
	 * characters will have the same content as {@code letters} in their values.
	 * 
	 * @param chars The original chars that will be transformed into a
	 *              {@code Letter}.
	 * @return An array of Letters with the corresponding values.
	 */
	static char[] convert(Letter... letters) {
		char[] chars = new char[letters.length];
		
		for (int i = 0; i < letters.length; i++) {
			chars[i] = letters[i].getValue();
		}
		
		return chars;
	}
	
	/**
	 * Sets the value of this {@code Letter} to the given {@code ch}.
	 * 
	 * @param ch The value of this {@code Letter} as {@code char}.
	 */
	public void setValue(char ch) {
		value = ch;
	}
	
	/**
	 * Sets the value of this {@code Letter} to the given {@code ch}.
	 * 
	 * @param ch The value of this {@code Letter} as {@code byte}.
	 */
	public void setValue(byte ch) {
		setValue((char) ch);
	}
	
	/**
	 * Sets the value of this {@code Letter} to the given {@code ch} if the sequence
	 * is one character in length.
	 * 
	 * @param ch The value of this {@code Letter} as {@code CharSequence}. It will
	 *           only be used if it is one character in length.
	 */
	public void setValue(CharSequence ch) {
		if (ch.length() > 1)
			return;
		setValue(ch, 0);
	}
	
	/**
	 * Sets the value of this {@code Letter} to the {@code n}th character of the
	 * given sequence.
	 * 
	 * @param ch    The sequence to use.
	 * @param index The index of the character in the sequence to use.
	 */
	public void setValue(CharSequence ch, int index) {
		value = ch.charAt(index);
	}
	
	/**
	 * Returns the value of this {@code Letter} as a {@code char}. This method is
	 * equivalent to {@link #toChar()}.
	 * 
	 * @return The value of this {@code Letter} as a {@code char}.
	 */
	public char getValue() {
		return toChar();
	}
	
	/**
	 * Returns the value of this {@code Letter} as a {@code char}.
	 * 
	 * @return The value of this {@code Letter} as a {@code char}.
	 */
	public char toChar() {
		return value;
	}
	
	/**
	 * Returns the value of this {@code Letter} as a piece of {@code Text}.
	 * 
	 * @return The value of this {@code Letter} as a piece of {@code Text}.
	 */
	public Text toText() {
		return new Text(toString());
	}
	
	/**
	 * Returns the value of this {@code Letter} as a {@code String}.
	 */
	@Override
	public String toString() {
		return "" + value;
	}
	
	/**
	 * Checks whether this {@code Letter} is equal to the given {@code Type}, in
	 * general terms.
	 * <p>
	 * Note that a {@code Letter} can never be equal to a {@link Text.Type#CLASS}
	 * because multiple letters are needed for that to be true.
	 * 
	 * @param type The type to be checked.
	 * @return {@code true} if this {@code Letter} is the same {@code Type} as the
	 *         given parameter; otherwise {@code false}.
	 */
	public boolean equals(Text.Type type) {
		if (type == Text.Type.CLASS)
			return false;
		return type.equalsAction.test(toString());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return compareTo(obj) == 0 ? true : super.equals(obj);
	}
	
	/**
	 * Specials cases are used if the {@code obj} is a {@link Text}, a
	 * {@link CharSequence}, a {@link Character}, a {@link Letter}, a {@code byte},
	 * an {@code int}, a {@code char[]}, or a {@code byte[]}.
	 */
	@Override
	public int compareTo(Object obj) {
		if (obj instanceof Text) {
			return ((Text) obj).length() > 1 ? -1 : ((Text) obj).compareTo(this);
		} else if (obj instanceof CharSequence) {
			obj.toString().compareTo(toString());
		} else if (obj instanceof Letter) {
			return Character.compare(value, ((Letter) obj).value);
		} else if (obj instanceof Character) {
			return Character.compare(value, (char) obj);
		} else if (obj instanceof char[]) {
			return ((char[]) obj).length > 1 ? -1 : toString().compareTo(new String((char[]) obj));
		} else if (obj instanceof byte[]) {
			return ((byte[]) obj).length > 1 ? -1 : toString().compareTo(new String((byte[]) obj));
		} else if (obj instanceof Byte) {
			return toString().compareTo(new String(new byte[] { (byte) obj }));
		} else if (obj instanceof Integer) {
			try {
				return compareTo((char) obj);
			} catch (Exception e) {
				return value > (int) obj ? 1 : -1;
			}
		}
		
		return toString().compareTo(obj.toString());
	}
}
