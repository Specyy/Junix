package com.junix.utils.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A <code>Logger</code> is used to log or prompt messages to this application's
 * console or log file. Loggers are typically named using a hierarchical
 * dot-separated naming convention although, doing so is not required. I
 * actually encourage the use of custom-named loggers for the uniqueness of each
 * logger.
 * <p>
 * A <code>Logger</code> instance can be obtained by calling the
 * {@link #getLogger(String)} method which either retrieves a existing logger or
 * creates a new logger.
 */
public class Logger implements com.junix.utils.Filterable<LoggerFilter> {
	
	/**
	 * A <code>Map</code> of all existing loggers and their names.
	 */
	private static final Map<String, Logger> loggers = new LinkedHashMap<String, Logger>();
	
	/**
	 * A list of all of the logging that has been done using a specific
	 * <code>Logger</code>.
	 * 
	 * @see LogRecordList
	 * @see LogRecord
	 */
	private LogRecordList logRecords;
	
	/**
	 * The identifier for this <code>Logger</code>. This cannot be changed in order
	 * to preserve the uniqueness of each {@code Logger}.
	 */
	private final String identifier;
	
	/**
	 * The options for this logger. These options will be used when logging messages
	 * to the {@link #out OutputStream} for this logger.
	 * 
	 * @see LoggerOptions
	 */
	private LoggerOptions options;
	
	/**
	 * The list of all LogFiles that can be saved from this {@code Logger}.
	 */
	private LogFileList logFileList;
	
	/**
	 * The filters used to restrict the logs shows to the user, depending on the
	 * acceptance of the filter.
	 */
	private Set<LoggerFilter> filters;
	
	/**
	 * The output stream where all normal logs (non-error logs) will be sent.
	 */
	private OutputStream out;
	
	/**
	 * The output stream where all error logs will be sent.
	 */
	private OutputStream err;
	
	/**
	 * Constructs a <code>Logger</code> with the given parameters.
	 * 
	 * @param identifier The identifier of the logger.
	 */
	private Logger(String identifier) {
		this(identifier, System.out, System.err);
	}
	
	/**
	 * Constructs a <code>Logger</code> with the given parameters.
	 * 
	 * @param identifier The identifier of the logger.
	 * @param out        The output stream where all normal logs (non-error logs)
	 *                   will be sent.
	 * @param err        The output stream where all error logs will be sent.
	 */
	private Logger(String identifier, OutputStream out, OutputStream err) {
		this.identifier = identifier;
		filters = ConcurrentHashMap.newKeySet();
		options = new LoggerOptions();
		logRecords = new LogRecordList(this);
		logFileList = new LogFileList(this);
		
		this.out = out;
		this.err = err;
		loggers.put(identifier, this);
	}
	
	/**
	 * Checks whether there is an existing <code>Logger</code> with the given
	 * identifier.
	 * 
	 * @param identifier The identifier to check.
	 * @return {@code true} if there is an existing logger with the given name or
	 *         {@code false} otherwise.
	 */
	public static boolean containsLogger(String identifier) {
		return loggers.containsKey(identifier);
	}
	
	/**
	 * Creates a <code>Logger</code> with the specified identifier if it does not
	 * already exist, otherwise retrieves a pre-existing <code>Logger</code>.
	 * 
	 * @param identifier The identifier to attach to a new <code>Logger</code> or to
	 *                   use to find a pre-existing <code>Logger</code>.
	 * @return A new <code>Logger</code> if one does not already exist with the
	 *         given identifier otherwise, a pre-exisiting <code>Logger</code>.
	 */
	public static Logger getLogger(String identifier) {
		if (containsLogger(identifier))
			return loggers.get(identifier);
		
		return new Logger(identifier);
	}
	
	/**
	 * Creates a <code>Logger</code> with the specified identifier and specified
	 * output streams if it does not already exist, otherwise retrieves a
	 * pre-existing <code>Logger</code>.
	 * 
	 * @param identifier The identifier to attach to a new <code>Logger</code> or to
	 *                   use to find a pre-existing <code>Logger</code>.
	 * @param identifier
	 * @param out        The output stream where all normal logs (non-error logs)
	 *                   will be sent.
	 * @param err        The output stream where all error logs will be sent.
	 * @return A new <code>Logger</code> if one does not already exist with the
	 *         given identifier otherwise, a pre-exisiting <code>Logger</code>.
	 */
	public static Logger getLogger(String identifier, OutputStream out, OutputStream err) {
		if (loggers.containsKey(identifier))
			return loggers.get(identifier);
		
		return new Logger(identifier, out, err);
	}
	
	/**
	 * Logs the given prompt to either the normal output stream or error output
	 * stream, depending on the level.
	 * 
	 * @param level  The level of log.
	 * @param prompt The prompt to log.
	 * @return {@code true} if the message or {@code prompt} was logged
	 *         successfully.
	 * 
	 * @see #getOutputStream() Normal output stream
	 * @see #getErrorStream() Error output stream
	 */
	public boolean log(Level level, String prompt) {
		String format = options.formatPrompt(options.getLogFormat(), level, false);
		String logMessage = options.willTranslateLog() ? options.formatPrompt(prompt, level, true) : prompt;
		String finalLog = format.replace(LoggerFormat.PROMPT.getFormat(), logMessage);
		
		if (filters != null) {
			synchronized (filters) {
				for (LoggerFilter filter : filters) {
					if (!filter.accept(format, level, logMessage, finalLog))
						return false;
				}
			}
		}
		
		if (level == Level.ERROR) {
			if (!logError(finalLog)) {
				finalLog = "An error occurred while trying to log a message!:\n " + prompt;
				
				if (!logOutput(finalLog)) {
					System.out.println(finalLog);
				}
				
				return false;
			}
		} else {
			if (!logOutput(finalLog)) {
				finalLog = "An error occurred while trying to log a message!:\n " + prompt;
				
				if (!logError(finalLog)) {
					System.out.println(finalLog);
				}
				
				return false;
			}
		}
		
		LogRecord record = new LogRecord(this, level, options.getLogFormat(), finalLog, logMessage);
		logRecords.addRecord(record);
		
		return true;
	}
	
	/**
	 * Logs the given prompt to either the normal output stream or error output
	 * stream, depending on the level, and prints a new line.
	 * 
	 * @param level  The level of log.
	 * @param prompt The prompt to log.
	 * @return {@code true} if the message or {@code prompt} was logged
	 *         successfully.
	 */
	public boolean logLine(Level level, String prompt) {
		String format = options.formatPrompt(options.getLogFormat(), level, false);
		String logMessage = options.willTranslateLog() ? options.formatPrompt(prompt, level, true) : prompt;
		String finalLog = format.replace(LoggerFormat.PROMPT.getFormat(), logMessage);
		
		if (filters != null) {
			synchronized (filters) {
				for (LoggerFilter filter : filters) {
					if (!filter.accept(format, level, logMessage, finalLog))
						return false;
				}
			}
		}
		
		if (level == Level.ERROR) {
			if (!logError(finalLog + System.lineSeparator())) {
				finalLog = "An error occurred while trying to log a message!:\n " + prompt;
				
				if (!logOutput(finalLog + System.lineSeparator())) {
					System.out.println(finalLog);
				}
				
				return false;
			}
		} else {
			if (!logOutput(finalLog + System.lineSeparator())) {
				finalLog = "An error occurred while trying to log a message!:\n " + prompt;
				
				if (!logError(finalLog + System.lineSeparator())) {
					System.out.println(finalLog + System.lineSeparator());
				}
				
				return false;
			}
		}
		
		LogRecord record = new LogRecord(this, level, options.getLogFormat(), finalLog, logMessage);
		logRecords.addRecord(record);
		
		return true;
	}
	
	/**
	 * Attempts to logs the prompt to the normal output stream.
	 * 
	 * @param prompt The prompt to log.
	 * @return Whether the log was successful. {@code true} if it was, and
	 *         {@code false} if it was not.
	 */
	private boolean logOutput(String prompt) {
		try {
			out.write(prompt.getBytes());
			out.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Attempts to logs the prompt to the error output stream.
	 * 
	 * @param prompt The prompt to log.
	 * @return Whether the log was successful. {@code true} if it was, and
	 *         {@code false} if it was not.
	 */
	private boolean logError(String prompt) {
		try {
			err.write(prompt.getBytes());
			err.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Returns the options for this logger. These options will be used when logging
	 * messages to the output streams of this logger.
	 * 
	 * @return The options for this logger.
	 * @see LoggerOptions
	 */
	public LoggerOptions getOptions() {
		return options;
	}
	
	/**
	 * Sets the options for this logger. These options will be used when logging
	 * messages to the output streams of this logger.
	 * 
	 * @param options The options that this <code>Logger</code> should use.
	 * 
	 * @see LoggerOptions
	 */
	public void setOptions(LoggerOptions options) {
		this.options = options;
	}
	
	/**
	 * Sets the normal output stream for this <code>Logger</code>. All normal logs
	 * (everything but error logs) will be sent to this logger.
	 * 
	 * @param out The output stream that this <code>Logger</code> should use for
	 *            normal logs.
	 */
	public void setOutputStream(OutputStream out) {
		try {
			if (out != null)
				this.out.close();
		} catch (IOException e) {
			logLine(Level.WARNING, "Could not close current output stream (" + this.out.toString() + ")");
		}
		
		this.out = out;
	}
	
	/**
	 * Returns the output stream where all normal logs (non-error logs) will be
	 * sent.
	 * 
	 * @return The output stream where all normal logs (non-error logs) will be
	 *         sent.
	 */
	public OutputStream getOutputStream() {
		return out;
	}
	
	/**
	 * Returns the output stream where all error log will be sent. This method will
	 * return {@code null} if this class was loaded
	 * 
	 * @return The output stream where all error logs will be sent.
	 */
	public OutputStream getErrorStream() {
		return err;
	}
	
	/**
	 * Sets the error output stream for this <code>Logger</code>. All errors logs
	 * will be sent to this logger.
	 * 
	 * @param out The output stream that this <code>Logger</code> should use for
	 *            error logs.
	 */
	public void setErrorStream(OutputStream err) {
		try {
			if (err != null)
				this.err.close();
		} catch (IOException e) {
			logLine(Level.WARNING, "Could not close current error stream (" + this.err.toString() + ")");
		}
		
		this.err = err;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof Logger) {
			return this.options.equals(((Logger) obj).options) && this.logRecords.equals(((Logger) obj).logRecords);
		}
		
		return super.equals(obj);
	}
	
	/**
	 * The identifier for this <code>Logger</code>. This cannot be changed in order
	 * to preserve the uniqueness of each {@code Logger}.
	 * 
	 * @return The identifier for this <code>Logger</code>.
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Sets the logger filters for this {@code Logger}. These filters will restrict
	 * the messages that can be logged. Once this filters are added, the
	 * {@link LoggerFilter#accept(String, Level, String) accept()} method of each
	 * filter must return true in order for a given message to be logged.
	 * 
	 * @param filters The filters for this {@code Logger}.
	 */
	@Override
	public void setFilters(Set<LoggerFilter> filters) {
		if (filters != null)
			this.filters = filters;
	}
	
	/**
	 * Adds a logger filter to the list of filters that will restrict the messages
	 * that can be logged. Once this filter is added, the
	 * {@link LoggerFilter#accept(String, Level, String) accept()} method must
	 * return true in order for a given message to be logged.
	 * 
	 * @param filter The filter to add.
	 */
	@Override
	public void addFilter(LoggerFilter filter) {
		if (filter != null)
			filters.add(filter);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFilter(LoggerFilter filter) {
		if (filter != null)
			filters.remove(filter);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<LoggerFilter> getFilters() {
		return filters;
	}
	
	/**
	 * Returns the list of all LogFiles that can be saved from this {@code Logger}.
	 * 
	 * @return The list of all LogFiles that can be saved from this {@code Logger}.
	 */
	public LogFileList getLogFileList() {
		return logFileList;
	}
	
	/**
	 * A list of all of the logging that has been done using this specific
	 * <code>Logger</code>.
	 * 
	 * @return A list of all logs that have been made on this logger.
	 * @see LogRecordList
	 * @see LogRecord
	 */
	public LogRecordList getLogRecords() {
		return logRecords;
	}
	
	/**
	 * The <code>Level</code> enumeration defines a set of standard logging levels
	 * that can be used to detail logging output. The logging <code>Level</code>
	 * items are ordered and are specified by ordered integers.
	 * <p>
	 * Clients should normally use the predefined Level constants such as
	 * {@link Level#INFO}.
	 * <p>
	 * The levels in descending order are:
	 * <ul>
	 * <li>SERVER (highest value: 3)</li>
	 * <li>CLIENT</li>
	 * <li>INFO</li>
	 * <li>WARNING</li>
	 * <li>ERROR (lowest value: -1)</li>
	 * </ul>
	 */
	public enum Level {
		/* All the logging levels */
		
		/**
		 * Lowest value <code>(-1)</code> used for logging error messages. Logs with
		 * this level will no longer be used in the default output stream but the error
		 * output stream defined via the {@link Logger#setErrorStream(OutputStream)}
		 * method.
		 */
		ERROR(-1, "ERROR"),
		
		/**
		 * Second to lowest value <code>(0)</code> which logs just at the surface of the
		 * {@link Logger#getOutputStream() normal output stream logger}. Used to log
		 * warnings to the user.
		 */
		WARNING(0, "WARNING"),
		
		/**
		 * Default value <code>(1)</code> used to log info messages to the
		 * {@link Logger#getOutputStream() normal output stream logger}. This
		 * {@code Level} should be used for logging any normal messages or any type of
		 * message that gives information about the application as a whole, but should
		 * not be used to show the inner-workings of the application, unless a form of
		 * debug mode is active.
		 */
		INFO(1, "INFO"),
		
		/**
		 * Second to highest value <code>(2)</code> used for logging messages about the
		 * client itself with fine detail to the {@link Logger#getOutputStream() normal
		 * output stream logger}. This {@code Level} should be used to explain the
		 * inner-workings of the program or application.
		 */
		CLIENT(2, "CLIENT"),
		
		/**
		 * Highest value <code>(3)</code> used to log detailed messages about the server
		 * or to the server. This {@code Level} this level can be used to transfer
		 * information through a client and a server for simple data transfer. This is
		 * the highest level because of the amount of possible risk with the transfer of
		 * data through the client and server.
		 */
		SERVER(3, "SERVER");
		
		/**
		 * The value of this {@code Level} instance.
		 */
		private final int id;
		
		/**
		 * The message that will be used to signify this {@code Level} of log.
		 */
		private String prompt;
		
		/**
		 * Construct a {@code Level}.
		 * 
		 * @param id     The level of this {@code Level} instance.
		 * @param prompt The message that will be used to signify this {@code Level} of
		 *               log.
		 */
		private Level(final int id, String prompt) {
			this.id = id;
			this.prompt = prompt;
		}
		
		/**
		 * Parses a {@code Level} with the given {@code id}, which is the value of the
		 * wanted {@code Level}.
		 * 
		 * @param id The value of the {@code Level} instance.
		 * @return The {@code Level} associated with the given value; {@code null}
		 *         otherwise.
		 */
		public static Level parse(int id) {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].id == id)
					return values()[i];
			}
			
			return null;
		}
		
		/**
		 * Parses a {@code Level} with the given {@code level}, which is the name of the
		 * wanted {@code Level}.
		 * 
		 * @param level The name of the level that you would wish to parse;
		 *              case-insensitive.
		 * @return The {@code Level} associated with the given name; {@code null}
		 *         otherwise.
		 */
		public static Level parse(String level) {
			return valueOf(level.trim().toUpperCase());
		}
		
		/**
		 * Returns the message that will be used to signify this {@code Level} of log.
		 * 
		 * @return The message that will be used to signify this {@code Level} of log.
		 */
		public String getPrompt() {
			return prompt;
		}
		
		/**
		 * Sets the message that will be used to signify this {@code Level} of log.
		 * 
		 * @param prompt The message that will be used to signify this {@code Level} of
		 *               log.
		 */
		public void setPrompt(String prompt) {
			this.prompt = prompt;
		}
		
		/**
		 * Returns the message that will be used to signify this {@code Level} of log.
		 * 
		 * @return The message that will be used to signify this {@code Level} of log.
		 */
		@Override
		public String toString() {
			return getPrompt();
		}
	}
}
