package com.osc4j.ds.personal;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class MyInformation implements Serializable{
	@SerializedName("status")
	private int mStatus;
	@SerializedName("am")
	private Am mAm;


	public MyInformation(int status, Am am) {
		mStatus = status;
		mAm = am;
	}


	public int getStatus() {
		return mStatus;
	}

	public Am getAm() {
		return mAm;
	}
}
