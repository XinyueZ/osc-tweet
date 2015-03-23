package com.osc.tweet.app.adapters;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.events.ShowEditorEvent;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
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
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final Friend friend = mData.get(position);
		holder.mPortraitIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
		holder.mPortraitIv.setImageUrl(friend.getPortrait(), TaskHelper.getImageLoader());
		holder.mPortraitIv.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new ShowEditorEvent("@" + friend.getName() + ": "));
			}
		});
		holder.mPortraitIv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.add(String.format(v.getContext().getString(R.string.action_at_him), friend.getName())).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								EventBus.getDefault().post(new ShowEditorEvent("@" + friend.getName() + ": "));
								return true;
							}
						});
				menu.add(R.string.action_personal_information).setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						EventBus.getDefault().post(new ShowUserInformationEvent(friend.getUserId()));
						return true;
					}
				});
			}
		});
	}

	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}


	static class ViewHolder extends RecyclerView.ViewHolder  {
		private NetworkImageView mPortraitIv;

		private ViewHolder(View convertView) {
			super(convertView);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
		}
	}
}
