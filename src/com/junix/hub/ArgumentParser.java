/**
 * 
 */
package com.junix.hub;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.junix.utils.text.Text;

/**
 * This class is responsible for parsing all the program's arguments, executing
 * the actions associated with each argument, and retaining those arguments for
 * other use.
 */
final class ArgumentParser {
	
	/**
	 * Set used to store the program's arguments.
	 */
	private static Set<String> arguments;
	
	/**
	 * No instance will ever be created.
	 */
	private ArgumentParser() {
	}
	
	/**
	 * Parses the program's arguments, executes all related actions, and stores them
	 * in memory.
	 * 
	 * @param args The arguments to parse
	 */
	static void parse(String[] args) { // TODO Parse arguments
		if (arguments == null)
			arguments = ConcurrentHashMap.newKeySet();
		
		for (int i = 0; i < args.length; i++) {
			arguments.add(args[i]);
		}
		
		arguments.removeIf(x -> x == null || x.isEmpty());
		JunixHub.debugMode = arguments.contains("-debug");
		
		Text t = new Text("Hello World");
		int s = t.firstIndexOf('W');
		
		System.out.println(s);
	}
	
	/**
	 * Retrieves the argument that is equal to, starts with "{@code -}", or starts
	 * with "{@code +}" and the {@code arg} if it is present. Note that arguments do
	 * not need to start with a "{@code -}" or "{@code +}" to be considered and
	 * argument.
	 * 
	 * @param arg The argument to check for.
	 * @return The whole argument that was searched for before another argument is
	 *         identified.
	 */
	public static String getArgument(String arg) {
		synchronized (arguments) {
			for (String argument : arguments) {
				if (argument.equals(arg) || argument.startsWith("-" + arg) || argument.startsWith("+" + arg))
					return argument;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the program's arguments from memory.
	 * 
	 * @return The program's arguments.
	 */
	public static String[] getArguments() {
		return arguments.toArray(new String[0]); // TODO Optimize
	}
}