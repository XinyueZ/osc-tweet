package com.osc.tweet.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.fragments.NoticesListFragment;
import com.osc4j.ds.common.NoticeType;
import com.osc4j.ds.personal.Notices;
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
		Notices notices;
		switch (position) {
		case 0:
			notices = new Notices(com.osc4j.ds.common.Status.STATUS_OK, null);
			if (myInformation.getNotices() != null) {
				notices.setNotices(myInformation.getNotices());
			}
			f = NoticesListFragment.newInstance(App.Instance, notices, NoticeType.AtMe);
			break;
		case 1:
			notices = new Notices(com.osc4j.ds.common.Status.STATUS_OK, null);
			if (myInformation.getComments() != null) {
				notices.setNotices(myInformation.getComments());
			}
			f = NoticesListFragment.newInstance(App.Instance, notices, NoticeType.Comments);
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
		String s = mContext.getString(TITLES[position]);
		int n = 0;
		switch (position) {
		case 0:
			if (myInformation.getNotices() != null) {
				n = myInformation.getNotices().size();
			}
			break;
		case 1:
			if (myInformation.getComments() != null) {
				n =myInformation.getComments().size();
			}
			break;
		default:
			break;
		}

		s = String.format("%s (%d)", s, n);
		return s;
	}

}
