package com.osc4j.ds.comment;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class CommentListRefer  implements Serializable{
	@SerializedName("refertitle")
	private String mRefertitle;
	@SerializedName("referbody")
	private String mReferbody;


	public CommentListRefer(String refertitle, String referbody) {
		mRefertitle = refertitle;
		mReferbody = referbody;
	}


	public String getRefertitle() {
		return mRefertitle;
	}

	public String getReferbody() {
		return mReferbody;
	}
}
