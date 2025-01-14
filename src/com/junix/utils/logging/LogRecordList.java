package com.junix.utils.logging;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// FOR THE UNMEMORYWISE METHOD, TELL THEM TO USE .FREEMEMORY FROM JUNIXHUB TO HELP WITH MEMORY CLEARAGE.
/**
 * A list of all the LogRecords that belong to a specific <code>Logger</code>.
 * This list will include all levels of logs, from {@link Logger.Level#ERROR
 * ERROR} logs to {@link Logger.Level#SERVER SERVER} logs. For more information
 * about how logs get recorded, see {@link LogRecord}.
 * <p>
 * This class implements the {@link Iterable} interface and can be iterated
 * over, like a real list.
 * 
 * @see LogRecord
 * @see Logger
 */
public class LogRecordList implements Iterable<LogRecord> {
	
	/**
	 * The <code>Logger</code> where all the logs are being sent from.
	 */
	private Logger logger;
	
	/**
	 * A <code>List</code> of all the LogRecords from the {@code Logger}.
	 */
	private List<LogRecord> records;
	
	/**
	 * Constructs a {@code LogRecordList} with a given {@code Logger}.
	 * 
	 * @param logger The <code>Logger</code> where all the logs are being sent from.
	 */
	LogRecordList(Logger logger) {
		this.logger = logger;
		records = new LinkedList<LogRecord>();
	}
	
	/**
	 * Removes the record at the specified {@code index} for the list of recorded
	 * logs.
	 * 
	 * @param index The index of the record to remove. {@code 0} for the first
	 *              record, {@link #getRecords() getRecords().size()} - 1 for the
	 *              latest log.
	 * @return The record that was removed from this list.
	 */
	LogRecord removeRecord(int index) {
		return records.remove(index);
	}
	
	/**
	 * Removes the specified {@code LogRecord} from this list of records.
	 * 
	 * @param record The record to remove.
	 * @return <code>true</code> if this list contained the specified record;
	 *         <code>false</code> otherwise.
	 */
	boolean removeRecord(LogRecord record) {
		return records.remove(record);
	}
	
	/**
	 * Adds a record to the list of records at the specified index.
	 * 
	 * @param record The record to add.
	 * @param index  The index where the record should be recorded.
	 */
	void addRecord(LogRecord record, int index) {
		records.add(index, record);
	}
	
	/**
	 * Adds a record to the list of records.
	 * 
	 * @param record The record to add.
	 */
	void addRecord(LogRecord record) {
		records.add(record);
	}
	
	/**
	 * Gets a record depending on when it was logged. <code>0</code> for the first
	 * logged message and <code>{@link #getRecords()}.size()</code> for the latest
	 * log.
	 * 
	 * @param index The index of the wanted log.
	 * @return A record depending on when it was logged.
	 */
	public LogRecord getRecord(int index) {
		return records.get(index);
	}
	
	/**
	 * Gets the records with the given log message. This is the message without
	 * including the format of the whole log; only the actual prompt of the log.
	 * 
	 * @param logMessage The prompted prompt.
	 * @return The records with the given log message.
	 */
	public LogRecord[] getRecords(String logMessage) {
		List<LogRecord> recs = new LinkedList<LogRecord>();
		
		for (int i = 0; i < records.size(); i++) {
			if (records.get(i).getLogMessage().equals(logMessage))
				recs.add(records.get(i));
		}
		
		return recs.toArray(new LogRecord[0]);
	}
	
	/**
	 * Gets all the records that are accepted by the given {@code LoggerFilter}.
	 * 
	 * @param filter The {@code LoggerFilter} to be used as a filter for the
	 *               records.
	 * @return All the records that are accepted by the given {@code LoggerFilter}.
	 */
	public LogRecord[] getRecords(LoggerFilter filter) {
		List<LogRecord> accepted = new LinkedList<LogRecord>();
		
		for (int i = 0; i < records.size(); i++) {
			LogRecord record = records.get(i);
			
			if (filter.accept(record.getFormat(), record.getLevel(), record.getLogMessage(), record.getLog()))
				accepted.add(record);
		}
		
		return accepted.toArray(new LogRecord[0]);
	}
	
	/**
	 * Returns the number of LogRecords in this {@code LogRecodList}. If this
	 * {@code LogRecodList} contains more than <tt>Integer.MAX_VALUE</tt> records,
	 * returns <tt>Integer.MAX_VALUE</tt>.
	 * 
	 * @return The number of elements in this list
	 */
	public int size() {
		return records.size();
	}
	
	/**
	 * Returns all the records that exist for the {@code Logger} of this
	 * {@code LogRecordList} as an <code>unmodifiableList</code>. So every message
	 * or prompt that has been logged successfully.
	 * 
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
	public List<LogRecord> getRecords() {
		return Collections.unmodifiableList(records);
	}
	
	/**
	 * Gets the <code>Logger</code> where all the logged messages from this list are
	 * coming from.
	 * 
	 * @return The <code>Logger</code> responsible for the records from this list.
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<LogRecord> iterator() {
		return records.iterator();
	}
}
