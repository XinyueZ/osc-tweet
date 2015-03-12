package com.osc.tweet.app.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.NoticesListsViewPagerAdapter;
import com.osc.tweet.events.GetMyInformationEvent;
import com.osc.tweet.utils.Prefs;
import com.osc4j.ds.personal.MyInformation;

/**
 * A container shows all my new notices.
 *
 * @author Xinyue Zhao
 */
public final class MyNoticesFragment extends BaseFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_my_notices;
	/**
	 * Tabs.
	 */
	private PagerSlidingTabStrip mTabs;

	/**
	 * The pagers
	 */
	private ViewPager mViewPager;

	/**
	 * My personal information .
	 */
	private MyInformation myInfo;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.osc.tweet.events.GetMyInformationEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.GetMyInformationEvent}.
	 */
	public void onEvent(GetMyInformationEvent e) {
		myInfo = e.getMyInformation();
		showMyNotices();
	}


	//------------------------------------------------


	/**
	 * Initialize an {@link  MyNoticesFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link MyNoticesFragment}.
	 */
	public static MyNoticesFragment newInstance(Context context) {
		return (MyNoticesFragment) Fragment.instantiate(context, MyNoticesFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("myinfo", myInfo);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mViewPager = (ViewPager) view.findViewById(R.id.my_notices_vp);
		// Bind the tabs to the ViewPager
		mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.my_notices_tabs);
		mTabs.setIndicatorColorResource(R.color.common_white);

		if (savedInstanceState != null) {
			myInfo = (MyInformation) savedInstanceState.getSerializable("myinfo");
		}

		showMyNotices();
	}

	/**
	 * Show all my current new notices.
	 */
	private void showMyNotices() {
		if (myInfo != null && myInfo.getAm() != null) {
			mViewPager.setAdapter(new NoticesListsViewPagerAdapter(App.Instance, getChildFragmentManager(), myInfo));
			mTabs.setViewPager(mViewPager);
		} else {
			mViewPager.setAdapter(new NoticesListsViewPagerAdapter(App.Instance, getChildFragmentManager(), null));
			mTabs.setViewPager(mViewPager);
		}
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
