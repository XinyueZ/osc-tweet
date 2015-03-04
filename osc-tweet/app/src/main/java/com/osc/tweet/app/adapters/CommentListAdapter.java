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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.events.ShowEditorEvent;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc.tweet.views.URLImageParser;
import com.osc4j.ds.comment.Comment;
import com.osc4j.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * The adapter for tweet-list.
 *
 * @author Xinyue Zhao
 */
public final class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
	/**
	 * Data-source.
	 */
	private List<Comment> mData;
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_comment_line;

	/**
	 * A menu on each line of list.
	 */
	private static final int MENU_LIST_ITEM = R.menu.menu_list_item;

	/**
	 * Constructor of {@link com.osc.tweet.app.adapters.CommentListAdapter}.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public CommentListAdapter(List<Comment> data) {
		setData(data);
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData(List<Comment> data) {
		mData = data;
	}

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<Comment> getData() {
		return mData;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		View convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT, parent, false);
		CommentListAdapter.ViewHolder viewHolder = new CommentListAdapter.ViewHolder(convertView);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Comment item = mData.get(position);
		holder.mPortraitIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
		holder.mPortraitIv.setImageUrl(item.getCommentPortrait(), TaskHelper.getImageLoader());
		holder.mPortraitIv.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new ShowUserInformationEvent(item.getCommentAuthorId()));
			}
		});


		holder.mAuthorTv.setText(item.getCommentAuthor());

		if (!TextUtils.isEmpty(item.getContent())) {
			Spanned htmlSpan = Html.fromHtml(item.getContent(), new URLImageParser(holder.mBodyTv.getContext(),
					holder.mBodyTv), null);
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


		final Menu menu = holder.mToolbar.getMenu();

		MenuItem atHimMi = menu.findItem(R.id.action_at_him);
		atHimMi.setTitle(String.format(holder.itemView.getContext().getString(R.string.action_at_him),
				item.getCommentAuthor()));
		atHimMi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				EventBus.getDefault().post(new ShowEditorEvent(item.getTitle().toString()));
				return true;
			}
		});

		MenuItem replayMi = menu.findItem(R.id.action_reply);
		replayMi.setTitle(String.format(holder.itemView.getContext().getString(R.string.action_reply_comment),
				item.getCommentAuthor()));
		replayMi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem i) {
				//EventBus.getDefault().post(new CommentTweetEvent(item));
				return true;
			}
		});

		MenuItem qReplayMi = menu.findItem(R.id.action_quick_reply);
		SubMenu subMenu = qReplayMi.getSubMenu();
		for (int i = 0, size = subMenu.size(); i < size; i++) {
			subMenu.getItem(i).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					//EventBus.getDefault().post(new CommentTweetEvent(item, menuItem.getTitle().toString()));
					return true;
				}
			});
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
		private Toolbar mToolbar;

		private ViewHolder(View convertView) {
			super(convertView);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
			mAuthorTv = (TextView) convertView.findViewById(R.id.author_tv);
			mBodyTv = (TextView) convertView.findViewById(R.id.body_tv);
			mTime = (TextView) convertView.findViewById(R.id.time_tv);
			mToolbar = (Toolbar) convertView.findViewById(R.id.toolbar);
			mToolbar.inflateMenu(MENU_LIST_ITEM);


			com.osc.tweet.utils.Utils.makeQuickReplyMenuItems(convertView.getContext(), mToolbar.getMenu());
		}
	}
}