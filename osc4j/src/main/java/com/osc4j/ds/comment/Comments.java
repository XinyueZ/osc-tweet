package com.osc4j.ds.comment;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;


public final class Comments implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("comments")
	private List<Comment> mComments;

	public Comments(int status, List<Comment> comments) {
		mStatus = status;
		mComments = comments;
	}


	public int getStatus() {
		return mStatus;
	}

	public List<Comment> getComments() {
		return mComments;
	}
}
