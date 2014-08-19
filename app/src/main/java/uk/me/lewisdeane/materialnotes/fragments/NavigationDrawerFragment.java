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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 19/08/2014.
 */
public class NavigationDrawerFragment extends Fragment {

    private View mRootView;
    private ListView mListView;
    private NavigationDrawerCallbacks mCallbacks;
    public static ActionBarDrawerToggle mDrawerToggle;
    public static DrawerLayout mDrawerLayout;
    public static List<String> mDrawerItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        init();
        setListeners();

        return mRootView;
    }

    private void init() {
        mDrawerItems = Arrays.asList(getResources().getStringArray(R.array.navigation_items));

        mListView = (ListView) mRootView.findViewById(R.id.fragment_navigation_drawer_list);
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mDrawerItems));
    }

    private void setListeners(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallbacks.onNavigationItemSelected(mDrawerItems.get(i), mDrawerItems, i);
            }
        });
    }

    public void setUp(int _res, DrawerLayout _drawerLayout){

        mDrawerLayout = _drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.drawable.background_circle, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mCallbacks.onDrawerClosed();
                MainActivity.mFABFragment.mRootView.setClickable(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mCallbacks.onDrawerOpened();
                MainActivity.mFABFragment.mRootView.setClickable(false);
            }

            @Override
            public void onDrawerSlide(View drawerView, float offSet){
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
        public void onNavigationItemSelected(String item, List<String> items, int position);
        public void onDrawerOpened();
        public void onDrawerClosed();
    }
}
