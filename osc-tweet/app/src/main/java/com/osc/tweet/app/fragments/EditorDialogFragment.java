package com.osc.tweet.app.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.TaskHelper;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.chopping.utils.Utils;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.events.LoadEvent;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc4j.OscApi;
import com.osc4j.ds.comment.Comment;
import com.osc4j.ds.common.StatusResult;
import com.osc4j.ds.personal.Notice;
import com.osc4j.ds.tweet.TweetListItem;
import com.osc4j.exceptions.OscTweetException;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/**
 * Message editor.
 *
 * @author Xinyue Zhao
 */
public final class EditorDialogFragment extends DialogFragment implements OnMenuItemClickListener {
	private static final String EXTRAS_DEFAULT_TEXT = EditorDialogFragment.class.getName() + ".EXTRAS.defaultText";
	private static final String EXTRAS_DEFAULT_FIXED = EditorDialogFragment.class.getName() + ".EXTRAS.defaultFixed";
	private static final String EXTRAS_TWEET_ITEM = EditorDialogFragment.class.getName() + ".EXTRAS.tweetItem";
	private static final String EXTRAS_COMMENT = EditorDialogFragment.class.getName() + ".EXTRAS.comment";
	private static final String EXTRAS_NOTICE = EditorDialogFragment.class.getName() + ".EXTRAS.notice";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_dialog_editor;
	/**
	 * Menu for the editor.
	 */
	private static final int MENU = R.menu.menu_editor;
	/**
	 * An "action-bar" for the editor.
	 */
	private Toolbar mToolbar;
	/**
	 * Editor for message.
	 */
	private EditText mEditText;
	/**
	 * {@link android.view.MenuItem} clicked to send message.
	 */
	private MenuItem mSendMi;
	/**
	 * An {@link android.view.View} to indicate that "sending" is in progress.
	 */
	private SmoothProgressBar mSendingIndicatorV;

	/**
	 * Initialize an {@link  EditorDialogFragment} for a tweet publish.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @param defaultText
	 * 		Default text. It could be {@code null} if no default message provided.
	 * @param isDefaultFixed
	 * 		{@code true} if the default text must be used at first position of message. It should be used only when {@code
	 * 		defaultText != null}.
	 *
	 * @return An instance of {@link EditorDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context, @Nullable String defaultText, boolean isDefaultFixed) {
		Bundle args = new Bundle();
		args.putString(EXTRAS_DEFAULT_TEXT, defaultText);
		args.putBoolean(EXTRAS_DEFAULT_FIXED, isDefaultFixed);
		args.putSerializable(EXTRAS_TWEET_ITEM, null);
		args.putSerializable(EXTRAS_COMMENT, null);
		args.putSerializable(EXTRAS_NOTICE, null);
		return (DialogFragment) Fragment.instantiate(context, EditorDialogFragment.class.getName(), args);
	}

	/**
	 * Initialize an {@link  EditorDialogFragment} for writing comment for a {@link com.osc4j.ds.comment.Comment}(reply)
	 * of a {@link com.osc4j.ds.tweet.TweetListItem}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @param tweetListItem
	 * 		{@link com.osc4j.ds.tweet.TweetListItem}.
	 * @return An instance of {@link EditorDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context,  TweetListItem tweetListItem ) {
		Bundle args = new Bundle();
		args.putString(EXTRAS_DEFAULT_TEXT, null);
		args.putBoolean(EXTRAS_DEFAULT_FIXED, false);
		args.putSerializable(EXTRAS_TWEET_ITEM, tweetListItem);
		args.putSerializable(EXTRAS_COMMENT, null);
		args.putSerializable(EXTRAS_NOTICE, null);
		return (DialogFragment) Fragment.instantiate(context, EditorDialogFragment.class.getName(), args);
	}


	/**
	 * Initialize an {@link  EditorDialogFragment} for writing comment for a {@link com.osc4j.ds.comment.Comment}(reply)
	 * of a {@link com.osc4j.ds.tweet.TweetListItem}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @param tweetListItem
	 * 		{@link com.osc4j.ds.tweet.TweetListItem}.
	 * @param comment
	 * 		The {@code comment} will be replied. Reply the {@code comment}.
	 *
	 * @return An instance of {@link EditorDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context, TweetListItem tweetListItem, @Nullable Comment comment) {
		Bundle args = new Bundle();
		args.putString(EXTRAS_DEFAULT_TEXT, comment != null ? context.getString(R.string.action_reply_comment) + " @" +
				comment.getCommentAuthor() + ": " : null);
		args.putBoolean(EXTRAS_DEFAULT_FIXED, comment != null);
		args.putSerializable(EXTRAS_TWEET_ITEM, tweetListItem);
		args.putSerializable(EXTRAS_COMMENT, comment);
		args.putSerializable(EXTRAS_NOTICE, null);
		return (DialogFragment) Fragment.instantiate(context, EditorDialogFragment.class.getName(), args);
	}

	/**
	 * Initialize an {@link  EditorDialogFragment} for writing comment for a {@link com.osc4j.ds.comment.Comment}(reply)
	 * of a {@link com.osc4j.ds.tweet.TweetListItem}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @param tweetListItem
	 * 		{@link com.osc4j.ds.tweet.TweetListItem}.
	 * @param notice
	 * 		The {@code notice} will be replied. Reply the {@code notice}.
	 *
	 * @return An instance of {@link EditorDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context, TweetListItem tweetListItem, Notice notice) {
		Bundle args = new Bundle();
		args.putString(EXTRAS_DEFAULT_TEXT, context.getString(R.string.action_reply_comment) + " @" +
				notice.getAuthor() + ": ");
		args.putBoolean(EXTRAS_DEFAULT_FIXED, true);
		args.putSerializable(EXTRAS_TWEET_ITEM, tweetListItem);
		args.putSerializable(EXTRAS_COMMENT, null);
		args.putSerializable(EXTRAS_NOTICE, notice);
		return (DialogFragment) Fragment.instantiate(context, EditorDialogFragment.class.getName(), args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Transparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ScreenSize sz = DeviceUtils.getScreenSize(getActivity().getApplication());
		view.findViewById(R.id.root_v).setLayoutParams(new FrameLayout.LayoutParams(sz.Width, sz.Height));

		mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
		mToolbar.inflateMenu(MENU);
		mToolbar.setTitle(getString(R.string.lbl_editor_title));
		mSendMi = mToolbar.getMenu().findItem(R.id.action_send);
		mSendMi.setOnMenuItemClickListener(this);
		mEditText = (EditText) view.findViewById(R.id.message_content_et);
		String defaultText = getDefaultText();
		if (!TextUtils.isEmpty(defaultText)) {
			mEditText.setText(defaultText);
		}

		mSendingIndicatorV = (SmoothProgressBar) view.findViewById(R.id.sending_pb);
		mSendingIndicatorV.setSmoothProgressDrawableBackgroundDrawable(
				SmoothProgressBarUtils.generateDrawableWithColors(getResources().getIntArray(
								R.array.pocket_background_colors),
						((SmoothProgressDrawable) mSendingIndicatorV.getIndeterminateDrawable()).getStrokeWidth()));
		mSendingIndicatorV.progressiveStart();


		TweetListItem item = getTweetItem();
		if (item != null) {//Reply
			view.findViewById(R.id.tweet_ll).setVisibility(View.VISIBLE);

			//Show original tweet information, photo of editor and content of tweet.
			NetworkImageView photoIv = (NetworkImageView) view.findViewById(R.id.portrait_tweet_iv);
			photoIv.setDefaultImageResId(R.drawable.ic_not_loaded);
			photoIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
			photoIv.setOnClickListener(new OnViewAnimatedClickedListener() {
				@Override
				public void onClick() {
					TweetListItem item = getTweetItem();
					EventBus.getDefault().post(new ShowUserInformationEvent(item.getAuthorId()));
				}
			});

			TextView tweetItemContentTv = (TextView) view.findViewById(R.id.tweet_content_et);
			com.osc.tweet.utils.Utils.showTweetListItem(App.Instance, tweetItemContentTv, item);

			//Show the comment of tweet which will be commented further.
			Comment comment = getComment();
			if (comment != null) {
				view.findViewById(R.id.comment_rl).setVisibility(View.VISIBLE);

				TextView commentTv = (TextView) view.findViewById(R.id.comment_tv);
				com.osc.tweet.utils.Utils.showComment(App.Instance, commentTv, comment);

				photoIv = (NetworkImageView) view.findViewById(R.id.portrait_comment_iv);
				photoIv.setDefaultImageResId(R.drawable.ic_not_loaded);
				photoIv.setImageUrl(comment.getCommentPortrait(), TaskHelper.getImageLoader());
				photoIv.setOnClickListener(new OnViewAnimatedClickedListener() {
					@Override
					public void onClick() {
						Comment comment = getComment();
						EventBus.getDefault().post(new ShowUserInformationEvent(comment.getCommentAuthorId()));
					}
				});
			}

			//Show notice of a comment of tweet which will be commented further.
			Notice notice = getNotice();
			if (notice != null) {
				view.findViewById(R.id.comment_rl).setVisibility(View.VISIBLE);

				TextView commentTv = (TextView) view.findViewById(R.id.comment_tv);
				com.osc.tweet.utils.Utils.showNotice(App.Instance, commentTv, notice);

				photoIv = (NetworkImageView) view.findViewById(R.id.portrait_comment_iv);
				photoIv.setDefaultImageResId(R.drawable.ic_not_loaded);
				photoIv.setImageUrl(notice.getPortrait(), TaskHelper.getImageLoader());
				photoIv.setOnClickListener(new OnViewAnimatedClickedListener() {
					@Override
					public void onClick() {
						Notice notice = getNotice();
						EventBus.getDefault().post(new ShowUserInformationEvent(notice.getAuthorId()));
					}
				});
			}

			mToolbar.setTitle(R.string.action_reply_comment);
		}
	}

	/**
	 * @return Default text. It could be {@code null} if no default message provided.
	 */
	private
	@Nullable
	String getDefaultText() {
		return getArguments().getString(EXTRAS_DEFAULT_TEXT);
	}

	/**
	 * @return {@code true} if the default text must be used at first position of message. It works only when {@code
	 * getDefaultText() != null}.
	 */
	private boolean isDefaultFixed() {
		return getArguments().getBoolean(EXTRAS_DEFAULT_FIXED);
	}


	/**
	 * @return The comment of tweet.
	 */
	private
	@Nullable
	Comment getComment() {
		if (getArguments().getSerializable(EXTRAS_COMMENT) instanceof Comment) {
			return (Comment) getArguments().getSerializable(EXTRAS_COMMENT);
		} else {
			return null;
		}
	}

	/**
	 * @return The notice of a comment of tweet.
	 */
	private
	@Nullable
	Notice getNotice() {
		if (getArguments().getSerializable(EXTRAS_NOTICE) instanceof Notice) {
			return (Notice) getArguments().getSerializable(EXTRAS_NOTICE);
		} else {
			return null;
		}
	}


	/**
	 * @return {@link com.osc4j.ds.tweet.TweetListItem} which will be written with a comment.
	 */
	private TweetListItem getTweetItem() {
		return (TweetListItem) getArguments().getSerializable(EXTRAS_TWEET_ITEM);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_send:
			sendMessage();
			break;
		}
		return true;
	}

	/**
	 * Send tweet.
	 */
	private void sendMessage() {
		String msg = mEditText.getText().toString();
		if (!TextUtils.isEmpty(msg)) {
			AsyncTaskCompat.executeParallel(new AsyncTask<String, Object, StatusResult>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mSendMi.setEnabled(false);
					mEditText.setEnabled(false);
					mSendingIndicatorV.setVisibility(View.VISIBLE);
				}

				@Override
				protected StatusResult doInBackground(String... params) {
					String msg = params[0];
					try {
						if (getTweetItem() == null) {
							return OscApi.tweetPub(App.Instance, Utils.encode(msg));
						} else {
							if (getComment() == null && getNotice() == null) {
								return OscApi.tweetCommentPub(App.Instance, getTweetItem(), Utils.encode(msg));
							} else {
								if (getComment() != null) {
									return OscApi.tweetReply(App.Instance, getTweetItem(), Utils.encode(msg),
											getComment());
								} else {
									return OscApi.tweetReply(App.Instance, Utils.encode(msg), getNotice());
								}
							}
						}
					} catch (IOException | OscTweetException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(StatusResult s) {
					super.onPostExecute(s);
					if (s != null && Integer.valueOf(s.getResult().getCode()) == com.osc4j.ds.common.Status.STATUS_OK) {
						if (isVisible() && mSendMi != null) {
							mSendMi.setEnabled(true);
							mEditText.setEnabled(true);
							mSendingIndicatorV.setVisibility(View.INVISIBLE);
							mSendingIndicatorV.progressiveStop();
						}
						EventBus.getDefault().post(new LoadEvent());
						EventBus.getDefault().post(new OperatingEvent(true));
						dismiss();
					} else {
						EventBus.getDefault().post(new OperatingEvent(false));
						mEditText.setEnabled(true);
					}
				}
			}, msg);
		} else {
			Utils.showShortToast(App.Instance, R.string.msg_message_input);
		}
	}
}
