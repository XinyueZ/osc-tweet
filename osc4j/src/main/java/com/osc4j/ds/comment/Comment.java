package com.osc4j.ds.comment;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Comment implements Serializable {
	@SerializedName("content")
	private String mContent  ;
	@SerializedName("id")
	private long mId       ;
	@SerializedName("pubDate")
	private String mPubDate;
	@SerializedName("client_type")
	private int mClientType;
	@SerializedName("commentAuthor")
	private String mCommentAuthor;
	@SerializedName("commentAuthorId")
	private long mCommentAuthorId  ;
	@SerializedName("commentPortrait")
	private String mCommentPortrait;
	@SerializedName("refers")
	private List<CommentListRefer> mRefers;
	@SerializedName("replies")
	private List<CommentListReply> mReplies;


	public Comment(String content, long id, String pubDate, int clientType, String commentAuthor,
			long commentAuthorId, String commentPortrait, List<CommentListRefer> refers,
			List<CommentListReply> replies) {
		mContent = content;
		mId = id;
		mPubDate = pubDate;
		mClientType = clientType;
		mCommentAuthor = commentAuthor;
		mCommentAuthorId = commentAuthorId;
		mCommentPortrait = commentPortrait;
		mRefers = refers;
		mReplies = replies;
	}


	public String getContent() {
		return mContent;
	}

	public long getId() {
		return mId;
	}

	public String getPubDate() {
		return mPubDate;
	}

	public int getClientType() {
		return mClientType;
	}

	public String getCommentAuthor() {
		return mCommentAuthor;
	}

	public long getCommentAuthorId() {
		return mCommentAuthorId;
	}

	public String getCommentPortrait() {
		return mCommentPortrait;
	}

	public List<CommentListRefer> getRefers() {
		return mRefers;
	}

	public List<CommentListReply> getReplies() {
		return mReplies;
	}
}
