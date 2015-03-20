package com.osc.tweet.app.activities;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonSyntaxException;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.PeopleListAdapter;
import com.osc4j.OscApi;
import com.osc4j.ds.personal.PeopleList;
import com.osc4j.exceptions.OscTweetException;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Show list of all no-relation people.
 *
 * @author Xinyue Zhao
 */
public final class PeopleActivity extends OscActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_people;
	/**
	 * List container for showing all tweets.
	 */
	private RecyclerView mRv;
	/**
	 * Progress indicator.
	 */
	private SmoothProgressBar mSmoothProgressBar;
	/**
	 * Pull to load.
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;
	/**
	 * Some fun indicator when data not loaded.
	 */
	private View mNotLoadedIndicatorV;
	/**
	 * Indicator for empty feeds.
	 */
	private View mEmptyV;
	/**
	 * Adapter for {@link #mRv} to show all tweets.
	 */
	private PeopleListAdapter mAdp;
	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean mInProgress;



	/**
	 * Show single instance of {@link}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, PeopleActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);

		//Actionbar and navi-drawer.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = 	getSupportActionBar();
		actionBar.setTitle(R.string.lbl_settings);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDefaultDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.lbl_might_known_people);

		mEmptyV = findViewById(R.id.empty_ll);
		mNotLoadedIndicatorV = findViewById(R.id.not_loaded_ll);
		mSmoothProgressBar = (SmoothProgressBar) findViewById(R.id.loading_pb);

		mRv = (RecyclerView) findViewById(R.id.people_list_rv);
		mRv.setLayoutManager(new LinearLayoutManager(this));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new PeopleListAdapter(null));


		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2,
				R.color.color_pocket_3, R.color.color_pocket_4);
		mSwipeRefreshLayout.setRefreshing(true);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getPeople();
			}
		});

		getPeople();
	}

	private void getPeople() {
		if (!mInProgress) {
			AsyncTaskCompat.executeParallel(new AsyncTask<Object, PeopleList, PeopleList>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mInProgress = true;
				}

				@Override
				protected PeopleList doInBackground(Object... params) {
					try {
						return OscApi.noRelationPeople(App.Instance);
					} catch (IOException | OscTweetException | JsonSyntaxException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(PeopleList peopleList) {
					super.onPostExecute(peopleList);
					try {
						if (peopleList != null && peopleList.getStatus() == com.osc4j.ds.common.Status.STATUS_OK) {
							if (peopleList.getPeople().size() > 0) {
								mAdp.setData(peopleList.getPeople());
								mEmptyV.setVisibility(View.GONE);
								mNotLoadedIndicatorV.setVisibility(View.GONE);
							} else {
								mEmptyV.setVisibility(View.VISIBLE);
								mNotLoadedIndicatorV.setVisibility(View.GONE);
							}
							finishLoading();
						} else {
							mEmptyV.setVisibility(View.GONE);
							mNotLoadedIndicatorV.setVisibility(View.VISIBLE);
						}
					} catch (IllegalStateException e) {
						//Activity has been destroyed
					}
					mInProgress = false;
				}
			});
		}
	}

	/**
	 * Calls when loading data has been done.
	 */
	private void finishLoading() {
		mAdp.notifyDataSetChanged();
		mSmoothProgressBar.setVisibility(View.INVISIBLE);
		mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ActivityCompat.finishAfterTransition(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
