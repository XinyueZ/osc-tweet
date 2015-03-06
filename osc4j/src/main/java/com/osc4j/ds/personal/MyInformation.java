package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class MyInformation implements Serializable{
	@SerializedName("status")
	private int mStatus;
	@SerializedName("am")
	private Am mAm;
	@SerializedName("actives")
	private List<Active> mActives;

	public MyInformation(int status, Am am, List<Active> actives) {
		mStatus = status;
		mAm = am;
		mActives = actives;
	}


	public int getStatus() {
		return mStatus;
	}

	public Am getAm() {
		return mAm;
	}

	public List<Active> getActives() {
		return mActives;
	}
}
