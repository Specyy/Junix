package com.junix.utils.logging;

/**
 * A <code>LoggerFilter</code> is interface used by the {@link LogFile} and
 * {@link Logger} classes for filtering a set of logs shown to the user or logs
 * to be sent to a custom log file. See {@link LogFile} for an implementation
 * that filters using the {@code LogRecords}.
 * <p>
 * A <code>LoggerFilter</code> can be set on a <code>JFileChooser</code> to keep
 * unwanted messages from logging to a {@code LogFile}.
 *
 * @see LogFile
 */
@FunctionalInterface
public interface LoggerFilter {
	/**
	 * Whether the given log is accepted by this filter.
	 * 
	 * @param format     The format of the log. This can be changed via the
	 *                   {@link LoggerOptions#setLogFormat(String)} method by using
	 *                   a different {@link LoggerFormat}.
	 * @param level      The level of that logger. See {@link Logger.Level}
	 * @param logMessage The actual message that was sent to the {@code LogFile} or
	 *                   {@code Logger}, excluding the format.
	 * @param log        The final message that was actually logged by the
	 *                   {@code Logger}, including the format.
	 * @return Returns {@code true} if the log is to be accepted.
	 */
	boolean accept(final String format, final Logger.Level level, final String logMessage, final String log);
}
