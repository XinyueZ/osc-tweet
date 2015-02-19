package com.osc.tweet.app.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.events.ShowBigImageEvent;
import com.osc.tweet.views.URLImageParser;
import com.osc4j.ds.tweet.TweetListItem;
import com.osc4j.utils.Utils;

import de.greenrobot.event.EventBus;

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
	 *
	 * @param data
	 * 		Data-source.
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
	 *
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

		if (!TextUtils.isEmpty(item.getImgSmall())) {
			holder.mSmallImgIv.setVisibility(View.VISIBLE);
			holder.mSmallImgIv.setDefaultImageResId(R.drawable.ic_not_loaded);
			holder.mSmallImgIv.setImageUrl(item.getImgSmall(), TaskHelper.getImageLoader());
		} else {
			holder.mSmallImgIv.setVisibility(View.GONE);
		}

		holder.mSmallImgIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new ShowBigImageEvent(item));
			}
		});
		holder.mAuthorTv.setText(item.getAuthor());
		holder.mComments.setText(item.getCommentCount() + "");

		if (!TextUtils.isEmpty(item.getBody())) {
			Spanned htmlSpan = Html.fromHtml(item.getBody(), new URLImageParser(holder.mBodyTv.getContext(), holder.mBodyTv), null);
			holder.mBodyTv.setText(htmlSpan);
			holder.mBodyTv.setMovementMethod(LinkMovementMethod.getInstance());
			holder.mBodyTv.setVisibility(View.VISIBLE);
		} else {
			holder.mBodyTv.setVisibility(View.GONE);
		}

		Calendar editTime = Calendar.getInstance();
		try {
			editTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.getPubDate()));
			editTime = Utils.transformTime(editTime, TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
			CharSequence elapsedSeconds = DateUtils.getRelativeTimeSpanString(editTime.getTimeInMillis(),
					System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
			holder.mTime.setText(elapsedSeconds);
		} catch (ParseException e) {
			holder.mTime.setText("?");
		}

		holder.mToolbar.getMenu().findItem(R.id.action_at_him).setTitle(String.format(
				holder.itemView.getContext().getString(R.string.action_at_him), item.getAuthor()));
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
		private Toolbar mToolbar;

		private ViewHolder(View convertView) {
			super(convertView);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
			mAuthorTv = (TextView) convertView.findViewById(R.id.author_tv);
			mBodyTv = (TextView) convertView.findViewById(R.id.body_tv);
			mTime = (TextView) convertView.findViewById(R.id.time_tv);
			mComments = (TextView) convertView.findViewById(R.id.comments_tv);
			mSmallImgIv = (NetworkImageView) convertView.findViewById(R.id.small_img_iv);
			mToolbar = (Toolbar) convertView.findViewById(R.id.toolbar);
			mToolbar.inflateMenu(R.menu.menu_list_item);
		}
	}
}
