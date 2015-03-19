/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG, Never BUG.
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱。
package com.osc.tweet.app;

import java.util.List;

import android.app.Application;
import android.webkit.CookieSyncManager;

import com.chopping.net.TaskHelper;
import com.facebook.stetho.Stetho;
import com.osc.tweet.utils.Prefs;
import com.osc4j.ds.tweet.TweetListItem;


/**
 * The app-object of the project.
 *
 * @author Xinyue Zhao
 */
public final class App extends Application {
	/**
	 * Application's instance.
	 */
	public static App Instance;
	/**
	 * The list of all saved favorite tweets.
	 */
	private List<TweetListItem>  mTweetFavorites;

	{
		Instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CookieSyncManager.createInstance(this);
		Prefs.createInstance(this);
		TaskHelper.init(getApplicationContext());

		Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
				.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());
	}

	/**
	 * To know whether a tweet-item has been in favorite-list or not.
	 *
	 * @param item
	 * 		{@link TweetListItem}.
	 *
	 * @return {@code true} if the item has been favorite.
	 */
	public boolean isFavorite(TweetListItem item) {
		if (mTweetFavorites == null) {
			return false;
		}
		for (TweetListItem t : mTweetFavorites) {
			if (t.getId() == item.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a {@link TweetListItem} to favorite-list.
	 * @param item {@link TweetListItem}.
	 */
	public void addFavorite(TweetListItem item) {
		mTweetFavorites.add(0, item);
	}


	/**
	 * Delete a {@link TweetListItem} to favorite-list.
	 * @param item {@link TweetListItem}.
	 */
	public void deleteFavorite(TweetListItem item) {
		for(TweetListItem fi : mTweetFavorites) {
			if(fi.getId() == item.getId()) {
				mTweetFavorites.remove(fi);
				return;
			}
		}
	}

	/**
	 * Set   list of all saved favorite tweets.
	 *
	 * @param tweetFavoritesList
	 * 		The list of all saved favorite tweets.
	 */
	public void setTweetFavoritesList(List<TweetListItem>  tweetFavoritesList) {
		mTweetFavorites = tweetFavoritesList;
	}

	/**
	 *
	 * @return The list of all saved favorite tweets.
	 */
	public List<TweetListItem>  getTweetFavoritesList() {
		return mTweetFavorites;
	}
}
