package com.osctweet4j.ds;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class TweetListItem implements Serializable {
	@SerializedName("id")
	private int mId;
	@SerializedName("pubDate")
	private String mPubDate;
	@SerializedName("body")
	private String mBody;
	@SerializedName("author")
	private String mAuthor;
	@SerializedName("authorid")
	private long mAuthorId;
	@SerializedName("imgSmall")
	private String mImgSmall;
	@SerializedName("commentCount")
	private int mCommentCount;
	@SerializedName("imgBig")
	private String mImgBig;
	@SerializedName("portrait")
	private String mPortrait;


	public TweetListItem(int id, String pubDate, String body, String author, long authorId, String imgSmall,
			int commentCount, String imgBig, String portrait) {
		mId = id;
		mPubDate = pubDate;
		mBody = body;
		mAuthor = author;
		mAuthorId = authorId;
		mImgSmall = imgSmall;
		mCommentCount = commentCount;
		mImgBig = imgBig;
		mPortrait = portrait;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getPubDate() {
		return mPubDate;
	}

	public void setPubDate(String pubDate) {
		mPubDate = pubDate;
	}

	public String getBody() {
		return mBody;
	}

	public void setBody(String body) {
		mBody = body;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public void setAuthor(String author) {
		mAuthor = author;
	}

	public long getAuthorId() {
		return mAuthorId;
	}

	public void setAuthorId(long authorId) {
		mAuthorId = authorId;
	}

	public int getCommentCount() {
		return mCommentCount;
	}

	public void setCommentCount(int commentCount) {
		mCommentCount = commentCount;
	}

	public String getPortrait() {
		return mPortrait;
	}

	public void setPortrait(String portrait) {
		mPortrait = portrait;
	}

	public String getImgSmall() {
		return mImgSmall;
	}

	public void setImgSmall(String imgSmall) {
		mImgSmall = imgSmall;
	}

	public String getImgBig() {
		return mImgBig;
	}

	public void setImgBig(String imgBig) {
		mImgBig = imgBig;
	}
}
