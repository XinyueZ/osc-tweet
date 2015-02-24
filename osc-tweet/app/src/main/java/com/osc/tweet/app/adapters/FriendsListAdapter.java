package com.osc.tweet.app.adapters;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.events.ShowUserInformation;
import com.osc4j.ds.personal.Friend;

import de.greenrobot.event.EventBus;

/**
 * The adapter for friends-list.
 *
 * @author Xinyue Zhao
 */
public final class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {
	/**
	 * Data-source.
	 */
	private List<Friend> mData;
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_friends_list;

	/**
	 * Constructor of {@link com.osc.tweet.app.adapters.FriendsListAdapter}.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public FriendsListAdapter(List<Friend> data) {
		setData(data);
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData(List<Friend> data) {
		mData = data;
	}

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<Friend> getData() {
		return mData;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		View convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT, parent, false);
		FriendsListAdapter.ViewHolder viewHolder = new FriendsListAdapter.ViewHolder(convertView);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Friend item = mData.get(position);
		holder.mPortraitIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
		holder.mPortraitIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
		holder.mPortraitIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new ShowUserInformation(item.getUserId()));
			}
		});

	}

	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		private NetworkImageView mPortraitIv;

		private ViewHolder(View convertView) {
			super(convertView);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
		}
	}
}
