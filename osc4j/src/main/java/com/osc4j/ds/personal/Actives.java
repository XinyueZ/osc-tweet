package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Actives implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("actives")
	private List<Active> mActives;

	public Actives(int status, List<Active> actives) {
		mStatus = status;
		mActives = actives;
	}


	public int getStatus() {
		return mStatus;
	}

	public List<Active> getActives() {
		return mActives;
	}

	public void setActivesList(List<Active> actives) {
		mActives = actives;
		}
}
