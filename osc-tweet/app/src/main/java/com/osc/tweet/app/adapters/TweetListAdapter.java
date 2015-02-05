package com.osc.tweet.app.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.utils.URLImageParser;
import com.osctweet4j.ds.TweetListItem;

/**
 * The adapter for tweet-list.
 *
 * @author Xinyue Zhao
 */
public final class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.ViewHolder> {
	/**
	 * Data-source.
	 */
	private List<TweetListItem> mData;
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_tweet_line;

	/**
	 * Constructor of {@link com.osc.tweet.app.adapters.TweetListAdapter}.
	 * @param data Data-source.
	 */
	public TweetListAdapter(List<TweetListItem> data) {
		setData(data);
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData(List<TweetListItem> data) {
		mData = data;
	}

	/**
	 * Get current used data-source.
	 * @return The data-source.
	 */
	public List<TweetListItem> getData() {
		return mData;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		View convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT, parent, false);
		TweetListAdapter.ViewHolder viewHolder = new TweetListAdapter.ViewHolder(convertView);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final TweetListItem item = mData.get(position);
		holder.mPortraitIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
		holder.mPortraitIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
		holder.mSmallImgIv.setImageUrl(item.getImgSmall(), TaskHelper.getImageLoader());
		holder.mAuthorTv.setText(item.getAuthor());
		holder.mComments.setText(item.getCommentCount() + "");

		Spanned htmlSpan = Html.fromHtml(
				item.getBody(),
				new URLImageParser(holder.mBodyTv.getContext(), holder.mBodyTv),
				null);
		holder.mBodyTv.setText(htmlSpan);
		holder.mBodyTv.setMovementMethod(LinkMovementMethod.getInstance());

		Calendar editTime = Calendar.getInstance();
		try {
			editTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.getPubDate()));
			CharSequence elapsedSeconds = DateUtils.getRelativeTimeSpanString(editTime.getTimeInMillis(),
					System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
			holder.mTime.setText(elapsedSeconds);
		} catch (ParseException e) {
			holder.mTime.setText("?");
		}
	}

	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		private NetworkImageView mPortraitIv;
		private TextView mAuthorTv;
		private TextView mBodyTv;
		private TextView mTime;
		private TextView mComments;
		private NetworkImageView mSmallImgIv;

		private ViewHolder(View convertView) {
			super(convertView);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
			mAuthorTv = (TextView) convertView.findViewById(R.id.author_tv);
			mBodyTv = (TextView) convertView.findViewById(R.id.body_tv);
			mTime = (TextView) convertView.findViewById(R.id.time_tv);
			mComments = (TextView) convertView.findViewById(R.id.comments_tv);
			mSmallImgIv = (NetworkImageView) convertView.findViewById(R.id.small_img_iv);
		}
	}
}
