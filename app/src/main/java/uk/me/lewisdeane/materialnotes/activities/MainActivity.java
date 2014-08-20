package uk.me.lewisdeane.materialnotes.activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.ldialogs.CustomListDialog;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.fragments.ActionBarFragment;
import uk.me.lewisdeane.materialnotes.fragments.AddFragment;
import uk.me.lewisdeane.materialnotes.fragments.FABFragment;
import uk.me.lewisdeane.materialnotes.fragments.MainFragment;
import uk.me.lewisdeane.materialnotes.fragments.NavigationDrawerFragment;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    public static ActionBarFragment mActionBarFragment;
    public static MainFragment mMainFragment;
    public static AddFragment mAddFragment;
    public static FABFragment mFABFragment;
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    public static FrameLayout mContainer;
    public static RelativeLayout mMainContainer;

    public static int isInAdd = 0; // 0 normally, 1 for editting/add, 2 for viewing

    public static Context mContext;

    public static boolean DRAWER_OPEN = false;
    public static String PATH = ""; // This holds the current path of the parent note eg. University or University/Bath or University/Bath/Work
    public static String MODE = "Everything"; // Holds the string of the current mode from navigation drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        getActionBar().hide();

        mContext = this;

        mActionBarFragment = (ActionBarFragment) getFragmentManager().findFragmentById(R.id.fragment_action_bar);
        mMainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
        mAddFragment = (AddFragment) getFragmentManager().findFragmentById(R.id.fragment_add);
        mFABFragment = (FABFragment) getFragmentManager().findFragmentByTag("FAB");
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        mContainer = (FrameLayout) findViewById(R.id.container);
        mMainContainer = (RelativeLayout) findViewById(R.id.main_container);

        mNavigationDrawerFragment.setUp(
                R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void onDrawerOpened(){
        DRAWER_OPEN = false;
        mActionBarFragment.onDrawerOpened();
    }

    public void onDrawerClosed(){
        DRAWER_OPEN = true;
        mActionBarFragment.onDrawerClosed();
    }

    public void onNavigationItemSelected(String _item, List<String> _items, int _position){
        if(_position < 8 && !_item.equals(MODE)){
            MODE = _item;
            loadNotes();
        } else if(_position == 8){
            // Go to settings.
        } else if(_position == 9){
            // Go to info.
        }
        mNavigationDrawerFragment.mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public static void loadNotes(){
        clearNoteList();
        mMainFragment.mNoteAdapter.addAll(DatabaseHelper.getNotesFromDatabase());
        mMainFragment.applyListViewFeatures();
    }

    public static void clearNoteList(){
        mMainFragment.mNoteItems.clear();
    }

    @Override
    public void onResume(){
        super.onResume();
        mMainFragment.mNoteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed(){
        mActionBarFragment.goBack(true);
    }
}
