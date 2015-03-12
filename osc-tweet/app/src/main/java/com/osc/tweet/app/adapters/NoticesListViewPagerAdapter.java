package com.osc.tweet.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.fragments.NoticesListPageFragment;
import com.osc4j.ds.common.NoticeType;
import com.osc4j.ds.personal.MyInformation;
import com.osc4j.ds.personal.Notices;

/**
 * Adapter for the pages of notices.
 *
 * @author Xinyue Zhao
 */
public final class NoticesListViewPagerAdapter extends FragmentStatePagerAdapter {
	/**
	 * {@link Context}
	 */
	private Context mContext;
	/**
	 * Title of tabs.
	 */
	private final int[] TITLES = { R.string.lbl_at_me, R.string.lbl_new_comments };
	/**
	 * The whole data source for each page.
	 */
	private MyInformation myInformation;

	/**
	 * Constructor of {@link NoticesListViewPagerAdapter}
	 *
	 * @param cxt
	 * 		{@link Context}
	 * @param fm
	 * 		{@link FragmentManager}.
	 * @param myInformation
	 * 		The whole data source for each page.
	 */
	public NoticesListViewPagerAdapter(Context cxt, FragmentManager fm, MyInformation myInformation) {
		super(fm);
		mContext = cxt;
		this.myInformation = myInformation;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment f = null;
		if (myInformation == null) {
			f = NoticesListPageFragment.newInstance(App.Instance, null, NoticeType.Null);
		} else {
			Notices notices;
			switch (position) {
			case 0:
				notices = new Notices(com.osc4j.ds.common.Status.STATUS_OK, null);
				if (myInformation.getNotices() != null) {
					notices.setNotices(myInformation.getNotices());
				}
				f = NoticesListPageFragment.newInstance(App.Instance, notices, NoticeType.AtMe);
				break;
			case 1:
				notices = new Notices(com.osc4j.ds.common.Status.STATUS_OK, null);
				if (myInformation.getComments() != null) {
					notices.setNotices(myInformation.getComments());
				}
				f = NoticesListPageFragment.newInstance(App.Instance, notices, NoticeType.Comments);
			default:
				break;
			}
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
