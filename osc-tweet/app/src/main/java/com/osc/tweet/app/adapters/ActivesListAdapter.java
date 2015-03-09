package com.osc.tweet.app.adapters;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc.tweet.views.URLImageParser;
import com.osc4j.ds.personal.Active;

import de.greenrobot.event.EventBus;

/**
 * The adapter that hold list of actives.
 *
 * @author Xinyue Zhao
 */
public final class ActivesListAdapter extends RecyclerView.Adapter<ActivesListAdapter.ViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_actives_list;
	/**
	 * Data-source.
	 */
	private List<Active> mData;

	/**
	 * Constructor of {@link ActivesListAdapter}.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public ActivesListAdapter(List<Active> data) {
		setData(data);
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData(List<Active> data) {
		mData = data;
	}

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<Active> getData() {
		return mData;
	}


	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Active item = getData().get(position);
		holder.mPortraitIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
		holder.mPortraitIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
		holder.mPortraitIv.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new ShowUserInformationEvent(item.getAuthorId()));
			}
		});


		holder.mImageIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//EventBus.getDefault().post(new ShowBigImageEvent(item));
			}
		});

		if (!TextUtils.isEmpty(item.getMessage())) {
			Spanned htmlSpan = Html.fromHtml(item.getMessage(), new URLImageParser(holder.messageTv.getContext(),
					holder.messageTv), null);
			holder.messageTv.setText(htmlSpan);
			holder.messageTv.setMovementMethod(LinkMovementMethod.getInstance());
			holder.messageTv.setVisibility(View.VISIBLE);
		} else {
			holder.messageTv.setVisibility(View.GONE);
		}

		Context cxt = holder.itemView.getContext();
		holder.mReplyBtn.setText(cxt.getString(R.string.action_reply_comment) + " " + item.getAuthor());
	}

	/**
	 * ViewHolder for the list.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		private NetworkImageView mPortraitIv;
		private TextView messageTv;
		private NetworkImageView mImageIv;
		private Button mReplyBtn;
		private Button mOpenBtn;

		private ViewHolder(View convertView) {
			super(convertView);
			messageTv = (TextView) convertView.findViewById(R.id.message_tv);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
			mImageIv = (NetworkImageView) convertView.findViewById(R.id.small_img_iv);
			mReplyBtn = (Button) convertView.findViewById(R.id.reply_btn);
			mOpenBtn = (Button) convertView.findViewById(R.id.open_btn);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		View convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT, parent, false);
		return new ViewHolder(convertView);
	}
}
