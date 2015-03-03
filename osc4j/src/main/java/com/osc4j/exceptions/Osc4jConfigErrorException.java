package com.osc4j.exceptions;

/**
 * Error description when the osc4j.properties is not edited correctly.
 *
 * @author Xinyue Zhao
 */
public final class Osc4jConfigErrorException extends Exception {
	public Osc4jConfigErrorException(String detailMessage) {
		super(detailMessage);
	}
}
