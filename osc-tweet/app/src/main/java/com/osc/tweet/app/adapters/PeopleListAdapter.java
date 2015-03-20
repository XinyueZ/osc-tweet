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
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc4j.ds.personal.Gender;
import com.osc4j.ds.personal.People;

import de.greenrobot.event.EventBus;

/**
 * List for showing people: no-relation .
 *
 * @author Xinyue Zhao
 */
public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_people;
	/**
	 * Data-source.
	 */
	private List<People> mData;

	/**
	 * Constructor of {@link UserInfoTweetListAdapter}.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public PeopleListAdapter(List<People> data) {
		setData(data);
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData(List<People> data) {
		mData = data;
	}

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<People> getData() {
		return mData;
	}


	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final People item = mData.get(position);
		holder.mPortraitIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
		holder.mPortraitIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
		holder.mPortraitIv.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new ShowUserInformationEvent(item.getId()));
			}
		});

		holder.mNameTv.setText(item.getName());
		holder.mGenderTv.setText(
				item.getGender() == Gender.Male ? R.string.lbl_user_gender_male : R.string.lbl_user_gender_female);
		holder.mFromTv.setText(item.getFrom());
	}

	/**
	 * ViewHolder for the list.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		private NetworkImageView mPortraitIv;
		private TextView mNameTv;
		private TextView mGenderTv;
		private TextView mFromTv;

		ViewHolder(View convertView) {
			super(convertView);
			mPortraitIv = (NetworkImageView) convertView.findViewById(R.id.portrait_iv);
			mNameTv = (TextView) convertView.findViewById(R.id.user_name_tv);
			mGenderTv = (TextView) convertView.findViewById(R.id.user_gender_tv);
			mFromTv = (TextView) convertView.findViewById(R.id.user_location_tv);


			//			com.osc.tweet.utils.Utils.makeQuickReplyMenuItems(convertView.getContext(), mToolbar.getMenu());
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
