package com.osc.tweet.events;

import com.osc4j.ds.personal.MyInformation;

/**
 * Event when my-information has been gotten.
 *
 * @author Xinyue Zhao
 */
public final class GetMyInformationEvent {
	/**
	 * All my current personal information.
	 */
	private MyInformation myInformation;

	/**
	 * Constructor of {@link GetMyInformationEvent}
	 * @param myInformation  All my current personal information.
	 */
	public GetMyInformationEvent(MyInformation myInformation) {
		this.myInformation = myInformation;
	}

	/**
	 *
	 * @return  All my current personal information.
	 */
	public MyInformation getMyInformation() {
		return myInformation;
	}
}
