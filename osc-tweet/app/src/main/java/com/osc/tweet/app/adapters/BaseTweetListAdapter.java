package com.osc.tweet.app.adapters;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.events.CommentTweetEvent;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.events.ShowBigImageEvent;
import com.osc.tweet.events.ShowEditorEvent;
import com.osc.tweet.events.ShowTweetCommentListEvent;
import com.osc.tweet.views.URLImageParser;
import com.osc4j.OscApi;
import com.osc4j.ds.common.StatusResult;
import com.osc4j.ds.tweet.TweetListItem;
import com.osc4j.exceptions.OscTweetException;
import com.osc4j.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Basic class for all adapters that hold list of tweets.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseTweetListAdapter extends RecyclerView.Adapter<BaseTweetListAdapter.ViewHolder> {

	/**
	 * A menu on each line of list.
	 */
	private static final int MENU_LIST_ITEM = R.menu.menu_list_item;
	/**
	 * Data-source.
	 */
	private List<TweetListItem> mData;

	/**
	 * Constructor of {@link com.osc.tweet.app.adapters.UserInfoTweetListAdapter}.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public BaseTweetListAdapter(List<TweetListItem> data) {
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
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final TweetListItem item = mData.get(position);
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

		if (!TextUtils.isEmpty(item.getBody())) {
			Spanned htmlSpan = Html.fromHtml(item.getBody(), new URLImageParser(holder.mBodyTv.getContext(),
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
			holder.mTimeTv.setText(elapsedSeconds);
		} catch (ParseException e) {
			holder.mTimeTv.setText("?");
		}

		Menu menu = holder.mToolbar.getMenu();

		MenuItem atHimMi = menu.findItem(R.id.action_at_him);
		atHimMi.setTitle(String.format(holder.itemView.getContext().getString(R.string.action_at_him),
				item.getAuthor()));
		atHimMi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				EventBus.getDefault().post(new ShowEditorEvent(item.getTitle().toString()));
				return true;
			}
		});

		MenuItem replayMi = menu.findItem(R.id.action_reply);
		replayMi.setTitle(String.format(holder.itemView.getContext().getString(R.string.action_reply_comment),
				item.getAuthor()));
		replayMi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem i) {
				EventBus.getDefault().post(new CommentTweetEvent(item, null));
				return true;
			}
		});

		MenuItem qReplayMi = menu.findItem(R.id.action_quick_reply);
		SubMenu subMenu = qReplayMi.getSubMenu();
		for (int i = 0, size = subMenu.size(); i < size; i++) {
			subMenu.getItem(i).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					AsyncTaskCompat.executeParallel(new AsyncTask<MenuItem, Object, StatusResult>() {
						@Override
						protected StatusResult doInBackground(MenuItem... params) {
							try {
								return OscApi.tweetCommentPub(App.Instance, item, com.chopping.utils.Utils.encode(
										params[0].getTitle().toString()));
							} catch (IOException | OscTweetException e) {
								return null;
							}
						}

						@Override
						protected void onPostExecute(StatusResult s) {
							super.onPostExecute(s);
							EventBus.getDefault().post(new OperatingEvent(s != null && s.getResult() != null && Integer.valueOf(s.getResult().getCode()) ==
									com.osc4j.ds.common.Status.STATUS_OK));
						}
					}, menuItem);
					return true;
				}
			});
		}

		holder.mCommentsTv.setText(item.getCommentCount() + "");
		holder.mCommentsBtn.setVisibility(View.VISIBLE);
		holder.mCommentsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new ShowTweetCommentListEvent(item));
			}
		});
	}

	/**
	 * ViewHolder for the list.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView mAuthorTv;
		private TextView mBodyTv;
		private TextView mTimeTv;
		private TextView mCommentsTv;
		private ImageButton mCommentsBtn;
		private NetworkImageView mSmallImgIv;
		private Toolbar mToolbar;

		ViewHolder(View convertView) {
			super(convertView);
			mAuthorTv = (TextView) convertView.findViewById(R.id.author_tv);
			mBodyTv = (TextView) convertView.findViewById(R.id.body_tv);
			mTimeTv = (TextView) convertView.findViewById(R.id.time_tv);
			mCommentsTv = (TextView) convertView.findViewById(R.id.comments_tv);
			mCommentsBtn = (ImageButton) convertView.findViewById(R.id.comments_list_btn);
			mSmallImgIv = (NetworkImageView) convertView.findViewById(R.id.small_img_iv);
			mToolbar = (Toolbar) convertView.findViewById(R.id.toolbar);
			mToolbar.inflateMenu(MENU_LIST_ITEM);

			com.osc.tweet.utils.Utils.makeQuickReplyMenuItems(convertView.getContext(), mToolbar.getMenu());
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		View convertView = LayoutInflater.from(cxt).inflate(getItemLayout(), parent, false);
		return getViewHolder(convertView);
	}

	/**
	 * @return Item's layout resource.
	 */
	protected abstract int getItemLayout();

	/**
	 * The holder.
	 *
	 * @param convertView
	 * 		The host view.
	 *
	 * @return The holder.
	 */
	protected ViewHolder getViewHolder(View convertView) {
		return new ViewHolder(convertView);
	}
}
