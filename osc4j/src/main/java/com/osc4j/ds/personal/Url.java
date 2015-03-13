package com.osc4j.ds.personal;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class Url implements Serializable {
	@SerializedName("home")
	private String mHome;
	@SerializedName("edit")
	private String mEdit;

	public Url(String home, String edit) {
		mHome = home;
		mEdit = edit;
	}

	public String getHome() {
		return mHome;
	}

	public String getEdit() {
		return mEdit;
	}
}
