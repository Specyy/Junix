package com.junix.utils.logging;

/**
 * The formats that can be used for the log format each of log, or the can be
 * used as utility in a prompt if {@link LoggerOptions#setTranslateLog(boolean)
 * setTranslateLog(true)} is called.
 */
public enum LoggerFormat {
	/**
	 * Gets the current year (ex. 1900, 2000, 2020)
	 */
	YEAR("%year"),

	/**
	 * Gets the current month as a number (January = 1, February = 2, March = 3,
	 * ...). See also {@link #MONTH_NAME} to get the month name.
	 * 
	 * @see #MONTH_NAME
	 */
	MONTH("%month"),

	/**
	 * Gets the current month's name (ex. January, February, March, ...)
	 */
	MONTH_NAME("%mn"),

	/**
	 * Gets the day on which the month started, as a number (1 = Monday, 2 =
	 * Tuesday, ..., 7 = Sunday)
	 */
	MONTH_START("%ms"),

	/**
	 * Gets the name of the day on which the month started (ex. Monday, Tuesday,
	 * ..., Sunday)
	 */
	MONTH_START_NAME("%msn"),

	/**
	 * Gets the current week of the year.
	 */
	WEEK_OF_YEAR("%woy"),

	/**
	 * Gets the current week of the month (usually 1, 2, 3 or 4).
	 */
	WEEK_OF_MONTH("%wom"),

	/**
	 * Gets the current day of the year.
	 */
	DAY_OF_YEAR("%doy"),

	/**
	 * Gets the current day of the month.
	 */
	DAY_OF_MONTH("%dom"),

	/**
	 * Gets the current day of the week. (1 = Monday, 2 = Tuesday, ..., 7 = Sunday)
	 */
	DAY_OF_WEEK("%dow"),

	/**
	 * Gets the current day of the week's name (ex. Monday, Tuesday, Wednesday, ...)
	 */
	DAY_OF_WEEK_NAME("%down"),

	/**
	 * Returns {@value LoggerOptions#DEFAULT_AM_HOUR_TEXT} (or the text that was set
	 * in the {@link LoggerOptions#setAMHourText(String) setAMHourText()} method) if
	 * the current hour is between {@code 0:00 - 11:59}, and
	 * {@value LoggerOptions#DEFAULT_PM_HOUR_TEXT} (or the text that was set in the
	 * {@link LoggerOptions#setPMHourText(String) setPMHourText()} method) if the
	 * hour is between {@code 12:00 - 23:59}.
	 */
	HOUR_AM_PM("%ampm"),

	/**
	 * Gets the current hour as a 12-hour clock would.
	 */
	HOUR12("%hour12"),

	/**
	 * Gets the current hour as a 24-hour clock would.
	 */
	HOUR24("%hour24"),

	/**
	 * Gets the amount of hours that have passed since the start of the current
	 * year, rounded down.
	 */
	HOURS_IN_YEAR("%hiy"),

	/**
	 * Gets the amount of hours that have passed since the start of the current
	 * month, rounded down.
	 */
	HOURS_IN_MONTH("%him"),

	/**
	 * Gets the amount of hours that have passed since the start of the current
	 * week, rounded down.
	 */
	HOURS_IN_WEEK("%hiw"),

	/**
	 * Gets the amount of minutes that have passed since the start of the current
	 * year, rounded down.
	 */
	MINUTES_IN_YEAR("%miy"),

	/**
	 * Gets the amount of minutes that have passed since the start of the current
	 * month, rounded down.
	 */
	MINUTES_IN_MONTH("%mim"),

	/**
	 * Gets the amount of minutes that have passed since the start of the current
	 * week, rounded down.
	 */
	MINUTES_IN_WEEK("%miw"),

	/**
	 * Gets the amount of seconds that have passed since the start of the current
	 * year, rounded down.
	 */
	SECONDS_IN_YEAR("%siy"),

	/**
	 * Gets the amount of seconds that have passed since the start of the current
	 * month, rounded down.
	 */
	SECONDS_IN_MONTH("%sim"),

	/**
	 * Gets the amount of seconds that have passed since the start of the current
	 * week, rounded down.
	 */
	SECONDS_IN_WEEK("%siw"),

	/**
	 * Gets the current minute in the current hour.
	 */
	MINUTE("%minute"),

	/**
	 * Gets the current second in the current minute
	 */
	SECOND("%second"),

	/**
	 * Gets the amount of milliseconds in the current second.
	 */
	MILLISECOND("%millis"),

	/**
	 * Gets the amount of nanoseconds in the current second.
	 */
	NANOSECOND("%nano"),

	/**
	 * The {@code Level} of the log. See {@link Logger.Level}
	 * 
	 * @see Logger.Level
	 */
	LEVEL("%level"),

	/**
	 * The title of the application that can be displayed in each log.
	 * 
	 * @see LoggerOptions#getTitle()
	 */
	TITLE("%title"),

	/**
	 * The message that will be sent to the logger.
	 */
	PROMPT("%prompt");

	private String format;

	LoggerFormat(String format) {
		this.format = format;
	}

	public static LoggerFormat parse(String identifier) {
		return parse(identifier, false);
	}

	public static LoggerFormat parse(String identifier, boolean ignoreCase) {
		for (int i = 0; i < values().length; i++) {
			if (ignoreCase) {
				if (values()[i].format.equalsIgnoreCase(identifier))
					return values()[i];
			} else {
				if (values()[i].format.equals(identifier))
					return values()[i];
			}
		}

		return null;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return getFormat();
	}
}
