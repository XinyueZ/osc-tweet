package com.osc4j.ds.common;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class Result implements Serializable {
	@SerializedName("error")
	private String mCode;
	@SerializedName("relation")
	private int mRelation;


	public Result(String code, int relation ) {
		mCode = code;
		mRelation = relation;
	}

	public String getCode() {
		return mCode;
	}

	public int getRelation() {
		return mRelation;
	}
}
