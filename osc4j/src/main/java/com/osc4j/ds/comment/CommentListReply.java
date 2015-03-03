package com.osc4j.ds.comment;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class CommentListReply implements Serializable{
	@SerializedName("rauthor")
	private String mRauthor;
	@SerializedName("rpubDate")
	private String mRpubDate;
	@SerializedName("rauthorId")
	private int mRauthorId;
	@SerializedName("rcontent")
	private String mRcontent;


	public CommentListReply(String rauthor, String rpubDate, int rauthorId, String rcontent) {
		mRauthor = rauthor;
		mRpubDate = rpubDate;
		mRauthorId = rauthorId;
		mRcontent = rcontent;
	}


	public String getRauthor() {
		return mRauthor;
	}

	public String getRpubDate() {
		return mRpubDate;
	}

	public int getRauthorId() {
		return mRauthorId;
	}

	public String getRcontent() {
		return mRcontent;
	}
}
