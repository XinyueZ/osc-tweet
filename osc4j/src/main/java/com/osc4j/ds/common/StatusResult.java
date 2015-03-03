package com.osc4j.ds.common;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class StatusResult implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("result")
	private Result mResult;


	public StatusResult(int status, Result result) {
		mStatus = status;
		mResult = result;
	}

	public int getStatus() {
		return mStatus;
	}

	public Result getResult() {
		return mResult;
	}
}
