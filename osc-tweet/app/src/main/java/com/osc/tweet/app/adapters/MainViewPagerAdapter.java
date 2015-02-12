package com.osc.tweet.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.osc.tweet.R;
import com.osc.tweet.app.fragments.TweetListFragment;


/**
 * Adapter for main viewpager.
 *
 * @author Xinyue Zhao
 */
public final class MainViewPagerAdapter extends FragmentPagerAdapter {
	private Context mContext;
	private final int[] TITLES = { R.string.lbl_all, R.string.lbl_my };
	private int mScrollY;

	public MainViewPagerAdapter(Context cxt, FragmentManager fm) {
		super(fm);
		mContext = cxt;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment f = null;
		switch (position) {
		case 1:
			f = TweetListFragment.newInstance(mContext, true);
			break;
		case 0:
			f = TweetListFragment.newInstance(mContext, false);
		default:
			break;
		}
		return f;
	}

	@Override
	public int getCount() {
		return 2;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getString(TITLES[position]);
	}

	public void setScrollY(int scrollY) {
		mScrollY = scrollY;
	}

	public Fragment getItemAt(int position) {
		return getItem(position);
	}

}
