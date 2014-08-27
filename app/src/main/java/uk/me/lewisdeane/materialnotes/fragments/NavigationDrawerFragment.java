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
import uk.me.lewisdeane.materialnotes.activities.MainActivity;

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
                MainActivity.NoteMode noteMode = MainActivity.getNoteMode(i);

                if(MainActivity.NOTE_MODE != noteMode) {

                    MainActivity.NOTE_MODE = noteMode;
                    MainActivity.PATH = "/";
                    MainActivity.CURRENT_SELECTED_POSITION = i;
                    MainActivity.ADD_MODE = MainActivity.AddMode.NONE;

                    // When new item selected turn off search.
                    MainActivity.mActionBarFragment.mSearchBox.setText("");
                    MainActivity.mActionBarFragment.mActionBar1.setVisibility(View.VISIBLE);
                    MainActivity.mActionBarFragment.mActionBar2.setVisibility(View.GONE);

                    if(i < navigationItems.size() - 2 ){
                        navigationItems.get(MainActivity.CURRENT_SELECTED_POSITION).setIsSelected(true);
                        MainActivity.loadNotes();
                    } else if(i == navigationItems.size()-2){
                        MainActivity.loadSettings();
                    } else if(i == navigationItems.size()-1){
                        MainActivity.loadInfo();
                    }

                    // Set items swipe able and highlight newly selected item.
                    MainActivity.mMainFragment.applyListViewFeatures();
                }

                // Close drawer and set up action bar.
                MainActivity.mActionBarFragment.setUp(null);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
        });
    }

    public void setUp(DrawerLayout _drawerLayout){
        mDrawerLayout = _drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.drawable.background_circle, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mCallbacks.onDrawerClosed();
                MainActivity.mFABFragment.mRootView.setClickable(true);
                MainActivity.mActionBarFragment.mSearch.setClickable(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mCallbacks.onDrawerOpened();
                MainActivity.mFABFragment.mRootView.setClickable(false);
                MainActivity.mActionBarFragment.mSearch.setClickable(false);
            }

            @Override
            public void onDrawerSlide(View drawerView, float offSet){
                // Fade the things out as drawer slides.
                MainActivity.mFABFragment.mRootView.setAlpha(1-offSet);
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

    public interface NavigationDrawerCallbacks{
        // Called when drawer opened or closed.
        public void onDrawerOpened();
        public void onDrawerClosed();
    }
}
