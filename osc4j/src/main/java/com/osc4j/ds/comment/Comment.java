package com.osc4j.ds.comment;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Comment implements Serializable {
	@SerializedName("content")
	private String mContent  ;
	@SerializedName("id")
	private int mId       ;
	@SerializedName("pubDate")
	private String mPubDate;
	@SerializedName("client_type")
	private int mClientType;
	@SerializedName("commentAuthor")
	private String mCommentAuthor;
	@SerializedName("commentAuthorId")
	private int mCommentAuthorId  ;
	@SerializedName("commentPortrait")
	private String mCommentPortrait;
	@SerializedName("refers")
	private List<CommentListRefer> mRefers;
	@SerializedName("replies")
	private List<CommentListReply> mReplies;


	public Comment(String content, int id, String pubDate, int clientType, String commentAuthor,
			int commentAuthorId, String commentPortrait, List<CommentListRefer> refers,
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

	public int getId() {
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

	public int getCommentAuthorId() {
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
