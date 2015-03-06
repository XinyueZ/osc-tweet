package com.osc4j.ds.personal;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class Active implements Serializable{
	@SerializedName("id")
	private int mId;
	@SerializedName("portrait")
	private String mPortrait;
	@SerializedName("author")
	private String mAuthor;
	@SerializedName("authorid")
	private int mAuthorId;
	@SerializedName("catalog")
	private int mCatalog;
	@SerializedName("appClient")
	private int mAppClient;
	@SerializedName("objectId")
	private int mObjectId;
	@SerializedName("objectType")
	private int mObjectType;
	@SerializedName("objectCatalog")
	private int mObjectCatalog;
	@SerializedName("objectTitle")
	private String mObjectTitle;
	@SerializedName("url")
	private String mUrl;
	@SerializedName("message")
	private String mMessage;
	@SerializedName("tweetImage")
	private String mTweetImage;
	@SerializedName("commentCount")
	private int mCommentCount;
	@SerializedName("pubDate")
	private String mPubDate;
	@SerializedName("objectReply")
	private ObjectReply mObjectReply;

	public Active(int id, String portrait, String author, int authorId, int catalog, int appClient, int objectId,
			int objectType, int objectCatalog, String objectTitle, String url, String message, String tweetImage,
			int commentCount, String pubDate, ObjectReply objectReply) {
		mId = id;
		mPortrait = portrait;
		mAuthor = author;
		mAuthorId = authorId;
		mCatalog = catalog;
		mAppClient = appClient;
		mObjectId = objectId;
		mObjectType = objectType;
		mObjectCatalog = objectCatalog;
		mObjectTitle = objectTitle;
		mUrl = url;
		mMessage = message;
		mTweetImage = tweetImage;
		mCommentCount = commentCount;
		mPubDate = pubDate;
		mObjectReply = objectReply;
	}

	public int getId() {
		return mId;
	}

	public String getPortrait() {
		return mPortrait;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public int getAuthorId() {
		return mAuthorId;
	}

	public int getCatalog() {
		return mCatalog;
	}

	public int getAppClient() {
		return mAppClient;
	}

	public int getObjectId() {
		return mObjectId;
	}

	public int getObjectType() {
		return mObjectType;
	}

	public int getObjectCatalog() {
		return mObjectCatalog;
	}

	public String getObjectTitle() {
		return mObjectTitle;
	}

	public String getUrl() {
		return mUrl;
	}

	public String getMessage() {
		return mMessage;
	}

	public String getTweetImage() {
		return mTweetImage;
	}

	public int getCommentCount() {
		return mCommentCount;
	}

	public String getPubDate() {
		return mPubDate;
	}

	public ObjectReply getObjectReply() {
		return mObjectReply;
	}
}
