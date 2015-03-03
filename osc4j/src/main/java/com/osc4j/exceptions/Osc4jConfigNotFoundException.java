package com.osc4j.exceptions;

/**
 * Fired when the osc4j.properties isn't found under asset folder.
 *
 * @author Xinyue Zhao
 */
public final class Osc4jConfigNotFoundException extends Exception{
	public Osc4jConfigNotFoundException() {
		super("Can't find the [osc4j.properties] file under asset folder.");
	}
}
