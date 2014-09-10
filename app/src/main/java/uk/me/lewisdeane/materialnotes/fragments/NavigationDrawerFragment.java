package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import uk.me.lewisdeane.lnavigationdrawer.NavigationItem;
import uk.me.lewisdeane.lnavigationdrawer.NavigationListView;
import uk.me.lewisdeane.materialnotes.R;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.AddMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.CURRENT_SELECTED_POSITION;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.NoteMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.PATH;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.getNoteMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.loadInfo;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.loadNotes;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.loadSettings;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mActionBarFragment;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mAddMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mFABFragment;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mMainFragment;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mNoteMode;

/**
 * Created by Lewis on 19/08/2014.
 */
public class NavigationDrawerFragment extends Fragment {

    private View mRootView;
    private NavigationListView mListView;
    private NavigationDrawerCallbacks mCallbacks;
    public static ActionBarDrawerToggle mDrawerToggle;
    public static DrawerLayout mDrawerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        init();
        return mRootView;
    }

    private void init() {
        // Set up the list view
        mListView = (NavigationListView) mRootView.findViewById(R.id.fragment_navigation_drawer_list);

        mListView.addNavigationItem(new NavigationItem(getString(R.string.navigation_item_1), R.drawable.ic_mask_folder, true));
        mListView.addNavigationItem(new NavigationItem(getString(R.string.navigation_item_2), R.drawable.ic_mask_upcoming));
        mListView.addNavigationItem(new NavigationItem(getString(R.string.navigation_item_3), R.drawable.ic_mask_delete));
        mListView.addNavigationItem(new NavigationItem(getString(R.string.navigation_item_4), R.drawable.ic_action_settings_grey));
        mListView.addNavigationItem(new NavigationItem(getString(R.string.navigation_item_5), R.drawable.ic_action_info_outline_grey));

        mListView.setSelectedColor("#4285F4");

        mListView.setNavigationItemClickListener(new NavigationListView.NavigationItemClickListener() {
            @Override
            public void onNavigationItemSelected(String item, ArrayList<NavigationItem> navigationItems, int i) {
                // Check if a different mode is selected, if so apply new properties.
                NoteMode noteMode = getNoteMode(i);

                if (mNoteMode != noteMode) {

                    mNoteMode = noteMode;
                    PATH = "/";
                    CURRENT_SELECTED_POSITION = i;
                    mAddMode = AddMode.NONE;

                    // When new item selected turn off search.
                    mActionBarFragment.mSearchBox.setText("");
                    mActionBarFragment.mActionBar1.setVisibility(View.VISIBLE);
                    mActionBarFragment.mActionBar2.setVisibility(View.GONE);

                    if (i < navigationItems.size() - 2) {
                        navigationItems.get(CURRENT_SELECTED_POSITION).setIsSelected(true);
                        loadNotes();
                        switch (i) {
                            case 0:
                                mFABFragment.mFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_add));
                                mFABFragment.mFAB.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                mFABFragment.mFAB.setVisibility(View.GONE);
                            case 2:
                                mFABFragment.mFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_clear));
                                mFABFragment.mFAB.setVisibility(View.VISIBLE);
                                break;
                        }
                    } else if (i == navigationItems.size() - 2) {
                        loadSettings();
                    } else if (i == navigationItems.size() - 1) {
                        loadInfo();
                    }

                    // Set items swipe able and highlight newly selected item.
                    mMainFragment.applyListViewFeatures();
                }

                // Close drawer and set up action bar.
                mActionBarFragment.setUp(null);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    public void setUp(DrawerLayout _drawerLayout) {
        mDrawerLayout = _drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.drawable.background_circle, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mCallbacks.onDrawerClosed();
                mFABFragment.mRootView.setClickable(true);
                mActionBarFragment.mSearch.setClickable(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mCallbacks.onDrawerOpened();
                mFABFragment.mRootView.setClickable(false);
                mActionBarFragment.mSearch.setClickable(false);
            }

            @Override
            public void onDrawerSlide(View drawerView, float offSet) {
                // Fade the things out as drawer slides.
                mFABFragment.mRootView.setAlpha(1 - offSet);
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass() + " must implement NavigationDrawerCallbacks.");
        }
    }

    public interface NavigationDrawerCallbacks {
        // Called when drawer opened or closed.
        public void onDrawerOpened();

        public void onDrawerClosed();
    }
}
