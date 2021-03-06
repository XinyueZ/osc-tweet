package com.osc.tweet.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.osc.tweet.R;
import com.osc.tweet.views.URLImageParser;
import com.osc4j.ds.comment.Comment;
import com.osc4j.ds.personal.Notice;
import com.osc4j.ds.tweet.TweetListItem;

/**
 * Util methods.
 *
 * @author Xinyue Zhao
 */
public final class Utils {
	/**
	 * There is different between android pre 3.0 and 3.x, 4.x on this wording.
	 */
	public static final String ALPHA =
			(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) ? "alpha" : "Alpha";
	/**
	 * Standard sharing app for sharing on actionbar.
	 */
	public static Intent getDefaultShareIntent(android.support.v7.widget.ShareActionProvider provider, String subject,
			String body) {
		if (provider != null) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			i.putExtra(android.content.Intent.EXTRA_TEXT, body);
			provider.setShareIntent(i);
			return i;
		}
		return null;
	}

	/**
	 * Make a list of quick reply messages.
	 * @param context {@link android.content.Context}.
	 * @param menu The root menu.
	 */
	public static void makeQuickReplyMenuItems(Context context, Menu menu ) {
		MenuItem qReplyMi = menu.findItem(R.id.action_quick_reply);
		SubMenu subMenu = qReplyMi.getSubMenu();
		String[] quickReplyMessages = Prefs.getInstance().getQuickReplyList(context);
		for (String s : quickReplyMessages) {
			subMenu.add(s);
		}
	}

	/**
	 * Show a {@link com.osc4j.ds.tweet.TweetListItem} on {@link android.widget.TextView}.
	 * @param cxt {@link android.content.Context}.
	 * @param tweetItemContentTv {@link android.widget.TextView} to show item.
	 * @param item {@link com.osc4j.ds.tweet.TweetListItem}.
	 */
	public static void showTweetListItem(Context cxt, TextView tweetItemContentTv, TweetListItem item) {
		Spanned htmlSpan = Html.fromHtml(item.getBody(), new URLImageParser(cxt, tweetItemContentTv), null);
		tweetItemContentTv.setText(htmlSpan);
		tweetItemContentTv.setMovementMethod(LinkMovementMethod.getInstance());
		tweetItemContentTv.setVisibility(View.VISIBLE);
	}

	/**
	 * Show a {@link com.osc4j.ds.comment.Comment} on {@link android.widget.TextView}.
	 * @param cxt {@link android.content.Context}.
	 * @param tweetItemContentTv {@link android.widget.TextView} to show item.
	 * @param item {@link Comment}
	 */
	public static void showComment(Context cxt, TextView tweetItemContentTv, Comment item) {
		Spanned htmlSpan = Html.fromHtml(item.getContent(), new URLImageParser(cxt, tweetItemContentTv), null);
		tweetItemContentTv.setText(htmlSpan);
		tweetItemContentTv.setMovementMethod(LinkMovementMethod.getInstance());
		tweetItemContentTv.setVisibility(View.VISIBLE);
	}

	/**
	 * Show a {@link com.osc4j.ds.comment.Comment} on {@link android.widget.TextView}.
	 * @param cxt {@link android.content.Context}.
	 * @param tweetItemContentTv {@link android.widget.TextView} to show item.
	 * @param item {@link com.osc4j.ds.personal.Notice}
	 */
	public static void showNotice(Context cxt, TextView tweetItemContentTv, Notice item) {
		Spanned htmlSpan = Html.fromHtml(item.getMessage(), new URLImageParser(cxt, tweetItemContentTv), null);
		tweetItemContentTv.setText(htmlSpan);
		tweetItemContentTv.setMovementMethod(LinkMovementMethod.getInstance());
		tweetItemContentTv.setVisibility(View.VISIBLE);
	}


	/**
	 * Action or operation feedback with vibration
	 * @param cxt {@link Context}.
	 */
	public static void vibrationFeedback(Context cxt) {
		if(Prefs.getInstance().settingVibrationFeedback()) {
			((Vibrator) cxt.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
		}
	}
}
