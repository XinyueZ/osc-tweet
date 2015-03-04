package com.osc.tweet.app.adapters;

import java.util.List;

import android.view.View;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc4j.ds.tweet.TweetListItem;

import de.greenrobot.event.EventBus;

/**
 * The adapter for tweet-list.
 *
 * @author Xinyue Zhao
 */
public final class TweetListAdapter extends BaseTweetListAdapter {
	public TweetListAdapter(List<TweetListItem> data) {
		super(data);
	}

	@Override
	public void onBindViewHolder(BaseTweetListAdapter.ViewHolder holder, int position) {
		final TweetListItem item = getData().get(position);
		ViewHolder thisHolder = (ViewHolder) holder;
		thisHolder.mPortraitIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
		thisHolder.mPortraitIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
		thisHolder.mPortraitIv.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new ShowUserInformationEvent(item.getAuthorId()));
			}
		});
		super.onBindViewHolder(holder, position);
	}

	static class ViewHolder extends BaseTweetListAdapter.ViewHolder {
		private NetworkImageView mPortraitIv;

		private ViewHolder(View convertView) {
			super(convertView);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
		}
	}


	@Override
	protected int getItemLayout() {
		return R.layout.item_tweet_line;
	}

	@Override
	protected BaseTweetListAdapter.ViewHolder getViewHolder(View convertView) {
		return new TweetListAdapter.ViewHolder(convertView);
	}
}
