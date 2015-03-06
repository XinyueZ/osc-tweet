package com.osc.tweet.app.fragments;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.chopping.net.TaskHelper;
import com.osc.tweet.R;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc.tweet.views.URLImageParser;
import com.osc4j.ds.personal.Active;
import com.osc4j.ds.personal.Actives;

import de.greenrobot.event.EventBus;

/**
 * Show list of actives.
 */
public final class ActivesListFragment extends BaseFragment {
	private static final String EXTRAS_ACTIVES = ActivesListFragment.class.getName() + ".EXTRAS.actives";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_actives_list;
	/**
	 * Container for all {@link Active}s
	 */
	private ViewGroup mActivesVg;

	/**
	 * Init a {@link ActivesListFragment}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param actives
	 * 		{@link com.osc4j.ds.personal.Actives}, that contains objects to show.
	 *
	 * @return {@link ActivesListFragment}.
	 */
	public static Fragment newInstance(Context context, Actives actives) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_ACTIVES, actives);
		return ActivesListFragment.instantiate(context, ActivesListFragment.class.getName(), args);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mActivesVg = (ViewGroup) view.findViewById(R.id.actives_ll);
		List<Active> activesList = getActives().getActives();
		if (activesList != null) {
			for (final Active item : activesList) {
				View v = getActivity().getLayoutInflater().inflate(R.layout.item_actives_list, (ViewGroup) view, false);
				TextView messageTv = (TextView) v.findViewById(R.id.message_tv);
				NetworkImageView portraitIv = (NetworkImageView) v.findViewById(R.id.portrait_iv);
				NetworkImageView imageIv = (NetworkImageView) v.findViewById(R.id.small_img_iv);
				Button replyBtn = (Button) v.findViewById(R.id.reply_btn);
				Button openBtn = (Button) v.findViewById(R.id.open_btn);
				portraitIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
				portraitIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
				portraitIv.setOnClickListener(new OnViewAnimatedClickedListener() {
					@Override
					public void onClick() {
						EventBus.getDefault().post(new ShowUserInformationEvent(item.getAuthorId()));
					}
				});


				imageIv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//EventBus.getDefault().post(new ShowBigImageEvent(item));
					}
				});

				if (!TextUtils.isEmpty(item.getMessage())) {
					Spanned htmlSpan = Html.fromHtml(item.getMessage(), new URLImageParser(messageTv.getContext(),
							messageTv), null);
					messageTv.setText(htmlSpan);
					messageTv.setMovementMethod(LinkMovementMethod.getInstance());
					messageTv.setVisibility(View.VISIBLE);
				} else {
					messageTv.setVisibility(View.GONE);
				}

				replyBtn.setText(getString(R.string.action_reply_comment) + " " + item.getAuthor());

				mActivesVg.addView(v);
			}
		}
	}

	/**
	 * @return {@link Active}s to show.
	 */
	private Actives getActives() {
		return (Actives) getArguments().getSerializable(EXTRAS_ACTIVES);
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
		private int space;

		public SpacesItemDecoration(int space) {
			this.space = space;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			outRect.left = space;
			outRect.right = space;
			outRect.bottom = space * 2;

			// Add top margin only for the first item to avoid double space between items
			if (parent.getChildPosition(view) == 0) {
				outRect.top = space * 2;
			}
		}
	}
}
