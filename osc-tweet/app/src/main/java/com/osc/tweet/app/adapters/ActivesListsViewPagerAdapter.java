package com.osc.tweet.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.fragments.ActivesListFragment;
import com.osc4j.ds.personal.Actives;
import com.osc4j.ds.personal.MyInformation;

/**
 * Adapter for the pages of actives.
 *
 * @author Xinyue Zhao
 */
public final class ActivesListsViewPagerAdapter extends FragmentPagerAdapter {
	private Context mContext;
	private final int[] TITLES = { R.string.lbl_at_me, R.string.lbl_new_comments };
	private MyInformation myInformation;

	public ActivesListsViewPagerAdapter(Context cxt, FragmentManager fm, MyInformation myInformation) {
		super(fm);
		mContext = cxt;
		this.myInformation = myInformation;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment f = null;
		Actives actives;
		switch (position) {
		case 0:
			actives = new Actives(com.osc4j.ds.common.Status.STATUS_OK, null);
			if (myInformation.getActives() != null) {
				actives.setActivesList(myInformation.getActives());
			}
			f = ActivesListFragment.newInstance(App.Instance, actives);
			break;
		case 1:
			actives = new Actives(com.osc4j.ds.common.Status.STATUS_OK, null);
			if (myInformation.getComments() != null) {
				actives.setActivesList(myInformation.getComments());
			}
			f = ActivesListFragment.newInstance(App.Instance, actives);
		default:
			break;
		}
		return f;
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getString(TITLES[position]);
	}

}
