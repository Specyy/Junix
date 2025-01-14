/**
 * 
 */
package com.junix.utils.logging;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@code LogFileList} is a list of all {@link LogFile LogFiles} that have
 * been created or that can be created for a specific {@code Logger}. The
 * content's of this list depends on how many times the {@link LogFile#save()}
 * method has been called for the {@link #getNextFile()} method. Note that once
 * the {@link #getNextFile()} has been saved, it will return a new instance of a
 * {@code LogFile} that can be used to save a new file and the old instance will
 * get stored into the {@link #getLogFiles()} list.
 * <p>
 * Note that this inherist the {@code Iterable} interface so it can be iterated
 * over using the {@link #iterator()} method, or by using a {@code foreach}
 * loop.
 * 
 * @see LogFile
 */
public class LogFileList implements Iterable<LogFile> {
	
	/**
	 * The origin <code>Logger</code> for this {@code LogFileList}. This
	 * {@code Logger} is the logger that this {@code LogFileList} belongs to.
	 */
	private final Logger origin;
	
	/**
	 * The next {@code LogFile} that will be created once the {@link LogFile#save()}
	 * method is called for this instance. This instance will constantly be changing
	 * value so that a new file can be accessed every time another file is saved.
	 */
	private LogFile nextFile;
	
	/**
	 * A {@code List} of all the log files that have been saved. This will never
	 * include {@link #getNextFile()}.
	 */
	private List<LogFile> logFiles;
	
	/**
	 * Constructs a <code>LogFileList</code> with the given {@code Logger} as the
	 * origin that this list belongs to.
	 * 
	 * @param origin The {@code Logger} that this list belongs to.
	 */
	LogFileList(Logger origin) {
		this.origin = origin;
		nextFile = new LogFile(origin, this);
	}
	
	/**
	 * Returns the origin <code>Logger</code> for this {@code LogFileList}. This
	 * {@code Logger} is the logger that this {@code LogFileList} belongs to.
	 * 
	 * @return The <code>Logger</code> for this {@code LogFileList}.
	 */
	public Logger getOrigin() {
		return origin;
	}
	
	/**
	 * Returns the origin <code>Logger</code> for this {@code LogFileList}. This
	 * {@code Logger} is the logger that this {@code LogFileList} belongs to.
	 * <p>
	 * This method is equivalent to {@link #getOrigin()}.
	 * 
	 * @return The <code>Logger</code> for this {@code LogFileList}.
	 */
	public Logger getLogger() {
		return getOrigin();
	}
	
	/**
	 * Checks to see whether a {@code LogFile} in this {@code LogFileList} has the
	 * {@link LogFile#getFileName() file name} of {@code fileName}. For each one
	 * that does, it gets put into an array and returned.
	 * 
	 * @param fileName   The file name to check for.
	 * @param ignoreCase Whether to ignore the case of the letters when comparing.
	 * @return An array of all the LogFiles that have the given {@code fileName}.
	 */
	public LogFile[] getLogFiles(String fileName, boolean ignoreCase) {
		List<LogFile> accepted = new LinkedList<LogFile>();
		
		for (int i = 0; i < logFiles.size(); i++) {
			LogFile file = logFiles.get(i);
			
			if (ignoreCase ? file.getFileName().equalsIgnoreCase(fileName) : file.getFileName().equals(fileName))
				accepted.add(file);
		}
		
		return accepted.toArray(new LogFile[0]);
	}
	
	/**
	 * Checks to see whether a {@code LogFile} in this {@code LogFileList} has the
	 * filter {@code filter} in their {@code Set} of filters from their
	 * {@link LogFile#getFilters()} method. For each one that does, it gets put into
	 * an array and returned.
	 * 
	 * @param filter The filter to check for.
	 * @return An array of all the LogFiles that have the given {@code filter}.
	 */
	public LogFile[] getLogFiles(LoggerFilter filter) {
		List<LogFile> accepted = new LinkedList<LogFile>();
		
		for (int i = 0; i < logFiles.size(); i++) {
			LogFile file = logFiles.get(i);
			
			if (file.getFilters().contains(filter))
				accepted.add(file);
		}
		
		return accepted.toArray(new LogFile[0]);
	}
	
	/**
	 * Gets a {@code LogFile} depending on when it was saved. <code>0</code> for the
	 * first saved {@code LogFile} and <code>{@link #size()}</code> for the latest
	 * log.
	 * 
	 * @param index The index of the wanted log.
	 * @return A {@code LogFile} depending on when it was created.
	 */
	public LogFile getLogFile(int index) {
		return logFiles.get(index);
	}
	
	/**
	 * Returns all the log files that exist for the {@code Logger} of this
	 * {@code LogFileList} as an <code>unmodifiableList</code>. So every log file
	 * that has been successfully logged;
	 * <p>
	 * <code><strong>*WARNING*</strong></code>
	 * <p>
	 * <i> In some cases, this method may be inefficient to call abundantly because
	 * each call creates a</i> new List <i>that can be used. This may be inefficient
	 * in a memory standpoint. </i>
	 * 
	 * <p>
	 * <code><strong>*WARNING*</strong></code>
	 * 
	 * @return All the records that exist for the {@code Logger} of this
	 *         {@code LogRecordList} as an <code>unmodifiableList</code>.
	 */
	public List<LogFile> getLogFiles() {
		return Collections.unmodifiableList(logFiles);
	}
	
	/**
	 * Returns the next {@code LogFile} that will be created once the
	 * {@link LogFile#save()} method is called for this {@code LogFile} object. This
	 * method will constantly be changing value so that a new file can be accessed
	 * every time another file is saved.
	 * 
	 * @return The next {@code LogFile} that will be created once the
	 *         {@link LogFile#save()} method is called for this {@code LogFile}
	 *         object.
	 */
	public LogFile getNextFile() {
		if (nextFile == null || nextFile.isSaved()) { // will probably never be null
			if (nextFile != null)
				logFiles.add(nextFile);
			nextFile = new LogFile(origin, this);
		}
		
		return nextFile;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<LogFile> iterator() {
		return logFiles.iterator();
	}
}
