/**
 * The main package of this project.
 */
package com.junix.hub;

import java.util.HashSet;
import java.util.Set;

import com.junix.utils.logging.LogFile;
import com.junix.utils.logging.Logger;
import com.junix.utils.logging.Logger.Level;
import com.junix.utils.unique.Identifiable;
import com.junix.utils.unique.UniqueID;

// Fix the grantedAccessClasses link. Others cannot see because of privacy
/**
 * The main class of this Junix program. This project was made to facilitate the
 * implementation of plugins (or "add-ons") into a secondary project. To do so,
 * you can use this exported project (as a jar), and add it to the classpath to
 * be able to use the methods inside of it.
 * 
 * <p>
 * Even though this project is principally meant for secondary project, there
 * are a total of two different ways to use this project or utility. The first
 * way is to do what I explained before, and the second way is to use this
 * project in a stand-alone fashion. You can accomplish this by running the
 * program with a ".bat" file. A folder where you can put all of your plugins
 * will appear once you launch this program and to make your own plugin, you can
 * use this project and import it into your own classpath.
 *
 * <p>
 * Since this is the main class, there will only be one instance of it and most
 * methods will therefore be static will not be implemented by any other class
 * and is therefore final. Few classes will even be able to access this class,
 * only classes who need a utility from this class will be added to the
 * {@code Set} of classes that will be granted access to this class.
 * 
 * <p>
 * Since there is only one instance of this class, it can be identified by using
 * the {@link #uniqueIdentifier()} method.
 * 
 * @see Identifiable
 * @see #uniqueIdentifier()
 */
public final class JunixHub implements Identifiable {
	
	/**
	 * Serial version UID. Ignore this.
	 */
	private static final long serialVersionUID = 7429016486321519536L;

	/**
	 * A set of classes that are allowed to access this class. If a class tries to
	 * access this one without being in this collection, the program will throw an
	 * {@link IllegalAccessException}.
	 * 
	 * @see IllegalAccessException
	 * @see #tryAccess()
	 * @see #tryAccess(ClassLoader)
	 */
	private static final Set<Class<?>> grantedAccessClasses = new HashSet<Class<?>>();
	
	/**
	 * Static initializer. Adds classes who should have access to this one.
	 */
	static {
		grantedAccessClasses.add(JunixHub.class);
		grantedAccessClasses.add(ArgumentParser.class);
		grantedAccessClasses.add(LogFile.class);
	}
	
	/**
	 * The logger instance used for logging information, warnings, errors, and more.
	 * This object will be shared throughout the project
	 */
	private static final Logger logger = Logger.getLogger(JunixHub.class.getName());
	
	/**
	 * Whether this application should run in debug-mode. With this boolean set to
	 * {@code true}, there will be more outputs to the console.
	 */
	static boolean debugMode = false;
	
	/**
	 * The object of a {@link UniqueID unique identifier} for this class. Each class
	 * who inherits from the interface {@link Identifiable} will require a
	 * {@link UniqueID unique identifier}.
	 */
	private UniqueID identifier;
	
	/**
	 * The <code>raw</code> instance of this main class. This will be the only
	 * instance of this class;
	 */
	private static final JunixHub rawInstance = new JunixHub();
	
	/**
	 * The constructor for this main class. This will only be called once the the
	 * instance can be accessed by the {@link #rawInstance} field, or the
	 * {@link #tryAccess()} method.
	 * 
	 */
	private JunixHub() {
		identifier = new UniqueID(String.valueOf(System.identityHashCode(this)), true);
	}
	
	/**
	 * The main method of this program. This is used to run the program, and parse
	 * the user's arguments.
	 * 
	 * @param args The user's program arguments
	 * @deprecated This method should never be called.
	 */
	@Deprecated
	public static void main(String[] args) {
		ArgumentParser.parse(args);
	}
	
	/**
	 * This method tries to access this class, depending on which class you call it
	 * from. If the given class is in the {@link Set} of allowed classes, the class
	 * will be able to access this classes, and vice-versa.
	 * 
	 * @return This class if the class trying to access this one has access.
	 *         Otherwise, null.
	 * @throws IllegalAccessException If the class who is trying to access this one
	 *                                is not in the {@link Set} of allowed classes.
	 */
	public static Object tryAccess() throws IllegalAccessException {
		return tryAccess(ClassLoader.getSystemClassLoader());
	}
	
	/**
	 * This method tries to access this class, depending on which class you call it
	 * from. If the given class is in the {@link Set} of allowed classes, the class
	 * will be able to access this classes, and vice-versa.
	 * 
	 * @return This class if the class trying to access this one has access;
	 *         otherwise, {@code null}.
	 * @param context The {@link ClassLoader} of the class who is trying to access
	 *                this class. See {@link #tryAccess()} if the class is from the
	 *                {@link ClassLoader#getSystemClassLoader()}.
	 * @throws IllegalAccessException If the class who is trying to access this one
	 *                                is not in the {@link Set} of allowed classes.
	 * @see #tryAccess()
	 */
	public static Object tryAccess(ClassLoader context) throws IllegalAccessException {
		String examinedClassName = Thread.currentThread().getStackTrace()[3] == null ? Thread.currentThread().getStackTrace()[2].getClassName() :
				Thread.currentThread().getStackTrace()[3].getClassName();
		
		if (tryAccess0(examinedClassName, context)) {
			return rawInstance;
		} else {
			throw new IllegalAccessException(examinedClassName);
		}
	}
	
	// Checks if the given class can access this class
	private static boolean tryAccess0(String examinedClassName, ClassLoader context) {
		Class<?> examinedClass;
		
		// Make sure the class that calls this method exists (in case of error)
		try {
			examinedClass = Class.forName(examinedClassName, false, context);
		} catch (ClassNotFoundException e) {
			// Only print in debug-mode
			if (debugMode)
				logger.logLine(Level.ERROR, "Class \"" + examinedClassName + "\" could not be found");
			return false;
		}
		
		// Assure thread-safety
		synchronized (grantedAccessClasses) {
			for (Class<?> clazz : grantedAccessClasses) {
				if (clazz == examinedClass) { // Properly compares classes
					return true;
				}
			}
		}
		
		// Only print in debug-mode
		if (debugMode)
			// Log an access error
			logger.logLine(Level.ERROR, "Class \"" + examinedClass.getName() + "\" is not eligible to access the class \"" + JunixHub.class.getName() + "\"");
		return false;
	}
	
	// Checks if the given class can access this class
	@SuppressWarnings("unused")
	private static boolean tryAccess0Silent(String examinedClassName, ClassLoader context) {
		Class<?> examinedClass;
		
		try {
			examinedClass = Class.forName(examinedClassName, false, context);
		} catch (ClassNotFoundException e) {
			return false;
		}
		
		// Assure thread-safety
		synchronized (grantedAccessClasses) {
			for (Class<?> clazz : grantedAccessClasses) {
				if (clazz == examinedClass) { // Properly compares classes
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the total amount of memory in the JVM. The value returned by this
	 * method may vary over time, depending on the host environment.
	 * <p>
	 * Note that the amount of memory required to hold an object of any given type
	 * may be implementation-dependent.
	 *
	 * @return The total amount of memory currently available for current and future
	 *         objects, measured in bytes.
	 */
	public long totalMemory() {
		return Runtime.getRuntime().totalMemory();
	}
	
	/**
	 * Returns the amount of available memory until an {@link OutOfMemoryError} gets
	 * thrown. Calling the {@link #freeMemory()} method may result in increasing the
	 * value returned by {@link #availableMemory().}
	 *
	 * @return An approximation to the total amount of memory currently available
	 *         for future allocated objects, measured in bytes.
	 */
	public long availableMemory() {
		return maxMemory() - (totalMemory() - Runtime.getRuntime().freeMemory());
	}
	
	/**
	 * Returns the maximum amount of memory that the JVM will attempt to use. If
	 * there is no inherent limit then the value {@link java.lang.Long#MAX_VALUE}
	 * will be returned.
	 *
	 * @return The maximum amount of memory that the virtual machine will attempt to
	 *         use, measured in bytes
	 */
	public long maxMemory() {
		return Runtime.getRuntime().maxMemory();
	}
	
	/**
	 * Attempts to recycle all unused objects to free up their memory for other use.
	 */
	public void freeMemory() {
		System.gc();
	}
	
	/**
	 * Returns whether this application should run in debug-mode. With this boolean
	 * set to {@code true}, there will be more outputs to the console.
	 * 
	 * @return {@code true} if this application is in debug-mode; {@code false}
	 *         otherwise.
	 */
	public boolean debugMode() {
		return debugMode;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueID uniqueIdentifier() {
		return identifier;
	}
}
