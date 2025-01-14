package com.junix.utils.logging;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <code>LoggerOptions</code> are options used to change the output of a
 * {@code Logger}. Each {@code Logger} instance will have a
 * {@code LoggerOptions} instance attached to it that the user can use to modify
 * the way messages are output by the {@code Logger}.
 * <p>
 * In the class, much can be accomplished. For example, the thing that will most
 * likely make the use of this class abundant, is how one can change the format
 * the of the log. Now, there is a default log format that can be identified by
 * the {@link #DEFAULT_LOG_FORMAT} although, I suggest changing the log format
 * to your own unique format because the default log format does not give enough
 * information and is only there to be used as a template format for the
 * creation of your own. This class also provides all the translations of all
 * the {@link LoggerFormat LoggerFormats}.
 * <p>
 * I recommend that if you create a <code>Logger</code>, that you should edit at
 * least one setting or option for this class to fit your application; which
 * will most likely be the log format.
 * 
 * @see Logger
 */
public class LoggerOptions {
	
	/**
	 * The default amount of indent that will be prompted in the output of a message
	 * log from a {@code Logger}. If this value is set to {@code 3}, the
	 * {@link #DEFAULT_INDENT_STRING} will be repeated {@code 3} times at the
	 * beginning of the prompt to act as an indent (even before the
	 * {@link #getLogFormat() format information}).
	 * <p>
	 * The default value is {@code 0}.
	 * 
	 * @see #getIndent()
	 */
	public static final int DEFAULT_INDENT = 0;
	
	/**
	 * The default <code>String</code> used as an indent in a logged message. This
	 * indent will be repeated {@value #DEFAULT_INDENT} time(s) as the start of each
	 * log (even before the {@link #getLogFormat() format information}).
	 * <p>
	 * The default value is {@code "\t"}.
	 * 
	 * @see #getIndentString()
	 */
	public static final String DEFAULT_INDENT_STRING = "\t";
	
	/**
	 * The default log format for each message that will be logged. This determines
	 * how the logged messages will be displayed by the {@code Logger} and can give
	 * other information such as the {@link Logger.Level logging level}, and
	 * information about {@link LoggerFormat time}. For all possibilities, visit
	 * {@link LoggerFormat}.
	 * 
	 * @see LoggerFormat
	 */
	public static final String DEFAULT_LOG_FORMAT = "[" + LoggerFormat.YEAR + ":" + LoggerFormat.MONTH + ":" + LoggerFormat.WEEK_OF_MONTH + ":" + LoggerFormat.DAY_OF_MONTH + ":"
			+ LoggerFormat.HOUR12 + ":" + LoggerFormat.MINUTE + ":" + LoggerFormat.SECOND + " " + LoggerFormat.HOUR_AM_PM + " - " + LoggerFormat.LEVEL + "] " + LoggerFormat.PROMPT;
	
	/**
	 * The default text that will be used if the {@link LoggerFormat#HOUR_AM_PM}
	 * value is used and current hour is between {@code 0:00 - 11:59}.
	 * <p>
	 * The default value is {@code "AM"}.
	 */
	public static final String DEFAULT_AM_HOUR_TEXT = "AM";
	
	/**
	 * The default text that will be used if the {@link LoggerFormat#HOUR_AM_PM}
	 * value is used and current hour is between {@code 12:00 - 23:59}.
	 * <p>
	 * The default value is {@code "PM"}.
	 */
	public static final String DEFAULT_PM_HOUR_TEXT = "PM";
	
	/**
	 * The amount of times indent that will be prompted at the start of the output
	 * of a message log from a {@code Logger}. If this value is set to {@code 3},
	 * the {@link #DEFAULT_INDENT_STRING} will be repeated {@code 3} times at the
	 * beginning of the prompt to act as an indent (even before the
	 * {@link #getLogFormat() format information}).
	 */
	private int indent;
	
	/**
	 * The <code>String</code> used as an indent in a logged message. This indent
	 * will be repeated {@value #DEFAULT_INDENT} time(s) as the start of each log
	 * (even before the {@link #getLogFormat() format information}).
	 */
	private String indentString;
	
	/**
	 * The prefix for each log. This will be written before everything in the log
	 * (except the indent).
	 */
	private String logPrefix = "";
	
	/**
	 * The suffix for each log. This will be after everything.
	 */
	private String logSuffix = "";
	
	/**
	 * The text that will be used if the {@link LoggerFormat#HOUR_AM_PM} value is
	 * used and current hour is between {@code 0:00 - 11:59}.
	 */
	private String amHourText;
	
	/**
	 * The text that will be used if the {@link LoggerFormat#HOUR_AM_PM} value is
	 * used and current hour is between {@code 12:00 - 23:59}.
	 */
	private String pmHourText;
	
	/**
	 * The log format for each message that will be logged. This determines how the
	 * logged messages will be displayed by the {@code Logger} and can give other
	 * information such as the {@link Logger.Level logging level}, and information
	 * about {@link LoggerFormat time}. For all possibilities, visit
	 * {@link LoggerFormat}.
	 */
	private String logFormat;
	
	/**
	 * The title of the application that can be displayed in each log. This is meant
	 * to be used as a sort of identifier for each log for anybody who sees them.
	 * This title will tell them from where the log is coming from (from the
	 * application of from the servers, for example).
	 */
	private String title = "";
	
	/**
	 * Whether the {@link LoggerFormat LoggerFormats} used in a logged message
	 * should be translated to their proper value.
	 */
	private boolean translateLog;
	
	/**
	 * Creates a new instance of this class that can be used to change
	 * {@code Logger} options. Using the {@link Logger#setOptions(LoggerOptions)
	 * Logger.setOptions(LoggerOptions)} method can be useful if you wish for a
	 * {@code Logger} to follow these set of instructions from this instance.
	 */
	public LoggerOptions() {
		indent = DEFAULT_INDENT;
		indentString = DEFAULT_INDENT_STRING;
		logFormat = DEFAULT_LOG_FORMAT;
		
		amHourText = DEFAULT_AM_HOUR_TEXT;
		pmHourText = DEFAULT_PM_HOUR_TEXT;
		
		translateLog = false;
	}
	
	/**
	 * Translates all possible values of placeholders from {@link LoggerFormat}. You
	 * must choose whether what you entered in the {@code prompt} is the actual
	 * message that wants to be sent (not the format) because this method will
	 * format the prompt accordingly (no indent will be added before the prompt).
	 * 
	 * @param prompt The text to be translated.
	 * @param level  The level of the log.
	 * @param log    Whether the {@code prompt} is the actual message that wants to
	 *               be sent (not the format).
	 * @return Returns the translated message or {@code prompt}.
	 */
	String formatPrompt(String prompt, Logger.Level level, boolean log) {
		{
			ZonedDateTime time = ZonedDateTime.now();
			Calendar cal = Calendar.getInstance();
			cal.setMinimalDaysInFirstWeek(1);
			String logFormat = prompt;
			
			if (level != null)
				logFormat = prompt.replace(LoggerFormat.LEVEL.getFormat(), level.getPrompt());
			logFormat = logFormat.replace(LoggerFormat.TITLE.getFormat(), title);
			logFormat = logFormat.replace(LoggerFormat.MONTH.getFormat(), time.getMonthValue() >= 10 ? String.valueOf(time.getMonthValue()) : "0" + time.getMonthValue());
			logFormat = logFormat.replace(LoggerFormat.MONTH_NAME.getFormat(), cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
			logFormat = logFormat.replace(LoggerFormat.WEEK_OF_MONTH.getFormat(), "" + cal.get(Calendar.WEEK_OF_MONTH));
			logFormat = logFormat.replace(LoggerFormat.YEAR.getFormat(), String.valueOf(time.getYear() >= 10 ? time.getYear() : "0" + time.getYear()));
			logFormat = logFormat.replace(LoggerFormat.WEEK_OF_YEAR.getFormat(), String
					.valueOf(time.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) >= 10 ? time.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) : "0" + time.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
			logFormat = logFormat.replace(LoggerFormat.DAY_OF_MONTH.getFormat(), String.valueOf(time.getDayOfMonth() >= 10 ? time.getDayOfMonth() : "0" + time.getDayOfMonth()));
			logFormat = logFormat.replace(LoggerFormat.DAY_OF_YEAR.getFormat(), String.valueOf(time.getDayOfYear() >= 10 ? time.getDayOfYear() : "0" + time.getDayOfYear()));
			logFormat = logFormat.replace(LoggerFormat.DAY_OF_WEEK_NAME.getFormat(), DayOfWeek.of(time.getDayOfWeek().getValue()).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
			logFormat = logFormat.replace(LoggerFormat.DAY_OF_WEEK.getFormat(), "0" + time.getDayOfWeek().getValue());
			logFormat = logFormat.replace(LoggerFormat.HOUR24.getFormat(), String.valueOf(time.getHour() >= 10 ? time.getHour() : "0" + time.getHour()));
			boolean amHour = time.getHour() < 12;
			
			if (amHour) {
				logFormat = logFormat.replace(LoggerFormat.HOUR12.getFormat(), String.valueOf(time.getHour() >= 10 ? time.getHour() : "0" + time.getHour()));
				logFormat = logFormat.replace(LoggerFormat.HOUR_AM_PM.getFormat(), amHourText);
			} else {
				logFormat = logFormat.replace(LoggerFormat.HOUR12.getFormat(), String.valueOf(time.getHour() - 12 >= 10 ? time.getHour() - 12 : "0" + (time.getHour() - 12)));
				logFormat = logFormat.replace(LoggerFormat.HOUR_AM_PM.getFormat(), pmHourText);
			}
			
			int startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).getDayOfWeek().getValue();
			String monthStart = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
			logFormat = logFormat.replace(LoggerFormat.MONTH_START.getFormat(), "" + startOfMonth);
			logFormat = logFormat.replace(LoggerFormat.MONTH_START_NAME.getFormat(), monthStart);
			
			logFormat = logFormat.replace(LoggerFormat.HOURS_IN_WEEK.getFormat(), "" + (long) ((time.getDayOfWeek().getValue() - 1) * 24L + (long) time.getHour()));
			logFormat = logFormat.replace(LoggerFormat.HOURS_IN_MONTH.getFormat(), "" + (long) ((time.getDayOfMonth() - 1) * 24L + (long) time.getHour()));
			logFormat = logFormat.replace(LoggerFormat.HOURS_IN_YEAR.getFormat(), "" + (long) ((time.getDayOfYear() - 1) * 24L + (long) time.getHour()));
			
			logFormat = logFormat.replace(LoggerFormat.MINUTES_IN_WEEK.getFormat(), ""
					+ (long) ((time.getDayOfWeek().getValue() - 1) * 24L * 60L + (long) (time.getHour() * 60 + time.getMinute())));
			logFormat = logFormat
					.replace(LoggerFormat.MINUTES_IN_MONTH.getFormat(), "" + (long) ((time.getDayOfMonth() - 1) * 24L * 60L + (long) (time.getHour() * 60 + time.getMinute())));
			logFormat = logFormat
					.replace(LoggerFormat.MINUTES_IN_YEAR.getFormat(), "" + (long) ((time.getDayOfYear() - 1) * 24L * 60L + (long) (time.getHour() * 60 + time.getMinute())));
			
			logFormat = logFormat.replace(LoggerFormat.SECONDS_IN_WEEK.getFormat(), "" + (long) ((time.getDayOfWeek().getValue() - 1) * 24L * 60L * 60L + (long) time.getSecond()));
			logFormat = logFormat.replace(LoggerFormat.SECONDS_IN_MONTH.getFormat(), ""
					+ (long) ((time.getDayOfMonth() - 1) * 24L * 60L * 60L + (long) (time.getHour() * 60 * 60 + time.getSecond())));
			logFormat = logFormat.replace(LoggerFormat.SECONDS_IN_YEAR.getFormat(), ""
					+ (long) ((time.getDayOfYear() - 1) * 24L * 60L * 60L + (long) (time.getHour() * 60 * 60 + time.getSecond())));
			
			logFormat = logFormat
					.replace(LoggerFormat.MILLISECOND.getFormat(), String.valueOf(Calendar.getInstance(TimeZone.getTimeZone(time.getZone())).getTimeInMillis() % 1000));
			logFormat = logFormat.replace(LoggerFormat.MINUTE.getFormat(), String.valueOf(time.getMinute() >= 10 ? time.getMinute() : "0" + time.getMinute()));
			logFormat = logFormat.replace(LoggerFormat.SECOND.getFormat(), String.valueOf(time.getSecond() >= 10 ? time.getSecond() : "0" + time.getSecond()));
			logFormat = logFormat.replace(LoggerFormat.NANOSECOND.getFormat(), String.valueOf(time.getNano() % 1000000000));
			
			StringBuilder indentText = new StringBuilder();
			
			for (int i = 0; i < indent; i++) {
				indentText.append(indentString);
			}
			
			return log ? logPrefix + logFormat + logSuffix : indentText + logPrefix + logFormat + logSuffix;
		}
	}
	
	/**
	 * Returns the title that can be used in the log format. The title is usually
	 * used as the title of the application and can be displayed in each log. This
	 * is meant to be used as a sort of identifier for each log for anybody who sees
	 * them. This title will tell them from where the log is coming from (from the
	 * application of from the servers, for example).
	 * 
	 * @return The title that can be used in the log format.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title for the application or log format.
	 * 
	 * @param title The title that can be used in the log format.
	 * @see #getTitle()
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Returns the suffix for each log. This will be displayed after everything.
	 * It's default value is nothing: {@code ""}.
	 * 
	 * @return The suffix for each log.
	 */
	public String getLogSuffix() {
		return logSuffix;
	}
	
	/**
	 * Sets the suffix that will appear in each log. It's default value is nothing:
	 * {@code ""}.
	 * 
	 * @param logSuffix The suffix to use.
	 */
	public void setLogSuffix(String logSuffix) {
		this.logSuffix = logSuffix;
	}
	
	/**
	 * Returns the prefix for each log. This will be written before everything in
	 * the log (except the indent). It's default value is nothing: {@code ""}.
	 * 
	 * @return The prefix for each log.
	 */
	public String getLogPrefix() {
		return logPrefix;
	}
	
	/**
	 * Sets the prefix that will appear before each log. It's default value is
	 * nothing: {@code ""}.
	 * 
	 * @param logPrefix The prefix to use.
	 */
	public void setLogPrefix(String logPrefix) {
		this.logPrefix = logPrefix;
	}
	
	/**
	 * Returns the <code>String</code> used as an indent in a logged message. This
	 * indent will be repeated {@value #DEFAULT_INDENT} time(s) as the start of each
	 * log (even before the {@link #getLogFormat() format information}).
	 * 
	 * @return The <code>String</code> used as an indent in a logged message.
	 */
	public String getIndentString() {
		return indentString;
	}
	
	/**
	 * Sets the <code>String</code> used as an indent in a logged message. This
	 * indent will be repeated {@value #DEFAULT_INDENT} time(s) as the start of each
	 * log (even before the {@link #getLogFormat() format information}).
	 * 
	 * @param indentString The <code>String</code> to use as in indent in each
	 *                     logged message.
	 */
	public void setIndentString(String indentString) {
		this.indentString = indentString;
	}
	
	/**
	 * Returns the text that will be used if the {@link LoggerFormat#HOUR_AM_PM}
	 * value is used and current hour is between {@code 0:00 - 11:59}.
	 * 
	 * @return The text that will be used if the {@link LoggerFormat#HOUR_AM_PM}
	 *         value is used and current hour is between {@code 0:00 - 11:59}.
	 */
	public String getAMHourText() {
		return amHourText;
	}
	
	/**
	 * Sets the {@link LoggerFormat#HOUR_AM_PM} text if the current hour is between
	 * {@code 0:00 - 11:59}.
	 * 
	 * @param amHourText The "AM" hour text to be displayed if the statement is
	 *                   true.
	 */
	public void setAMHourText(String amHourText) {
		this.amHourText = amHourText;
	}
	
	/**
	 * Returns the text that will be used if the {@link LoggerFormat#HOUR_AM_PM}
	 * value is used and current hour is between {@code 12:00 - 23:59}.
	 * 
	 * @return The text that will be used if the {@link LoggerFormat#HOUR_AM_PM}
	 *         value is used and current hour is between {@code 12:00 - 23:59}.
	 */
	public String getPMHourText() {
		return pmHourText;
	}
	
	/**
	 * Sets the {@link LoggerFormat#HOUR_AM_PM} text if the current hour is between
	 * {@code 12:00 - 23:59}.
	 * 
	 * @param pmHourText The "PM" hour text to be displayed if the statement is
	 *                   true.
	 */
	public void setPMHourText(String pmHourText) {
		this.pmHourText = pmHourText;
	}
	
	/**
	 * Returns The log format for each message that will be logged. This determines
	 * how the logged messages will be displayed by the {@code Logger} and can give
	 * other information such as the {@link Logger.Level logging level}, and
	 * information about {@link LoggerFormat time}. For all possibilities, visit
	 * {@link LoggerFormat}.
	 * 
	 * @return The log format for these options that will be used in the related
	 *         {@code Logger}.
	 */
	public String getLogFormat() {
		return logFormat;
	}
	
	/**
	 * Sets the log format for each message that will be logged. This determines how
	 * the logged messages will be displayed by the {@code Logger} and can give
	 * other information such as the {@link Logger.Level logging level}, and
	 * information about {@link LoggerFormat time}. For all possibilities, visit
	 * {@link LoggerFormat}.
	 * 
	 * @param logFormat The log format for each message that will be logged.
	 */
	public void setLogFormat(String logFormat) {
		this.logFormat = logFormat;
	}
	
	/**
	 * Sets whether the {@link LoggerFormat LoggerFormats} used in a logged message
	 * should be translated to their proper value.
	 * 
	 * @param translateLog the value of this boolean. {@code true} for translated
	 *                     logs; {@code false} otherwise.
	 */
	public void setTranslateLog(boolean translateLog) {
		this.translateLog = translateLog;
	}
	
	/**
	 * Returns whether the {@link LoggerFormat LoggerFormats} used in a logged
	 * message should be translated to their proper value.
	 * 
	 * @return Whether the {@link LoggerFormat LoggerFormats} used in a logged
	 *         message should be translated to their proper value. {@code true} for
	 *         translated logs; {@code false} otherwise.
	 */
	public boolean willTranslateLog() {
		return translateLog;
	}
	
	/**
	 * Returns the amount of times indent that will be prompted at the start of the
	 * output of a message log from a {@code Logger}. If this value is set to
	 * {@code 3}, the {@link #DEFAULT_INDENT_STRING} will be repeated {@code 3}
	 * times at the beginning of the prompt to act as an indent (even before the
	 * {@link #getLogFormat() format information}).
	 * 
	 * @return The indent amount for these options.
	 */
	public int getIndent() {
		return indent;
	}
	
	/**
	 * Sets the amount of indent for these options. When this value is changed, it
	 * will be prompted at the start of the output of a message log from a
	 * {@code Logger} for same amount of times as the value. For example, if this
	 * value is set to {@code 3}, the {@link #DEFAULT_INDENT_STRING} will be
	 * repeated {@code 3} times at the beginning of the prompt to act as an indent
	 * (even before the {@link #getLogFormat() format information}).
	 * 
	 * @param indent The amount of times indent that will be prompted in the output
	 *               of a message log from a {@code Logger}.
	 */
	public void setIndent(int indent) {
		this.indent = indent;
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * <p>
	 * The {@code equals} method implements an equivalence relation on non-null
	 * object references:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any non-null reference value {@code x},
	 * {@code x.equals(x)} should return {@code true}.
	 * <li>It is <i>symmetric</i>: for any non-null reference values {@code x} and
	 * {@code y}, {@code x.equals(y)} should return {@code true} if and only if
	 * {@code y.equals(x)} returns {@code true}.
	 * <li>It is <i>transitive</i>: for any non-null reference values {@code x},
	 * {@code y}, and {@code z}, if {@code x.equals(y)} returns {@code true} and
	 * {@code y.equals(z)} returns {@code true}, then {@code x.equals(z)} should
	 * return {@code true}.
	 * <li>It is <i>consistent</i>: for any non-null reference values {@code x} and
	 * {@code y}, multiple invocations of {@code x.equals(y)} consistently return
	 * {@code true} or consistently return {@code false}, provided no information
	 * used in {@code equals} comparisons on the objects is modified.
	 * <li>For any non-null reference value {@code x}, {@code x.equals(null)} should
	 * return {@code false}.
	 * </ul>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof LoggerOptions) {
			LoggerOptions other = (LoggerOptions) obj;
			return other.indent == indent && other.pmHourText.equals(pmHourText) && other.amHourText.equals(amHourText) && other.logFormat.equals(logFormat) &&
					other.indentString.equals(indentString) && other.logPrefix.equals(logPrefix) && other.logSuffix.equals(logSuffix) && other.title.equals(title);
		}
		
		return super.equals(obj);
	}
}
