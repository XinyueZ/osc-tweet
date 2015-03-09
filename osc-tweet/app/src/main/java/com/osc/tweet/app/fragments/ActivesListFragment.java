package com.osc.tweet.app.fragments;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.ActivesListAdapter;
import com.osc.tweet.utils.Prefs;
import com.osc4j.ds.personal.Active;
import com.osc4j.ds.personal.Actives;

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
	 * Adapter for {@link #mRv} to show all actives.
	 */
	private ActivesListAdapter mAdp;
	/**
	 * List container for showing all actives.
	 */
	private RecyclerView mRv;

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
		mRv = (RecyclerView) view.findViewById(R.id.child_view);
		mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRv.addItemDecoration(new SpacesItemDecoration((int) com.osc4j.utils.Utils.convertPixelsToDp(App.Instance,
				5)));
		mRv.setItemAnimator(new DefaultItemAnimator());
		mRv.setHasFixedSize(false);
		List<Active> activesList = getActives().getActives();
		if (activesList != null) {
			mRv.setAdapter(mAdp = new ActivesListAdapter(activesList));
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
			outRect.bottom = space;

			// Add top margin only for the first item to avoid double space between items
			if (parent.getChildPosition(view) == 0) {
				outRect.top = space * 2;
			}
		}
	}
}
