/**
 * Utils package
 */
package com.junix.utils;

import java.util.Set;

import com.junix.utils.logging.Logger.Level;
import com.junix.utils.logging.LoggerFilter;

/**
 * The <code>Filterable</code> interface should be used whenever data gets
 * filtered according to certain demands. These demands should usually come from
 * a filter class like the {@link LoggerFilter} class. It should also have some
 * sort of {@link LoggerFilter#accept(String, Level, String, String) accept()}
 * method so that each implementation or each anonymous creation of that class
 * can have its own way of letting certain data pass through.
 * <p>
 * This interface allows users to add and remove any amount of filters that can
 * be accessed via the {@link #getFilters()} method. I recommend that you use
 * that method to filter your desired data to make sure that every filter
 * attached to this class accepts that piece of data. Think of this class as a
 * collection of filters.
 * <p>
 * The generic type for this interface should be assigned to the class of the
 * filter that will be used to filter the data. Note that this interface should
 * only be implemented and never initialized anonymously because the methods in
 * this interface must be overridden by another class in order for them to
 * function. This interface is basically only to be used as a template and other
 * classes will have to implement it so that the filters that get added with the
 * {@link #addFilter(Object)} method actually get stored somewhere. It will have
 * to be stored into a {@code Set} or something that can be transformed into a
 * {@code Set} because the {@link #getFilters()} returns a {@code Set}.
 */
public interface Filterable<T> {
	
	/**
	 * Sets the filters for this {@code Filterable} instance. These filters will
	 * restrict what can be used in the implementation of this class by essentially
	 * filtering valid information for use. In order for information to be valid,
	 * most filters should have some implementation of an {@code accept()} method
	 * where it must return {@code true} in order to accept the data. The
	 * {@link LoggerFilter#accept(String, Level, String, String)} method is a great
	 * example of that.
	 * 
	 * @param filters The filters of type T for this {@code Filterable} instance.
	 */
	public void setFilters(Set<T> filters);
	
	/**
	 * Adds a filter to the list of pre-existing filters.
	 * 
	 * @param filter The filter to add.
	 */
	public void addFilter(T filter);
	
	/**
	 * Removes a filter from the list of pre-existing filters.
	 * 
	 * @param filter The filter to remove.
	 */
	public void removeFilter(T filter);
	
	/**
	 * Returns a {@code Set} of filters that are used to restrict certain types of
	 * data from being used. In order for a piece of data to be used by a filter be
	 * valid, most filters should have some implementation of an {@code accept()}
	 * method where it must return {@code true} in order to accept the data. The
	 * {@link LoggerFilter#accept(String, Level, String, String)} method is a great
	 * example of that.
	 * 
	 * @return A {@code Set} of all the filters that are used to restrict data
	 *         usage.
	 */
	public Set<T> getFilters();
}
