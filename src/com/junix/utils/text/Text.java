/**
 * Text package 
 */
package com.junix.utils.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * <p>
 * The {@code Text} class represents a piece of text. Texts are not constant and
 * their values can be changed by using the methods in this class.
 * <p>
 * The class {@code Text} includes methods for examining individual Letters of
 * the sequence, for comparing texts, for searching text, for extracting
 * substrings, and for creating a copy of a text with all letters translated to
 * uppercase or to lowercase. Case mapping is based on the Unicode Standard
 * version specified by the {@link java.lang.Character Character} class.
 * <p>
 * This class is {@code java.io.Serializable} so that the contents of this class
 * can get cached and serialized to keep consistency. This will also allow safe
 * transportation through threads and servers. It is also {@code Comparable} for
 * the ease of comparing two different Texts. You can easily discover whether
 * one {@code Text} is greater or less than another. Thirdly, since a piece of
 * {@code Text} is also a sequence of characters, it would only be appropriate
 * if this class becomes a child of the {@code CharSequence}.
 */
public class Text implements java.io.Serializable, Comparable<Object>, CharSequence {
	
	/**
	 * Serial UID (Unique ID) for this class (not for each instance).
	 */
	private static final long serialVersionUID = 1015652313362459191L;
	
	/**
	 * A completely empty piece of {@code Text}.
	 * <p>
	 * Note that the content of this {@code Text} is not meant to be changed. Please
	 * do not change it. The default value is: <br>
	 * <br>
	 * <code>
	 *	public static final Text EMPTY_TEXT = Text.fromBytes(new byte[0]);
	 * </code>
	 */
	public static final Text EMPTY_TEXT = Text.fromBytes(new byte[0]);
	
	/**
	 * Regular expression that represents any type of white-space character.
	 * <p>
	 * Note that the content of this {@code Text} is not meant to be changed. Please
	 * do not change it.
	 */
	private static final Text WHITESPACE = new Text("\\s+");
	
	/**
	 * Regular expression for all the possible {@code ASCII} letters that can be
	 * written.
	 * <p>
	 * Note that the content of this {@code Text} is not meant to be changed. Please
	 * do not change it.
	 */
	private static final Text ASCII_LETTER = new Text("[A-Za-z]");
	
	/**
	 * Regular expression for all the possible {@code ASCII} numbers that can be
	 * written.
	 * <p>
	 * Note that the content of this {@code Text} is not meant to be changed. Please
	 * do not change it.
	 */
	private static final Text ASCII_NUMBER = new Text("[0-9]");
	
	/**
	 * Regular expression for all the possible numbers that can be written,
	 * including decimal numbers.
	 * <p>
	 * Note that the content of this {@code Text} is not meant to be changed. Please
	 * do not change it.
	 */
	private static final Text NUMBER = new Text("[\\-]?[0-9]+([\\.][0-9]+)?([eE][0-9]+)?");
	
	/**
	 * Regular expression for whether a {@code Text} is a boolean. This can be
	 * expressed in many different ways such as {@code true}, {@code false},
	 * {@code 0}, or {@code 1}. All expressions are case-insensitive.
	 * <p>
	 * Note that the content of this {@code Text} is not meant to be changed. Please
	 * do not change it.
	 */
	private static final Text BOOLEAN = new Text("(?i)(true|false|0|1)");
	
	/**
	 * Regular expression for all the possible special character that can be used.
	 * <p>
	 * Note that the content of this {@code Text} is not meant to be changed. Please
	 * do not change it.
	 */
	private static final Text SPECIAL = new Text("[\\p{Punct}-§]");
	
	/**
	 * Regular expression for whether a {@code Text} or {@code String} has a valid
	 * class name as its content. Valid class names are class names that end in
	 * {@code ".class"} and {@code ".java"}.
	 * <p>
	 * Note that the content of this {@code Text} is not meant to be changed. Please
	 * do not change it.
	 */
	private static final Text CLASS = new Text("[A-Za-z0-9]+\\.(class|java)");
	
	/**
	 * The {@code content} or {@code value} of this piece of text.
	 */
	private StringBuilder content;
	
	/**
	 * The {@code content} or {@code value} of this piece of text, as a
	 * {@code String}.
	 * 
	 * @deprecated Should not be edited directly; use the
	 *             {@link #setContent(CharSequence)} method.
	 */
	@Deprecated
	private String cachedContent;
	
	/**
	 * Constructs an empty piece of {@code Text}.
	 */
	public Text() {
		this(EMPTY_TEXT);
	}
	
	/**
	 * Creates a piece of {@code Text} with the given {@code base} as its initial
	 * {@code Text} content.
	 * 
	 * @param base The inital {@code Text} content of this {@code Text}.e
	 */
	public Text(String base) {
		content = new StringBuilder(base);
	}
	
	/**
	 * Creates a piece of {@code Text} with the given {@code base} as its initial
	 * {@code Text} content.
	 * 
	 * @param base The inital {@code Text} content of this {@code Text}.
	 */
	public Text(Text base) {
		content = base.content;
		
	}
	
	/**
	 * Creates a piece of {@code Text} from and array of Letters as its initial
	 * content.
	 * 
	 * @param base The initial content of this {@code Text}, as an {@code Letter}
	 *             array.
	 */
	public Text(Letter[] base) {
		for (int i = 0; i < base.length; i++) {
			content.append(base[i].toString());
		}
	}
	
	/**
	 * Creates a piece of {@code Text} from and array of {@code chars} as the
	 * initial content.
	 * 
	 * @param base The initial content of this {@code Text}, as a {@code char}
	 *             array.
	 */
	public Text(char[] base) {
		this(new String(base));
	}
	
	/**
	 * Creates a piece of {@code Text} object from the given byte array.
	 * 
	 * @param bytes The bytes this piece of {@code Text} will contain.
	 * @return The created {@code Text}.
	 */
	public static Text fromBytes(byte[] bytes) {
		{ // Delete everything at the end of the stack (don't think it matters).
			return new Text(new String(bytes));
		}
	}
	
	/**
	 * Creates a piece of {@code Text} from the contents of the given file.
	 * 
	 * @param file The file to be read.
	 * @return The resulting {@code Text}.
	 * @throws IOException If an I/O exception occurs while reading from the file.
	 */
	public static Text fromFile(java.io.File file) throws IOException {
		return fromBytes(Files.readAllBytes(file.toPath()));
	}
	
	/**
	 * Creates a piece of {@code Text} from the contents of the given input stream.
	 * 
	 * @param in         The input stream to be read.
	 * @param bufferSize The encoding of the file.
	 * @return The resulting {@code Text}.
	 * @throws UncheckedIOException If an I/O exception occurs while reading from
	 *                              the input stream.
	 */
	public static Text fromInputStream(java.io.InputStream in, Charset encoding) {
		return new Text(new BufferedReader(new InputStreamReader(in, encoding)).lines()
				.collect(Collectors.joining(System.lineSeparator())));
	}
	
	/**
	 * Finds the index at which the nth occurrence of {@code find} is situated.
	 * 
	 * @param src        The source sequence.
	 * @param find       The sequence to find.
	 * @param occurrence The nth occurrence of {@code find} in the sequence
	 *                   ({@code src}).
	 * @return The index where the nth occurrence of {@code find} is situated; or
	 *         {@code -1} if no such occurrence exists.
	 */
	protected static int findIndex(CharSequence src, CharSequence find, int occurrence) {
		String temp = src.toString();
		int tempIndex = -1;
		int finalIndex = 0;
		for (int n = 0; n < occurrence; ++n) {
			tempIndex = temp.indexOf(find.toString());
			if (tempIndex == -1) {
				finalIndex = 0;
				break;
			}
			temp = temp.substring(++tempIndex);
			finalIndex += tempIndex;
		}
		return --finalIndex;
	}
	
	/**
	 * Gets the {@code n}th occurrence of a particular regular expression, defined
	 * by the {@code regex}.
	 * 
	 * @param src        The source sequence.
	 * @param regex      The regular expression to search for.
	 * @param occurrence The {@code n}th occurrence.
	 * @return A {@code MatchResult} which is a result of the operation. This result
	 *         can be used to find the starting index ({@link MatchResult#start()})
	 *         of the {@code n}th sequence, and the ending index
	 *         ({@link MatchResult#end()}) of the {@code n}th sequence.
	 */
	protected static MatchResult getRegexOccurrence(CharSequence src, CharSequence regex, int occurrence) {
		Matcher mat = Pattern.compile(regex.toString()).matcher(src);
		
		for (int i = 0; i < occurrence; i++) {
			if (!mat.find())
				return null;
		}
		
		return mat;
	}
	
	/**
	 * Gets the index of the {@code n}th occurrence of a particular regular
	 * expression, defined by the {@code regex}.
	 * 
	 * @param src        The source sequence.
	 * @param regex      The regular expression to search for.
	 * @param occurrence The {@code n}th occurrence.
	 * @return A {@code MatchResult} which is a result of the operation. This result
	 *         can be used to find the starting index ({@link MatchResult#start()})
	 *         of the {@code n}th sequence, and the ending index
	 *         ({@link MatchResult#end()}) of the {@code n}th sequence.
	 */
	protected static int getRegexOccurrenceIndex(CharSequence src, CharSequence regex, int occurrence) {
		Matcher mat = Pattern.compile(regex.toString()).matcher(src);
		
		for (int i = 0; i < occurrence; i++) {
			if (!mat.find())
				return -1;
		}
		
		return findIndex(src, mat.group(), occurrence);
	}
	
	/**
	 * Returns the last match result of the given regex.
	 * 
	 * @param src   The source string.
	 * @param regex The regex to search for.
	 * @return The last match result of the given regex.
	 */
	protected static MatchResult getLastRegexOccurrence(CharSequence src, CharSequence regex) {
		Matcher mat = Pattern.compile(regex.toString()).matcher(src);
		
		while (mat.find()) {
		}
		
		return mat;
	}
	
	/**
	 * Replaces the first substring of this {@code Text} that matches the given
	 * <a href="../util/regex/Pattern.html#sum">regular expression</a> with the
	 * given replacement.
	 *
	 * <p>
	 * An invocation of this method of the form
	 * <i>text</i>{@code .replaceFirst(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
	 * yields exactly the same result as the expression
	 *
	 * <blockquote> <code>
	 * {@link java.util.regex.Pattern}.{@link
	 * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
	 * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
	 * java.util.regex.Matcher#replaceFirst replaceFirst}(<i>repl</i>)
	 * </code> </blockquote>
	 *
	 * <p>
	 * Note that backslashes ({@code \}) and dollar signs ({@code $}) in the
	 * replacement string may cause the results to be different than if it were
	 * being treated as a literal replacement string; see
	 * {@link java.util.regex.Matcher#replaceFirst}. Use
	 * {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
	 * meaning of these characters, if desired.
	 *
	 * @param regex        The regular expression to which this {@code Text} is to
	 *                     be matched.
	 * @param replacement. The {@code CharSequence} to be substituted for the first
	 *                     match.
	 *
	 * @return The resulting {@code Text} (or this).
	 *
	 * @throws PatternSyntaxException if the regular expression's syntax is invalid
	 *
	 * @see java.util.regex.Pattern
	 */
	public Text replaceFirstRegex(CharSequence regex, CharSequence replacement) {
		setContent(toString().replaceFirst(regex.toString(), replacement.toString()));
		return this;
	}
	
	/**
	 * Replaces the first occurrence of the given {@code CharSequence} in this
	 * {@code Text}.
	 * 
	 * @param find        The {@code CharSequence} to which this {@code Text} is to
	 *                    be matched.
	 * @param replacement The {@code Text} to be substituted for the first match.
	 * @return The resulting {@code Text} (or this).
	 */
	public Text replaceFirst(CharSequence find, CharSequence replacement) {
		String source = toString();
		
		int index = source.indexOf(find.toString());
		
		if (index == -1) {
			return this;
		}
		
		setContent(source.substring(0, index).concat(replacement.toString()).concat(source.substring(index + find.length())));
		return this;
	}
	
	/**
	 * Replaces the {@code n}th occurrence of the of the given sequence
	 * ({@code find}) to {@code replacement}.
	 * 
	 * @param find        The sequence to find.
	 * @param replacement The replacement of {@code find}, when found.
	 * @param occurrence  The {@code n}th occurrence of {@code find} in this
	 *                    {@code Text}.
	 * @return The resulting {@code Text}, or this {@code Text} if there is no
	 *         {@code n}th occurrence.
	 */
	public Text replace(CharSequence find, CharSequence replacement, int occurrence) {
		int index = findIndex(toString(), find, Math.max(1, occurrence));
		
		if (index == -1)
			return this;
		
		setContent(toString().substring(0, index) + replacement + toString().substring(index + find.length()));
		return this;
	}
	
	/**
	 * Replaces the {@code n}th occurrence of the of the given regular expression
	 * ({@code regex}) to {@code replacement}.
	 * 
	 * @param regex       The regular expression to check for.
	 * @param replacement The replacement of the regular expression, when found.
	 * @param occurrence  The {@code n}th occurrence of {@code find} in this
	 *                    {@code Text}.
	 * @return The resulting {@code Text}, or this {@code Text} if there is no
	 *         {@code n}th occurrence.
	 */
	public Text replaceRegex(CharSequence regex, CharSequence replacement, int occurrence) {
		MatchResult res = getRegexOccurrence(content, regex, occurrence);
		
		if (res == null)
			return this;
		
		setContent(content.substring(0, res.start()) + replacement + content.substring(res.end()));
		return this;
	}
	
	/**
	 * Replaces the last occurrence of a sequence in this {@code Text}.
	 * 
	 * @param find        The sequence to find.
	 * @param replacement The sequence that will be used as the replacement.
	 * @return The resulting {@code Text}, or this {@code Text} if there is no
	 *         {@code find} in this {@code Text}.
	 */
	public Text replaceLast(CharSequence find, CharSequence replacement) {
		int lastIndex = cachedContent.lastIndexOf(find.toString());
		
		if (lastIndex == -1)
			return this;
		
		setContent(cachedContent.substring(0, lastIndex) + replacement + cachedContent.substring(lastIndex + find.length()));
		return this;
	}
	
	/**
	 * Replaces the last occurrence of a regular expression in this {@code Text}.
	 * 
	 * @param regex       The regular expression to find and replace.
	 * @param replacement The replacement for the regular expression.
	 * @return The resulting {@code Text}, or this {@code Text} if there is no
	 *         {@code regex} in this {@code Text}.
	 */
	public Text replaceLastRegex(CharSequence regex, CharSequence replacement) {
		return replaceFirstRegex("(?s)(.*)" + regex.toString(), "$1" + replacement.toString());
	}
	
	/**
	 * Replaces all occurrences of a sequence {@code find} in this {@code Text} with
	 * {@code replacement}.
	 *
	 * @param find        The sequence of char values to be replaced.
	 * @param replacement The replacement sequence of char values.
	 * @return The resulting {@code Text}, or this {@code Text} if there is no
	 *         {@code find} in this {@code Text}.
	 */
	public Text replaceAll(CharSequence find, CharSequence replacement) {
		setContent(cachedContent.replace(find, replacement));
		return this;
	}
	
	/**
	 * Replaces all occurrences of a regular expression {@code find} in this
	 * {@code Text} with {@code replacement}.
	 *
	 * @param find        The regular expression to find and replace.
	 * @param replacement The replacement for the regular expression.
	 * @return The resulting {@code Text}, or this {@code Text} if there is no
	 *         {@code find} in this {@code Text}.
	 */
	public Text replaceAllRegex(CharSequence regex, CharSequence replacement) {
		setContent(cachedContent.replaceAll(regex.toString(), replacement.toString()));
		return this;
	}
	
	/**
	 * Returns the index within this {@code Text} of the first occurrence of the
	 * specified subsequence. The integer returned is the smallest value <i>k</i>
	 * such that:
	 * 
	 * <pre>
	 * {@code
	 * this.toString().startsWith(sequence, <i>k</i>)
	 * }
	 * </pre>
	 * 
	 * is {@code true}.
	 *
	 * @param str any sequence.
	 * @return If the sequence argument occurs as a subsequence within this
	 *         {@code Text}, then the index of the first character of the first such
	 *         subsequence is returned; if it does not occur as a subsequence,
	 *         {@code -1} is returned.
	 */
	public int firstIndexOf(CharSequence sequence) {
		return content.indexOf(sequence.toString());
	}
	
	/**
	 * Returns the index within this {@code Text} of the first occurrence of the
	 * specified subsequence, starting at the specified index. The integer returned
	 * is the smallest value {@code k} for which:
	 * 
	 * <pre>
	 * {@code
	 *     k >= Math.min(fromIndex, this.length()) &&
	 *                   this.toString().startsWith(sequence, k)
	 * }
	 * </pre>
	 * 
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param sequence  The subsequence for which to search.
	 * @param fromIndex The index from which to start the search.
	 * @return The index within this {@code Text} of the first occurrence of the
	 *         specified subsequence, starting at the specified index.
	 */
	public int firstIndexOf(CharSequence sequence, int fromIndex) {
		return content.indexOf(sequence.toString(), fromIndex);
	}
	
	/**
	 * Returns the index within this {@code Text} of the {@code n}th occurrence of
	 * the specified subsequence.
	 * 
	 * @param sequence   The subsequence for which to search.
	 * @param occurrence The {@code n}th occurrence of {@code sequence} in this
	 *                   {@code Text}.
	 * @return If the sequence argument occurs as a subsequence within this
	 *         {@code Text}, then the index of the first character of the
	 *         {@code n}th such subsequence is returned; if it does not occur as a
	 *         subsequence, {@code -1} is returned.
	 */
	public int indexOf(CharSequence sequence, int occurrence) {
		return findIndex(content, sequence, occurrence);
	}
	
	/**
	 * Returns the index within this {@code Text} of the {@code n}th occurrence
	 * (specified by {@code occurrence}) of the specified subsequence, starting at
	 * the specified index.
	 * 
	 * @param sequence   The subsequence for which to search.
	 * @param fromIndex  The index from which to start the search.
	 * @param occurrence The {@code n}th occurrence of {@code sequence} in this
	 *                   {@code Text}.
	 * @return The index within this {@code Text} of the first occurrence of the
	 *         specified subsequence, starting at the specified index.
	 */
	public int indexOf(CharSequence sequence, int fromIndex, int occurrence) {
		return findIndex(content.substring(fromIndex), sequence, occurrence);
	}
	
	/**
	 * Returns the index within this {@code Text} of the rightmost occurrence of the
	 * specified subsequence. The rightmost empty {@code Text} "" is considered to
	 * occur at the index value {@code this.length()}. The returned index is the
	 * largest value <i>k</i> such that
	 * 
	 * <pre>
	 * {@code
	 * this.toString().startsWith(sequence, k)
	 * }
	 * </pre>
	 * 
	 * is true.
	 *
	 * @param str the subsequence to search for.
	 * @return if the CharSequence argument occurs one or more times as a
	 *         subsequence within this object, then the index of the first character
	 *         of the last such subsequence is returned. If it does not occur as a
	 *         subsequence, {@code -1} is returned.
	 */
	public int lastIndexOf(CharSequence sequence) {
		return content.lastIndexOf(sequence.toString());
	}
	
	/**
	 * Returns the index within this {@code Text} of the last occurrence of the
	 * specified subsequence. The integer returned is the largest value <i>k</i>
	 * such that:
	 * 
	 * <pre>
	 * {@code
	 *     k <= Math.min(fromIndex, this.length()) &&
	 *                   this.toString().startsWith(str, k)
	 * }
	 * </pre>
	 * 
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param str       the subsequence to search for.
	 * @param fromIndex the index to start the search from.
	 * @return the index within this {@code Text} of the last occurrence of the
	 *         specified subsequence.
	 */
	public int lastIndexOf(CharSequence sequence, int fromIndex) {
		return content.lastIndexOf(sequence.toString(), fromIndex);
	}
	
	///////////////////////////////////// CHAR /////////////////////////////////////
	
	/**
	 * Returns the index within this {@code Text} of the first occurrence of the
	 * specified character. The integer returned is the smallest value <i>k</i> such
	 * that:
	 * 
	 * <pre>
	 * {@code
	 * this.toString().startsWith(ch, <i>k</i>)
	 * }
	 * </pre>
	 * 
	 * is {@code true}.
	 *
	 * @param ch Any character.
	 * @return If the sequence argument occurs as a character within this
	 *         {@code Text}, then the index of the first such character is returned;
	 *         if it does not occur as a character, {@code -1} is returned.
	 */
	public int firstIndexOf(int ch) {
		return firstIndexOf("" + (char) ch);
	}
	
	/**
	 * Returns the index within this {@code Text} of the first occurrence of the
	 * specified character, starting at the specified index. The integer returned is
	 * the smallest value {@code k} for which:
	 * 
	 * <pre>
	 * {@code
	 *     k >= Math.min(fromIndex, this.length()) &&
	 *                   this.toString().startsWith(ch, k)
	 * }
	 * </pre>
	 * 
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param ch        The character for which to search.
	 * @param fromIndex The index from which to start the search.
	 * @return The index within this {@code Text} of the first occurrence of the
	 *         specified character, starting at the specified index.
	 */
	public int firstIndexOf(int ch, int fromIndex) {
		return cachedContent.indexOf(ch, fromIndex);
	}
	
	/**
	 * Returns the index within this {@code Text} of the {@code n}th occurrence of
	 * the specified character.
	 * 
	 * @param ch         Any character.
	 * @param fromIndex  The index from which to start the search.
	 * @param occurrence The {@code n}th occurrence of {@code ch} in this
	 *                   {@code Text}.
	 * @return The index within this {@code Text} of the {@code n}th occurrence of
	 *         the specified character, starting at the specified index.
	 */
	public int indexOf(int ch, int fromIndex, int occurrence) {
		return findIndex(content.substring(fromIndex), "" + (char) ch, occurrence);
	}
	
	/**
	 * Returns the index within this {@code Text} of the {@code n}th occurrence of
	 * the specified character, starting at the specified index.
	 * 
	 * @param ch         Any character.
	 * @param occurrence The {@code n}th occurrence of {@code ch} in this
	 *                   {@code Text}.
	 * @return The index within this {@code Text} of the {@code n}th occurrence of
	 *         the specified character.
	 */
	public int indexOf(int ch, int occurrence) {
		return findIndex(content, "" + (char) ch, occurrence);
	}
	
	/**
	 * Returns the index within this {@code Text} of the rightmost occurrence of the
	 * specified character. The rightmost empty character '' is considered to occur
	 * at the index value {@code this.length()}. The returned index is the largest
	 * value <i>k</i> such that
	 * 
	 * <pre>
	 * {@code
	 * this.toString().startsWith(ch, k)
	 * }
	 * </pre>
	 * 
	 * is true.
	 *
	 * @param ch the character to search for.
	 * @return if the character argument occurs one or more times within this
	 *         object, then the index of the last such character is returned. If it
	 *         does not occur as a character, {@code -1} is returned.
	 */
	public int lastIndexOf(int ch) {
		return cachedContent.lastIndexOf(ch);
	}
	
	/**
	 * Returns the index within this {@code Text} of the last occurrence of the
	 * specified character. The integer returned is the largest value <i>k</i> such
	 * that:
	 * 
	 * <pre>
	 * {@code
	 *     k <= Math.min(fromIndex, this.length()) &&
	 *                   this.toString().startsWith(ch, k)
	 * }
	 * </pre>
	 * 
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param str       the character to search for.
	 * @param fromIndex the index to start the search from.
	 * @return the index within this {@code Text} of the last occurrence of the
	 *         specified character.
	 */
	public int lastIndexOf(int ch, int fromIndex) {
		return cachedContent.lastIndexOf(ch, fromIndex);
	}
	
	//////////////////////////// LETTER B ///////////////////////////////
	
	/**
	 * Returns the index within this {@code Text} of the first occurrence of the
	 * specified character. The integer returned is the smallest value <i>k</i> such
	 * that:
	 * 
	 * <pre>
	 * {@code
	 * this.toString().startsWith(ch, <i>k</i>)
	 * }
	 * </pre>
	 * 
	 * is {@code true}.
	 *
	 * @param ch any character.
	 * @return if the sequence argument occurs as a character within this
	 *         {@code Text}, then the index of the first such character is returned;
	 *         if it does not occur as a character, {@code -1} is returned.
	 */
	public int firstIndexOf(Letter ch) {
		return firstIndexOf(ch.getValue());
	}
	
	/**
	 * Returns the index within this {@code Text} of the first occurrence of the
	 * specified character, starting at the specified index. The integer returned is
	 * the smallest value {@code k} for which:
	 * 
	 * <pre>
	 * {@code
	 *     k >= Math.min(fromIndex, this.length()) &&
	 *                   this.toString().startsWith(ch, k)
	 * }
	 * </pre>
	 * 
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param ch        The character for which to search.
	 * @param fromIndex The index from which to start the search.
	 * @return The index within this {@code Text} of the first occurrence of the
	 *         specified character, starting at the specified index.
	 */
	public int firstIndexOf(Letter ch, int fromIndex) {
		return firstIndexOf(ch.getValue());
	}
	
	/**
	 * Returns the index within this {@code Text} of the {@code n}th occurrence of
	 * the specified character, starting at the specified index.
	 * 
	 * @param ch         Any character.
	 * @param fromIndex  The index from which to start the search.
	 * @param occurrence The {@code n}th occurrence of {@code ch} in this
	 *                   {@code Text}.
	 * @return The index within this {@code Text} of the {@code n}th occurrence of
	 *         the specified character, starting at the specified index.
	 */
	public int indexOf(Letter ch, int fromIndex, int occurrence) {
		return findIndex(content.substring(fromIndex), "" + ch.getValue(), occurrence);
	}
	
	/**
	 * Returns the index within this {@code Text} of the {@code n}th occurrence of
	 * the specified character.
	 *
	 * @param ch         the character for which to search.
	 * @param occurrence the index from which to start the search.
	 * @return the index within this {@code Text} of the {@code n}th occurrence of
	 *         the specified character.
	 */
	public int indexOf(Letter ch, int occurrence) {
		return indexOf(ch.getValue(), occurrence);
	}
	
	/**
	 * Returns the index within this {@code Text} of the rightmost occurrence of the
	 * specified character. The rightmost empty character '' is considered to occur
	 * at the index value {@code this.length()}. The returned index is the largest
	 * value <i>k</i> such that
	 * 
	 * <pre>
	 * {@code
	 * this.toString().startsWith(ch, k)
	 * }
	 * </pre>
	 * 
	 * is true.
	 *
	 * @param ch the character to search for.
	 * @return if the character argument occurs one or more times within this
	 *         object, then the index of the last such character is returned. If it
	 *         does not occur as a character, {@code -1} is returned.
	 */
	public int lastIndexOf(Letter ch) {
		return lastIndexOf(ch.getValue());
	}
	
	/**
	 * Returns the index within this {@code Text} of the last occurrence of the
	 * specified character. The integer returned is the largest value <i>k</i> such
	 * that:
	 * 
	 * <pre>
	 * {@code
	 *     k <= Math.min(fromIndex, this.length()) &&
	 *                   this.toString().startsWith(ch, k)
	 * }
	 * </pre>
	 * 
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param str       the character to search for.
	 * @param fromIndex the index to start the search from.
	 * @return the index within this {@code Text} of the last occurrence of the
	 *         specified character.
	 */
	public int lastIndexOf(Letter ch, int fromIndex) {
		return lastIndexOf(ch.getValue(), fromIndex);
	}
	
	/////////////////////////////// REGEX //////////////////////////////
	
	/**
	 * Returns the indices within this {@code Text} of the start and end of the
	 * first occurrence of the specified regex.
	 *
	 * @param regex The regex for which to search.
	 * @return The start and end indices within this {@code Text} of the first
	 *         occurrence of the specified regex.
	 */
	public int[] firstIndicesOfRegex(CharSequence regex) {
		return indicesOfRegex(regex, 1);
	}
	
	/**
	 * Returns the indices within this {@code Text} of the start and end of the
	 * first occurrence of the specified regex, starting at the specified index.
	 * 
	 * @param regex     The regex for which to search.
	 * @param fromIndex The index from which to start the search.
	 * @return The start and end indices within this {@code Text} of the first
	 *         occurrence of the specified regex, starting at the specified index.
	 */
	public int[] firstIndicesOfRegex(CharSequence regex, int fromIndex) {
		return indicesOfRegex(regex, fromIndex, 1);
	}
	
	/**
	 * Returns the indices within this {@code Text} of the start and end of the
	 * {@code n}th occurrence of the specified regex, starting at the specified
	 * index.
	 * 
	 * @param regex      The regex for which to search.
	 * @param fromIndex  The index from which to start the search.
	 * @param occurrence The {@code n}th occurrence of the specified regular
	 *                   expression.
	 * @return The start and end indices within this {@code Text} of the {@code n}th
	 *         occurrence of the specified regex, starting at the specified index.
	 */
	public int[] indicesOfRegex(CharSequence regex, int fromIndex, int occurrence) {
		MatchResult res = getRegexOccurrence(content.substring(fromIndex), regex, occurrence);
		return new int[] { res.start(), res.end() };
	}
	
	/**
	 * Returns the indices within this {@code Text} of the start and end of the
	 * {@code n}th occurrence of the specified regular expression.
	 *
	 * @param regex      The regex for which to search.
	 * @param occurrence The {@code n}th occurrence of the specified regular
	 *                   expression.
	 * @return The start and end indices within this {@code Text} of the {@code n}th
	 *         occurrence of the specified regex.
	 */
	public int[] indicesOfRegex(CharSequence regex, int occurrence) {
		MatchResult res = getRegexOccurrence(content, regex, occurrence);
		return new int[] { res.start(), res.end() };
	}
	
	/**
	 * Returns the indices within this {@code Text} of the start and end of the last
	 * occurrence of the specified regex.
	 *
	 * @param regex The regex for which to search.
	 * @return The start and end indices within this {@code Text} of the last
	 *         occurrence of the specified regex.
	 */
	public int[] lastIndicesOfRegex(CharSequence regex) {
		MatchResult res = getLastRegexOccurrence(content, regex);
		return new int[] { res.start(), res.end() };
	}
	
	/**
	 * Returns the indices within this {@code Text} of the start and end of the last
	 * occurrence of the specified regex, starting at the specified index.
	 * 
	 * @param regex     The regex for which to search.
	 * @param fromIndex The index from which to start the search.
	 * @return The start and end indices within this {@code Text} of the last
	 *         occurrence of the specified regex, starting at the specified index.
	 */
	public int[] lastIndicesOfRegex(CharSequence regex, int fromIndex) {
		MatchResult res = getLastRegexOccurrence(content.substring(fromIndex), regex);
		return new int[] { res.start(), res.end() };
	}
	
	/**
	 * Adds the specified character to the end of this {@code Text}.
	 * 
	 * @param ch The character to add.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text addLetter(char ch) {
		addLetter(ch, length());
		return this;
	}
	
	/**
	 * Adds the specified character at the specified index to this {@code Text}.
	 * 
	 * @param ch    The character to add.
	 * @param index The index where to add the character.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text addLetter(char ch, int index) {
		content.insert(index, ch);
		cachedContent = toString();
		return this;
	}
	
	/**
	 * Adds the specified letter at the specified index to this {@code Text}.
	 * 
	 * @param ch    The letter to add.
	 * @param index The index where to add the character.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text addLetter(Letter ch, int index) {
		addLetter(ch.getValue(), index);
		return this;
	}
	
	/**
	 * Adds the specified letter to the end of this {@code Text}.
	 * 
	 * @param ch The letter to add.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text addLetter(Letter ch) {
		addLetter(ch.getValue());
		return this;
	}
	
	///////// REMOVE /////////
	
	/**
	 * Removes all instances of the specified character from this {@code Text}.
	 * 
	 * @param ch The character to remove.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text removeLetter(char ch) {
		replaceAll("" + ch, "");
		return this;
	}
	
	/**
	 * Removes the specified letter at the specified index from this {@code Text} if
	 * and only if the letter at the given index is equal to {@code ch}.
	 * 
	 * @param index The index of the character to be removed the letter.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text removeLetter(int index) {
		content.deleteCharAt(index);
		cachedContent = toString();
		return this;
	}
	
	/**
	 * Removes all instances of the specified letter from this {@code Text}.
	 * 
	 * @param ch The letter to remove.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text removeLetter(Letter ch) {
		removeLetter(ch.getValue());
		return this;
	}
	
	/**
	 * Checks whether this {@code Text} is equal to the specified sequence, ignoring
	 * the letter-case.
	 * 
	 * @param sequence The sequence to check.
	 * @return {@code true} if this {@code Text} is equal to the given sequence,
	 *         ignoring the letter-case; {@code false} otherwise.
	 */
	public boolean equalsIgnoreCase(CharSequence sequence) {
		return cachedContent.equalsIgnoreCase(sequence.toString());
	}
	
	/**
	 * Checks whether this {@code Text} is equal to the specified sequence, ignoring
	 * the letter-case of the specified characters.
	 * <p>
	 * Note that this may or may not work whilst using letter from the Georgian
	 * alphabet.
	 * 
	 * @param sequence The sequence to check.
	 * @param chars    The characters on which the letter-cases should be ignored.
	 * @return {@code true} if this {@code Text} is equal to the given sequence,
	 *         ignoring the letter-case; {@code false} otherwise.
	 */
	public boolean equalsIgnoreCaseWith(CharSequence sequence, char... chars) {
		String content = toString();
		String seqString = sequence.toString();
		
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			
			content.replace(Character.toUpperCase(ch), Character.toLowerCase(ch));
			seqString.replace(Character.toUpperCase(ch), Character.toLowerCase(ch));
		}
		
		return content.equals(seqString);
	}
	
	/**
	 * Checks whether this {@code Text} is equal to the specified sequence, ignoring
	 * the letter-case of the specified letters.
	 * <p>
	 * Note that this may or may not work whilst using letter from the Georgian
	 * alphabet.
	 * 
	 * @param sequence The sequence to check.
	 * @param letters  The letters on which the letter-cases should be ignored.
	 * @return {@code true} if this {@code Text} is equal to the given sequence,
	 *         ignoring the letter-case; {@code false} otherwise.
	 */
	public boolean equalsIgnoreCaseWith(CharSequence sequence, Letter... letters) {
		return equalsIgnoreCaseWith(sequence, Letter.convert(letters));
	}
	
	/**
	 * Checks whether this {@code Text} starts with the given sequence.
	 * 
	 * @param sequence The sequence to check.
	 * @return {@code true} if this {@code Text} starts with the given sequence;
	 *         {@code false} otherwise.
	 */
	public boolean startsWith(CharSequence sequence) {
		return startsWith(sequence, 0);
	}
	
	/**
	 * Checks whether this {@code Text} starts with the given sequence, starting at
	 * the specified offset or index.
	 * 
	 * @param sequence The sequence to check.
	 * @param offset   The offset or index where to start looking.
	 * @return {@code true} if this {@code Text} starts with the given sequence;
	 *         {@code false} otherwise.
	 */
	public boolean startsWith(CharSequence sequence, int offset) {
		return cachedContent.startsWith(sequence.toString(), offset);
	}
	
	/**
	 * Checks whether this {@code Text} ends with the given sequence.
	 * 
	 * @param sequence The sequence to check.
	 * @return {@code true} if this {@code Text} ends with the given sequence;
	 *         {@code false} otherwise.
	 */
	public boolean endsWith(CharSequence sequence) {
		return cachedContent.endsWith(sequence.toString());
	}
	
	/**
	 * Checks whether this {@code Text} contains the given sequence.
	 * 
	 * @param sequence The sequence to check.
	 * @return {@code true} if this {@code Text} contains the given sequence;
	 *         {@code false} otherwise.
	 */
	public boolean contains(CharSequence sequence) {
		return cachedContent.contains(sequence);
	}
	
	/**
	 * Returns the character (Unicode code point) at the specified index. The index
	 * refers to {@code char} values (Unicode code units) and ranges from {@code 0}
	 * to {@link #length()}{@code  - 1}.
	 * 
	 * @param index The index to the {@code char} values.
	 * @return The code point value of the character at the {@code index}
	 * @exception IndexOutOfBoundsException If the {@code index} argument is
	 *                                      negative or not less than the length of
	 *                                      this sequence.
	 */
	public int codePointAt(int index) {
		return content.codePointAt(index);
	}
	
	/**
	 * Returns the character (Unicode code point) before the specified index. The
	 * index refers to {@code char} values (Unicode code units) and ranges from
	 * {@code 1} to {@link #length()}
	 * 
	 * @param index The index following the code point that should be returned.
	 * @return The Unicode code point value before the given index.
	 * @exception IndexOutOfBoundsException If the {@code index} argument is less
	 *                                      than 1 or greater than the length of
	 *                                      this sequence.
	 */
	public int codePointBefore(int index) {
		return content.codePointBefore(index);
	}
	
	/**
	 * Encodes this {@code Text} into a sequence of bytes using the platform's
	 * default charset, storing the result into a new byte array.
	 * 
	 * @return The resultant byte array.
	 */
	public byte[] getBytes() {
		return cachedContent.getBytes();
	}
	
	/**
	 * Converts this {@code Text} to a new character array.
	 * 
	 * @return A newly allocated character array whose length is the length of this
	 *         {@code Text} and whose contents are initialized to contain the
	 *         character sequence represented by this {@code Text}.
	 */
	public char[] getChars() {
		return cachedContent.toCharArray();
	}
	
	/**
	 * Converts this {@code Text} to a new {@link Letter} array.
	 * 
	 * @return A newly allocated {@link Letter} array whose length is the length of
	 *         this {@code Text} and whose contents are initialized to contain the
	 *         character sequence represented by this {@code Text}.
	 */
	public Letter[] getLetters() {
		return Letter.convert(getChars());
	}
	
	/**
	 * Encodes this {@code Text} into a sequence of bytes using the given
	 * {@linkplain java.nio.charset.Charset charset}, storing the result into a new
	 * byte array.
	 * 
	 * @param charset The {@linkplain java.nio.charset.Charset} to be used to encode
	 *                the {@code String}.
	 * @return The resultant byte array.
	 */
	public byte[] getBytes(Charset charset) {
		return cachedContent.getBytes(charset);
	}
	
	/**
	 * Reverses the content of this {@code Text}.
	 * 
	 * @return This piece of {@code Text}, with the reversed content.
	 */
	public Text reverseContent() {
		content = content.reverse();
		cachedContent = toString();
		return this;
	}
	
	/**
	 * Randomizes the content of this {@code Text} and returns the resulting output.
	 * 
	 * @return The resulting {@code Text}, or this.
	 */
	public Text randomizeContent() {
		List<Character> chars = new LinkedList<Character>();
		
		for (char c : getChars()) {
			chars.add(c);
		}
		
		StringBuilder output = new StringBuilder(length());
		
		while (chars.size() != 0) {
			int rand = (int) (Math.random() * chars.size());
			output.append(chars.remove(rand));
		}
		
		setContent(output);
		return this;
	}
	
	/**
	 * Clears the content of this {@code Text}. This is equivalent to setting the
	 * content to {@link #EMPTY_TEXT}.
	 */
	public void clear() {
		content.setLength(0);
	}
	
	/**
	 * Returns {@code true} if, {@link #length()} is {@code 0} or the content is
	 * equal to {@link #EMPTY_TEXT}.
	 *
	 * @return {@code true} if {@link #length()} is {@code 0}, or the content is
	 *         equal to {@link #EMPTY_TEXT} otherwise {@code false}.
	 */
	public boolean isEmpty() {
		return length() == 0 || equals(EMPTY_TEXT) || content.codePoints().count() == 0; // For extra precautions
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Object o) {
		if (o instanceof Letter[]) {
			return compareTo(new Text((Letter[]) o));
		} else if (o instanceof char[]) {
			return compareTo(new Text((char[]) o));
		} else if (o instanceof byte[]) {
			return compareTo(Text.fromBytes((byte[]) o));
		} else if (o instanceof CharSequence) {
			return compareTo(o.toString());
		} else if (o instanceof Character) {
			return compareTo("" + o);
		} else if (o instanceof Byte) {
			return compareTo(new Letter((byte) o));
		}
		
		return toString().compareTo(o.toString());
	}
	
	/**
	 * Returns the length (character count).
	 *
	 * @return the length of the sequence of characters currently represented by
	 *         this object
	 */
	@Override
	public int length() {
		return content.length();
	}
	
	/**
	 * Returns the {@code char} value in this sequence at the specified index. The
	 * first {@code char} value is at index {@code 0}, the next at index {@code 1},
	 * and so on, as in array indexing.
	 * <p>
	 * The index argument must be greater than or equal to {@code 0}, and less than
	 * the length of this sequence.
	 *
	 * <p>
	 * If the {@code char} value specified by the index is a
	 * <a href="Character.html#unicode">surrogate</a>, the surrogate value is
	 * returned.
	 *
	 * @param index the index of the desired {@code char} value.
	 * @return the {@code char} value at the specified index.
	 * @throws IndexOutOfBoundsException if {@code index} is negative or greater
	 *                                   than or equal to {@code length()}.
	 */
	@Override
	public char charAt(int index) {
		return content.charAt(index);
	}
	
	/**
	 * Returns a copy of the value of the letter at the specified index.
	 * 
	 * @param index The index of the letter.
	 * @return A copy of the letter situated at the specified index.
	 */
	public Letter letterAt(int index) {
		return new Letter(charAt(index));
	}
	
	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 *
	 * <p>
	 * An invocation of this method of the form
	 *
	 * <pre>
	 * {@code
	 * sb.subSequence(begin,&nbsp;end)}
	 * </pre>
	 *
	 * behaves in exactly the same way as the invocation
	 *
	 * <pre>
	 * {@code
	 * sb.substring(begin,&nbsp;end)}
	 * </pre>
	 *
	 * This method is provided so that this class can implement the
	 * {@link CharSequence} interface.
	 *
	 * @param start the start index, inclusive.
	 * @param end   the end index, exclusive.
	 * @return the specified subsequence.
	 *
	 * @throws IndexOutOfBoundsException if {@code start} or {@code end} are
	 *                                   negative, if {@code end} is greater than
	 *                                   {@code length()}, or if {@code start} is
	 *                                   greater than {@code end}
	 */
	@Override
	public CharSequence subSequence(int start, int end) {
		return content.subSequence(start, end);
	}
	
	/**
	 * Sets the content of this {@code Text} to the given {@code content}.
	 * 
	 * @param content The content to set this {@code Text} to.
	 */
	public void setContent(CharSequence content) {
		this.content.replace(0, this.content.length(), content.toString());
		cachedContent = this.content.toString();
	}
	
	/**
	 * Sets the letter at the specified index to the underlying {@code letter}.
	 * 
	 * @param index  The index where to place the letter.
	 * @param letter The letter to place.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text setLetter(int index, Letter letter) {
		return setLetter(index, letter.getValue());
	}
	
	/**
	 * Sets the letter at the specified index to the underlying {@code ch}.
	 * 
	 * @param index  The index where to place the letter.
	 * @param letter The letter to place.
	 * @return The resulting {@code Text}, or this.
	 */
	public Text setLetter(int index, char ch) {
		removeLetter(index);
		addLetter(ch, index);
		return this;
	}
	
	/**
	 * The raw content of this {@code Text} as a {@link StringBuilder}.
	 * 
	 * @return The raw content of this {@code Text}.
	 */
	public StringBuilder getContent() {
		return content;
	}
	
	/**
	 * Checks whether this {@code Text} is equal to the given {@code Type}, in
	 * general terms.
	 * 
	 * @param type The type to be checked.
	 * @return {@code true} if this {@code Text} is the same {@code Type} as the
	 *         given parameter; otherwise {@code false}.
	 */
	public boolean equals(Type type) {
		return type.equalsAction.test(toString());
	}
	
	/**
	 * Checks whether this {@code Text} contains the given {@code Type}, in general
	 * terms.
	 * 
	 * @param type The type to be checked.
	 * @return {@code true} if this {@code Text} contains the same {@code Type} as
	 *         the given parameter; otherwise {@code false}.
	 */
	public boolean contains(Type type) {
		return type.containsAction.test(toString());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		return compareTo(o) == 0 ? true : super.equals(o);
	}
	
	/**
	 * Returns a copy this {@code Text}'s content as a {@code String}.
	 */
	@Override
	public String toString() {
		return content.toString();
	}
	
	/**
	 * The <code>Type</code> enumeration should be used for identifying whether a
	 * piece of <code>Text</code> equals or contains as specific type of content.
	 * Currently, there are a total of {@code 7} different types. These types are:
	 * <ul>
	 * <li>WHITESPACE</li>
	 * <li>ASCII_LETTER</li>
	 * <li>ASCII_NUMBER</li>
	 * <li>SPECIAL</li>
	 * <li>BOOLEAN</li>
	 * <li>CLASS</li>
	 * </ul>
	 * 
	 * <p>
	 * An example usage of a <code>Type</code> check, is when you want to check
	 * whether the {@code Text} is a number. To accomplish this, the following code
	 * must be executed:
	 * 
	 * <pre>
	 * 	<code>
	 * 		Text text = new Text("123456789");
	 * 
	 * 		if(text.equals(Type.ASCII_NUMBER)){
	 * 		    // do something
	 * 		}
	 * 	</code>
	 * </pre>
	 * <p>
	 * Here, we are assigning a piece of <code>Text</code> to the value of
	 * <code>123456789</code> as a <code>String</code>. Although, we are able to
	 * check if the
	 * 
	 * @see Text
	 */
	public static enum Type {
		/**
		 * A {@code Type} that represents a whitespace character (usually {@code " "},
		 * {@code "\t"}, {@code "\n"} or {@link Character#isWhitespace(char)}).
		 */
		WHITESPACE(x -> equalsPattern(x, Text.WHITESPACE.toString()), x -> containsPattern(x, Text.WHITESPACE.toString())),
		
		/**
		 * A {@code Type} that represents any
		 * <a href="https://www.ascii-code.com/">ASCII letter (codes 65-90 &&
		 * 97-122)</a>
		 */
		ASCII_LETTER(
				x -> equalsPattern(x.toString().trim(), Text.ASCII_LETTER.toString()),
				x -> containsPattern(x.toString().trim(), Text.ASCII_LETTER.toString())
		),
		
		/**
		 * A {@code Type} that represents any
		 * <a href="https://www.ascii-code.com/">ASCII number (codes 48-57)</a> (any
		 * number from 0-9, excluding decimals).
		 */
		ASCII_NUMBER(
				x -> equalsPattern(x.toString().trim(), Text.ASCII_NUMBER.toString()),
				x -> containsPattern(x.toString().trim(), Text.ASCII_NUMBER.toString())
		),
		
		/**
		 * A {@code Type} that represents any sort of letter, including decimals. Note
		 * that any possible number that can be typed (even ones that would be parsed as
		 * infinity) will be recognized by this {@code Type}. This also accepts
		 * scientific notation although, that might still be in a beta testing, although
		 * it is probably safe to use in most cases.
		 */
		NUMBER(
				x -> equalsPattern(x.toString().trim(), Text.NUMBER.toString()),
				x -> containsPattern(x.toString().trim(), Text.NUMBER.toString())
		),
		
		/**
		 * A {@code Type} that represents all the special characters, but also including
		 * the {@code "§"} character.
		 */
		SPECIAL(
				x -> equalsPattern(x.toString().trim(), Text.SPECIAL.toString()),
				x -> containsPattern(x.toString().trim(), Text.SPECIAL.toString())
		),
		
		/**
		 * This {@code Type} represents a boolean value of {@code true} or
		 * {@code false}. Although, {@code 1} and {@code 0} and be parsed as a boolean;
		 * {@code 1} being {@code true} and {@code 0} being {@code false}.
		 */
		BOOLEAN(
				x -> equalsPattern(x.toString().trim(), Text.BOOLEAN.toString()),
				x -> containsPattern(x.toString().trim(), Text.BOOLEAN.toString())
		),
		
		/**
		 * A {@code Type} that represents whether a piece of {@code Text} has on
		 * contains a valid class name in its name. Valid class names are class names
		 * that end in {@code ".class"} and {@code ".java"}.
		 */
		CLASS(
				x -> equalsPattern(x.toString().trim(), Text.CLASS.toString()),
				x -> containsPattern(x.toString().trim(), Text.CLASS.toString())
		);
		
		/**
		 * Checks whether a sequence is equal to this {@code Type}.
		 * 
		 * @param source The sequence
		 * @param pat    The pattern for this {@code Type}
		 * @return {@code true} if they are equal; {@code false} otherwise.
		 */
		private static boolean equalsPattern(CharSequence source, String pat) {
			return Pattern.compile(pat, Pattern.MULTILINE).matcher(source).matches();
		}
		
		/**
		 * Checks whether a sequence contains this {@code Type}.
		 * 
		 * @param source The sequence
		 * @param pat    The pattern for this {@code Type}
		 * @return {@code true} if it contains; {@code false} otherwise.
		 */
		private static boolean containsPattern(CharSequence source, String pat) {
			return Pattern.compile(pat, Pattern.MULTILINE).matcher(source).find();
		}
		
		/**
		 * Action used to check whether {@code Text} is equal to this {@code Type}.
		 */
		Predicate<CharSequence> equalsAction;
		
		/**
		 * Action used to check whether {@code Text} contains this {@code Type}.
		 */
		Predicate<CharSequence> containsAction;
		
		/**
		 * Creates a {@code Type} with the given Predicates as actions used to check
		 * equality of a {@code Text}.
		 * 
		 * @param equalsAction   Action used to check whether {@code Text} is equal to
		 *                       this {@code Type}.
		 * @param containsAction Action used to check whether {@code Text} contains this
		 *                       {@code Type}.
		 */
		Type(Predicate<CharSequence> equalsAction, Predicate<CharSequence> containsAction) {
			this.equalsAction = equalsAction;
			this.containsAction = containsAction;
		}
	}
}
