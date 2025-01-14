package com.junix.utils.logging;

import com.junix.utils.logging.Logger.Level;

/**
 * A <code>LogRecord</code> is a record that is taken of each log that is
 * displayed for each {@code Logger}. So each message that is sent to a
 * {@code Logger} and is successfully logged (logged without any errors), will
 * have a {@code LogRecord} recorded for that successful log and that record
 * will be added to a list of all the recorded successful records for that
 * {@code Logger}. This list is a {@link LogRecordList} and is available via the
 * {@link Logger#getLogRecords()} method.
 * 
 * @see LogRecordList
 * @see Logger
 */
public final class LogRecord {
	
	/**
	 * The {@link Level} of the log.
	 */
	private Logger.Level level;
	
	/**
	 * The {@code Logger} that was used to log this record's message.
	 */
	private Logger logger;
	
	/**
	 * The message that was logged by the {@code Logger} which belongs to this
	 * record. This is the translated <i>(translated as in properly formatted text
	 * with all the {@code LoggerFormat} placeholders replaced by their real
	 * value)</i> message.
	 */
	private String log;
	
	/**
	 * The format of the log. See more at {@link LoggerFormat}.
	 */
	private String format;
	
	/**
	 * The actual message that was sent to the {@code Logger}, excluding the
	 * {@code LoggerFormat}.
	 */
	private String logMessage;
	
	/**
	 * Constructs a <code>LogRecord</code> for a given log that was displayed. That
	 * log's information can be passed through this constructor.
	 * 
	 * @param logger     The {@code Logger} that was used to log this record's
	 *                   message.
	 * @param level      The {@link Level} of the log.
	 * @param format     The format of the log. See more at {@link LoggerFormat}.
	 * @param log        The message that was logged by the {@code Logger} which
	 *                   belongs to this record.
	 * @param logMessage The actual message that was sent to the {@code Logger},
	 *                   excluding the {@code LoggerFormat}.
	 * 					
	 */
	LogRecord(Logger logger, Logger.Level level, String format, String log, String logMessage) {
		this.logger = logger;
		this.level = level;
		this.format = format;
		this.logMessage = logMessage;
		this.log = log;
	}
	
	/**
	 * Returns the format of the log. This method returns how the log was displayed.
	 * See more about logging format in the {@link LoggerFormat} class.
	 * 
	 * @return The format of the log.
	 * @see LoggerFormat
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * Returns the {@code Logger} that was used to log this record's message.
	 * 
	 * @return The {@code Logger} that was used to log this record's message.
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Returns the {@link Logger.Level} of the log. These will be one of the
	 * predefined {@code Level} constants from the {@link Logger.Level} enumaration.
	 * You can check which output the log came out from (error output or standard
	 * output) by checking the level of the log. To learn more about logging levels,
	 * see {@link Logger.Level}.
	 * 
	 * @return The {@link Level} of the log.
	 * @see Logger.Level
	 */
	public Logger.Level getLevel() {
		return level;
	}
	
	/**
	 * Returns the actual message that was sent to the {@code Logger}, excluding the
	 * {@code LoggerFormat}.
	 * 
	 * @return The actual message that was sent to the {@code Logger}, excluding the
	 *         {@code LoggerFormat}.
	 */
	public String getLogMessage() {
		return logMessage;
	}
	
	/**
	 * Returns the message that was logged by the {@code Logger} which belongs to
	 * this record. This is the translated <i>(translated as in properly formatted
	 * text with all the {@code LoggerFormat} placeholders replaced by their real
	 * value)</i> message.
	 * 
	 * @return The message that was logged by the {@code Logger} which belongs to
	 *         this record.
	 */
	public String getLog() {
		return log;
	}
}
