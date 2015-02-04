package com.osc.tweet.app.adapters;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
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
		holder.mPortraitIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
		holder.mAuthorTv.setText(item.getAuthor());
		holder.mBodyTv.setText(item.getBody());
	}

	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		private NetworkImageView mPortraitIv;
		private TextView mAuthorTv;
		private TextView mBodyTv;

		private ViewHolder(View convertView) {
			super(convertView);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
			mAuthorTv = (TextView) convertView.findViewById(R.id.author_tv);
			mBodyTv = (TextView) convertView.findViewById(R.id.body_tv);
		}
	}
}
