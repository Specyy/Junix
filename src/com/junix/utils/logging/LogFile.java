/**
 * 
 */
package com.junix.utils.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.junix.hub.JunixHub;
import com.junix.utils.Filterable;
import com.junix.utils.logging.Logger.Level;

/**
 * A <code>LogFile</code> is used to create a file containing all the logs that
 * have been logged for a specific {@code Logger}. Each {@code Logger} has its
 * own {@link LogFileList} and LogFiles can only be accessed through any
 * Logger's {@code LogFileList}. All logs that have been logged by that
 * {@code Logger} will be written in this {@code LogFile}, even error logs.
 * <p>
 * This class is a representation of the log file that will be created whenever
 * the {@link #save()} method is called. Additionally, this {@code LogFile}
 * could also be wrapped in a zip file depending on whether the
 * {@link #willKeepZipFile()} returns {@code true} or not. This zip file can be
 * created by writing this line of code: {@link #setKeepZipFile(boolean)
 * setKeepZipFile(true)}. The zip file will have the same name as this
 * {@code LogFile}, which can be changed via the {@link #setFileName(String)}.
 * The {@link LogFileZip} class is a representation of the zip file that could
 * be created.
 * <p>
 * This class is final because there should be no class that inherits the
 * methods and also to ensure that the only way to access a
 * <code>LogFile</code>, is by using the {@link LogFileList#getNextFile()}
 * method.
 * 
 * @see Logger
 * @see LogFileZip
 * @see LogFileList
 */
public final class LogFile implements Filterable<LoggerFilter> {
	
	/**
	 * The default name that each file name will be derived from. Now, each file's
	 * name will be different because the name will give information about what day
	 * the file was made on. For example, a file that was made on Thursday, April
	 * 30th, 2020 will have the date of <code>"2020-04-30"</code> and will stored
	 * like that unless there is already a file name the same name. In that case, an
	 * extra number will be added to the end of the file name (ex. "-1" if it's the
	 * second file with that name) depending on how many files there are with the
	 * same name.
	 */
	public static final String DEFAULT_FILE_NAME = LoggerFormat.YEAR + "-" + LoggerFormat.MONTH + "-" + LoggerFormat.DAY_OF_MONTH;
	
	/**
	 * The default directory where each log file will be created. Wherever this
	 * project is placed, a folder named {@code "logs"} will be created in the same
	 * directory and all the log files will be placed in that directory. These log
	 * files may be in a zip file depending on whether the
	 * {@link #willKeepZipFile()} method returns {@code true} or not.
	 */
	public static final File DEFAULT_DIRECTORY = new File("logs");
	
	/**
	 * The <code>Logger</code> where this log files' contents originated.
	 */
	private final Logger origin;
	
	/**
	 * The content of the actual log file stored as a String. Note that if the log
	 * file gets edited, this content variable will not change its content to the
	 * content of the file. This is why each log file will have the read-only
	 * property.
	 */
	private String content = "";
	
	/**
	 * The desired name that this {@code LogFile} will have. In the case that there
	 * is already a file name the same name, an extra number will be added to the
	 * end of the file name (ex. "-1" if it's the second file with that name)
	 * depending on how many files there are with that same name. If a zip file will
	 * be created with this file, in that case this name will be referencing inside
	 * the zip file and not the actual directory that the zip file is in. Adding
	 * directories in this name would be the only way of creating folders in the zip
	 * file.
	 * <p>
	 * You may use {@link LoggerFormat LoggerFormats} to translate information about
	 * time.
	 */
	private String fileName;
	
	/**
	 * The <code>File</code> where all the content of the logs that have been stored
	 * will go. This is the main file and is the file that this class represents.
	 */
	private File logFile;
	
	/**
	 * The directory where this log file will be created. Wherever this project is
	 * placed, a folder with the same name as {@code dir} will be created (or used
	 * if one already exists) in the same directory and this log file will be placed
	 * in that directory. This log file may be inside a zip file depending on
	 * whether the {@link #willKeepZipFile()} method returns {@code true} or not.
	 * Note that this variable is not involved with the creation of folders in the
	 * zip file but rather the folders outside of that zip file. To add or remove
	 * folders in the zip file, using the {@link #setFileName(String)} would be the
	 * most efficient way to do so.
	 */
	private File dir;
	
	/**
	 * The zip file that will be attached to this {@code LogFile} if
	 * {@link LoggerOptions#willKeepLogFileZip()} returns {@code true}. This zip
	 * file will contain the actual logs that were sent to this class'
	 * {@link #getOrigin() origin logger}. If no zip file is present or
	 * {@link LoggerOptions#willKeepLogFileZip()} returns {@code false}, this will
	 * be {@code null}.
	 * 
	 * @see LogFileList
	 */
	private LogFileZip zipFile;
	
	/**
	 * Whether this <code>LogFile</code> should store the file's data into a zip
	 * file.
	 * 
	 * @see LogFileList
	 * @see Logger
	 */
	private boolean keepZipFile;
	
	/**
	 * Whether to log errors that happen during the creation of this log file (and
	 * the {@link #getZipFile() zip file} if one should be created) to the
	 * {@link #getOrigin() origin logger}.
	 */
	private boolean logErrorsToOrigin;
	
	/**
	 * Whether this zip file has been created yet or not. Note that this will still
	 * be {@code false} if the {@link #getZipFile()} file already exists because
	 * another file will be created (possbily with a "-1" at the end of the name,
	 * depending on how many files have the same names (or that start with the same
	 * name).
	 */
	private boolean saved;
	
	/**
	 * The LoggerFilters that will be used to filter each log that gets sent to this
	 * <code>LogFile</code>. The all these filters must accept each log that will be
	 * written into the file or it will not be written into the file. There is also
	 * another set of filters in the <code>Logger</code> class that will also act as
	 * another filter to refine the logged messages to the user's liking.
	 * 
	 * @see Filterable
	 */
	private Set<LoggerFilter> filters;
	
	/**
	 * The list that this {@code LogFile} comes from.
	 * 
	 * @see LogFileList
	 */
	private LogFileList list;
	
	/**
	 * Constructs a <code>LogFile</code> with the given {@code Logger} as the origin
	 * of where this file's contents originated. This instance might also have a zip
	 * file attached to it. This zip file will be where the actual log file gets
	 * stored although that is only possible if and only if the
	 * {@link #willKeepZipFile()} method returns {@code true}.
	 * <p>
	 * This method also requires a {@code LogFileList} where this log file can be
	 * accessed from.
	 * 
	 * @param origin The {@code Logger} where this file's contents originated.
	 * @param list   The {@code LogFileList} for this {@code LogFile}
	 */
	LogFile(Logger origin, LogFileList list) {
		this.origin = origin;
		this.list = list;
		this.filters = ConcurrentHashMap.newKeySet();
		this.fileName = DEFAULT_FILE_NAME;
		this.dir = DEFAULT_DIRECTORY;
		zipFile = new LogFileZip(this, null);
	}
	
	/**
	 * Saves the content of this {@code LogFile} into an actual file. If the
	 * {@link #willKeepZipFile()} returns {@code true}, this method will also create
	 * a zip file where the actual log file will get stored. Otherwise, this method
	 * will only create one log file with the contents of the logs that have been
	 * recorded in the {@link #getOrigin() origin logger}.
	 * <p>
	 * This method will return true if the file (and the zip file if one will be
	 * created) were created successfully and without any errors. For any errors
	 * that are present during the creation of these files, this method will return
	 * {@code false} and information about the error will be logged to the
	 * {@link #getOrigin() origin logger} as an {@link Logger.Level#ERROR error
	 * log}.
	 * 
	 * @return {@code true} if the operation completed successfully; {@code false}
	 *         otherwise.
	 */
	public synchronized boolean save() {
		JunixHub hub = null;
		
		try {
			hub = (JunixHub) JunixHub.tryAccess();
		} catch (IllegalAccessException e2) {}
		
		// Null checks
		if (zipFile == null || dir == null)
			return false;
		
		String formattedFileName = origin.getOptions().formatPrompt(getFileName(), null, true);
		if (willKeepZipFile()) {
			// Create the zip file
			File zip = new File(dir, formattedFileName + ".zip");
			logFile = new File(dir, formattedFileName + ".log");
			
			if (!dir.exists())
				dir.mkdirs();
			
			String[] zipParentFiles = null;
			
			if (zip.exists()) {
				zipParentFiles = dir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.matches("^(" + Pattern.quote(getFileName()) + ")(" + Pattern.quote("-") + "\\d+)?"
								+ Pattern.quote(".zip") + "$");
					}
				});
			}
			
			// If there is an existing zip file (if one has already called the save() method
			// on this {@code LogFile}), change the name to match the amount of files.
			if (zipParentFiles != null && zipParentFiles.length > 0) {
				zip = new File(dir, formattedFileName + "-" + zipParentFiles.length + ".zip");
				logFile = new File(dir, formattedFileName + "-" + zipParentFiles.length + ".log");
			}
			
			// Create all files
			zipFile.zipFile = zip;
			zipFile.createFiles();
		} else {
			logFile = new File(dir, formattedFileName + ".log");
			
			if (!dir.exists())
				dir.mkdirs();
			
			String[] zipParentFiles = null;
			
			if (logFile.exists()) {
				zipParentFiles = dir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.matches("^(" + Pattern.quote(formattedFileName) + ")(" + Pattern.quote("-") + "\\d+)?"
								+ Pattern.quote(".log") + "$");
					}
				});
			}
			
			if (zipParentFiles != null && zipParentFiles.length > 0) {
				logFile = new File(dir, formattedFileName + "-" + zipParentFiles.length + ".log");
			}
			
			if (!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					origin.logLine(Level.ERROR, "Could not create log file: \"" + getFileName() + "\""
							+ (hub == null || !hub.debugMode() ? "" : e.getMessage()));
					return false;
				}
			}
			
			try (FileOutputStream out = new FileOutputStream(logFile)) {
				if (content == null || content.isEmpty()) {
					LogRecordList list = getOrigin().getLogRecords();
					
					logOutputLoop:
					for (int i = 0; i < list.size(); i++) {
						LogRecord record = list.getRecord(i);
						
						for (LoggerFilter filter : filters) {
							if (!filter.accept(record.getFormat(), record.getLevel(), record.getLogMessage(), record.getLog())) {
								continue logOutputLoop;
							}
						}
						
						String line = new String(record.getLog()) + System.lineSeparator();
						content += line;
						
						out.write(line.getBytes());
					}
				} else {
					out.write(content.getBytes());
				}
			} catch (IOException e) {
				origin.logLine(Level.ERROR, "Could not save log file: \"" + getFileName() + "\""
						+ (hub == null || !hub.debugMode() ? "" : e.getMessage()));
			} finally {
				logFile.setReadOnly();
			}
			
			saved = true;
		}
		
		// TODO Free Memory
		if (hub != null)
			hub.freeMemory();
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<LoggerFilter> getFilters() {
		return filters;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFilters(Set<LoggerFilter> filters) {
		this.filters = filters;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addFilter(LoggerFilter filter) {
		filters.add(filter);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFilter(LoggerFilter filter) {
		filters.remove(filter);
	}
	
	/**
	 * Returns the origin <code>Logger</code> for this {@code LogFile}. This
	 * {@code Logger} is where all the logs for this {@code LogFile} are being sent
	 * from.
	 * 
	 * @return The <code>Logger</code> where this log files' contents originated.
	 */
	public Logger getOrigin() {
		return origin;
	}
	
	/**
	 * Returns the origin <code>Logger</code> for this {@code LogFile}. This
	 * {@code Logger} is where all the logs for this {@code LogFile} are being sent
	 * from.
	 * <p>
	 * This method is equivalent to {@link #getOrigin()}.
	 * 
	 * @return The <code>Logger</code> where this log files' contents originated.
	 */
	public Logger getLogger() {
		return getOrigin();
	}
	
	/**
	 * Returns the list that this {@code LogFile} came from.
	 * 
	 * @return The list that this {@code LogFile} came from.
	 */
	public LogFileList getList() {
		return list;
	}
	
	/**
	 * The <code>File</code> where all the content of the logs that have been stored
	 * will go. This is the main file and is the file that this class represents.
	 * 
	 * @return The <code>File</code> associated with this class.
	 */
	public File getFile() {
		return logFile;
	}
	
	/**
	 * Returns the zip file that will be attached to this {@code LogFile} if
	 * {@link LoggerOptions#willKeepLogFileZip()} returns {@code true}. This zip
	 * file will contain the actual logs that were sent to this class'
	 * {@link #getOrigin() origin logger}. If no zip file is present or
	 * {@link LoggerOptions#willKeepLogFileZip()} returns {@code false}, this method
	 * will return {@code null}.
	 * 
	 * @return Returns the zip file that is associated with this {@code LogFile} if
	 *         {@link LoggerOptions#willKeepLogFileZip()} returns {@code true};
	 *         otherwise this method will return {@code null}.
	 * 		
	 * @see LogFileList
	 */
	public LogFileZip getZipFile() {
		return zipFile;
	}
	
	// TODO ADD TEXT.class NAME
	/**
	 * Sets the name that this {@code LogFile} will have. In the case that there is
	 * already a file with the same name, an extra number will be added to the end
	 * of the file name (ex. "-1" if it's the second file with that name) depending
	 * on how many files there are with that same name. If a zip file will be
	 * created with this file, in that case this name will be refrencing inside the
	 * zip file and not the actual directory where zip file will be placed. Putting
	 * directories in this name (by using {@link File#separator} to differentiate
	 * each level) would be the only way of creating folders in the zip file.
	 * <p>
	 * You may use {@link LoggerFormat LoggerFormats} to translate information about
	 * time.
	 * 
	 * @param name The desired name that this {@code LogFile} will have.
	 */
	public void setFileName(String name) {
		this.fileName = name;
	}
	
	/**
	 * Returns the desired name that this {@code LogFile} will have. In the case
	 * that there is already a file name the same name, an extra number will be
	 * added to the end of the file name (ex. "-1" if it's the second file with that
	 * name) depending on how many files there are with that same name.
	 * 
	 * @return The file name for this {@link LogFile}.
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Sets the directory where this log file will be created. Wherever this project
	 * is placed, a folder with the same name as {@code dir} will be created (or
	 * used if one already exists) in the same directory and this log file will be
	 * placed in that directory. This log file may be inside a zip file depending on
	 * whether the {@link #willKeepZipFile()} method returns {@code true} or not.
	 * 
	 * @param dir The directory where this log file (and/or zip file if one should
	 *            be created) will be created.
	 */
	public void setDirectory(File dir) {
		this.dir = dir;
	}
	
	/**
	 * Returns the directory where this log file will be created. If the
	 * {@link #willKeepZipFile()} method returns {@code true}, this method will also
	 * return where the zip file for this {@code LogFile} will be created. Note that
	 * this method (and the {@link #setDirectory(File)} method) are not involved
	 * with the creation of folders in the zip file but rather the folders outside
	 * of that zip file. To add or remove folders in the zip file, using the
	 * {@link #setFileName(String)} would be the most efficient way to do so. You
	 * could also try accessing the {@link LogFileZip#getZipFile()} method to make
	 * edits with the file at hand.
	 * 
	 * @return The directory where this log file (and/or zip file if one should be
	 *         created) will be created.
	 */
	public File getDirectory() {
		return dir;
	}
	
	/**
	 * Sets whether this <code>LogFile</code> should store the file's data into a
	 * zip file.
	 * 
	 * @param keep {@code true} if the zip file should be kept; {@code false}
	 *             otherwise.
	 * 
	 * @see LogFile
	 * @see LogFileList
	 * @see Logger
	 */
	public void setKeepZipFile(boolean keep) {
		this.keepZipFile = keep;
	}
	
	/**
	 * Returns whether this <code>LogFile</code> should store the file's data into a
	 * zip file.
	 * 
	 * @return {@code true} if the zip file should be kept; {@code false} otherwise.
	 * 
	 * @see LogFileList
	 * @see Logger
	 */
	public boolean willKeepZipFile() {
		return keepZipFile;
	}
	
	/**
	 * Sets whether to log errors that happen during the creation of this log file
	 * (and the {@link #getZipFile() zip file} if one should be created) to the
	 * {@link #getOrigin() origin logger}.
	 * 
	 * @param log Whether to log errors that happen during the creation of files to
	 *            the {@link #getOrigin() origin logger}.
	 */
	public void setLogErrorsToOrigin(boolean log) {
		this.logErrorsToOrigin = log;
	}
	
	/**
	 * Returns whether to log errors that happen during the creation of this log
	 * file (and the {@link #getZipFile() zip file} if one should be created) to the
	 * {@link #getOrigin() origin logger}.
	 * 
	 * @return {@code true} if errors should be logged; {@code false} otherwise.
	 */
	public boolean willLogErrorsToOrigin() {
		return logErrorsToOrigin;
	}
	
	/**
	 * Returns whether this log file has been saved yet or not. Note that this will
	 * still be {@code false} even if the {@link #getFile()} file already exists
	 * because another file will be created (possibly with a "-1" at the end of the
	 * name, depending on how many files have the same names (or that start with the
	 * same name).
	 * 
	 * @return {@code true} if this log has already been saved; {@code false}
	 *         otherwise.
	 */
	public boolean isSaved() {
		return saved;
	}
	
	/**
	 * Returns the content of the actual log file stored as a String. Note that if
	 * the log file gets edited, this content method will not return what is in the
	 * file. This is why each log file will have the read-only property.
	 * 
	 * @return The content of the actual log file stored as a String.
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * Returns the content of the actual log file stored as a String. Note that if
	 * the log file gets edited, this content method will not return what is in the
	 * file. This is why each log file will have the read-only property.
	 * 
	 * @return The content of the actual log file stored as a String.
	 */
	@Override
	public String toString() {
		return getContent();
	}
	
	/**
	 * This <code>LogFileZip</code> represents the zip file the can be created for
	 * each {@code LogFile}. This zip file will only be created if the
	 * {@link LogFile#willKeepZipFile()} returns {@code true}. If that method
	 * returns {@code true}, a {@link LogFile} will be attached to this class and
	 * will be accessible via the {@link #getLogFile()} method.
	 * <p>
	 * Note that this zip file will also be created with the same name as its
	 * attached {@code LogFile}.
	 * 
	 * @see LogFile
	 */
	private static class LogFileZip {
		/**
		 * The {@code LogFile} that will be stored inside this zip file.
		 */
		private LogFile root;
		
		/**
		 * The zip file that this class represents as a {@link java.io.File}.
		 */
		private File zipFile;
		
		/**
		 * Constructs a {@code LogFileZip} with the underlying parameters.
		 * 
		 * @param root    The {@code LogFile} that this zip file will contain.
		 * @param zipFile The {@link java.io.File} representation of this
		 *                {@code LogFileZip}.
		 */
		private LogFileZip(LogFile root, File zipFile) {
			this.root = root;
			this.zipFile = zipFile;
		}
		
		/**
		 * Creates the zip file and all the files inside the zip file.
		 */
		private void createFiles() {
			if (!zipFile.exists()) {
				try {
					zipFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
				{
					ZipEntry logFileEntry = new ZipEntry(root.logFile.getPath());
					out.putNextEntry(logFileEntry);
					if (root.content == null || root.content.isEmpty()) {
						LogRecordList list = root.getOrigin().getLogRecords();
						
						logListLoop:
						for (int i = 0; i < list.size(); i++) {
							LogRecord record = list.getRecord(i);
							
							synchronized (root.getFilters()) {
								for (LoggerFilter filter : root.getFilters()) {
									if (!filter.accept(record.getFormat(), record.getLevel(), record.getLogMessage(), record
											.getLog())) {
										continue logListLoop;
									}
								}
							}
							
							String line = new String(record.getLog().getBytes()) + System.lineSeparator();
							root.content += line;
							
							out.write(line.getBytes());
						}
					} else {
						out.write(root.content.getBytes());
					}
					
					out.closeEntry();
				}
			} catch (IOException e) {
				// TODO IS DEBUG
				e.printStackTrace();
			}
			
			// Make sure files are now read-only
			root.logFile.setReadOnly();
			zipFile.setReadOnly();
			
			root.saved = true;
		}
		
		/**
		 * Returns the {@code LogFile} that will be stored inside this zip file. This is
		 * the class representation of that log file.
		 * 
		 * @return The {@code LogFile} that will be stored inside this zip file.
		 */
		public LogFile getLogFile() {
			return root;
		}
		
		/**
		 * The zip file that this class represents as a {@link java.io.File}. This is
		 * the actual "file", including it's path and parents. Note that this may not be
		 * the same as {@link LogFile#getDirectory()} + {@link File#separator} +
		 * {@link LogFile#getFileName()} because the file name may have an extra number
		 * (e.g. "-1") depending on how many files there are with the same name as
		 * {@link LogFile#getFileName()}.
		 * 
		 * @return The zip file that this class represents as a {@link java.io.File}.
		 */
		public File getZipFile() {
			return zipFile;
		}
	}
}
