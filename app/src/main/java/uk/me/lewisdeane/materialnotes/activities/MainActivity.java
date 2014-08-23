package uk.me.lewisdeane.materialnotes.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.fragments.ActionBarFragment;
import uk.me.lewisdeane.materialnotes.fragments.AddFragment;
import uk.me.lewisdeane.materialnotes.fragments.FABFragment;
import uk.me.lewisdeane.materialnotes.fragments.MainFragment;
import uk.me.lewisdeane.materialnotes.fragments.NavigationDrawerFragment;
import uk.me.lewisdeane.materialnotes.objects.DrawerItem;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Animations;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    public static ActionBarFragment mActionBarFragment;
    public static MainFragment mMainFragment;
    public static AddFragment mAddFragment;
    public static FABFragment mFABFragment;
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    public static FrameLayout mContainer;
    public static RelativeLayout mMainContainer;

    public static Context mContext;

    public static boolean DRAWER_OPEN = false, isFABHidden = false;
    public static enum AddMode {NONE, ADD, VIEW};
    public static String PATH = "/";
    public static String MODE = "Everything";
    public static int CURRENT_SELECTED_POSITION = 0;

    public static AddMode ADD_MODE = AddMode.NONE;

    public static FragmentManager mFragmentManager;

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

        MainActivity.mActionBarFragment.setUp(null);
    }

    public void onDrawerOpened(){
        DRAWER_OPEN = false;
        mActionBarFragment.onDrawerOpened();
        MainActivity.mFABFragment.mRootView.setClickable(true);
    }

    public void onDrawerClosed(){
        DRAWER_OPEN = true;
        mActionBarFragment.onDrawerClosed();
        MainActivity.mFABFragment.mRootView.setClickable(false);
    }

    public void onNavigationItemSelected(String _item, ArrayList<DrawerItem> _items, int _position){
        if(!_item.equals(MODE)){
            MODE = _item;
            PATH = "/";

            CURRENT_SELECTED_POSITION = _position;
            ADD_MODE = AddMode.NONE;

            for(DrawerItem di : _items)
                di.setIsSelected(false);

            if(_position < _items.size() - 2 ){
                _items.get(CURRENT_SELECTED_POSITION).setIsSelected(true);
                loadNotes();
            } else if(_position == _items.size()-2){
                loadSettings();
            } else if(_position == _items.size()-1){
                loadInfo();
            }

            mMainFragment.applyListViewFeatures();
            mNavigationDrawerFragment.mDrawerAdapter.notifyDataSetChanged();
        }

        mActionBarFragment.setUp(null);
        mNavigationDrawerFragment.mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public static void loadNotes(){
        clearNoteList();
        mMainFragment.mNoteAdapter.addAll(DatabaseHelper.getNotesFromDatabase());
        mMainFragment.applyListViewFeatures();
    }

    private void loadSettings(){
    }

    private void loadInfo(){

    }

    public static void clearNoteList(){
        mMainFragment.mNoteItems.clear();
    }

    public static void openNote(NoteItem _noteItem){
        if (_noteItem.getIsFolder()) {
            PATH += _noteItem.getTitle() + "/";
        } else {
            ADD_MODE = AddMode.VIEW;
            mAddFragment.setUp(_noteItem);

            Animations.setAddAnimation(false, mFABFragment.mRootView);
            Animations.setListAnimation(false, mMainFragment.mList);
        }
        mActionBarFragment.setUp(_noteItem.getTitle());
        loadNotes();
    }

    public static void editNote(NoteItem _noteItem){
        ADD_MODE = AddMode.ADD;
        mAddFragment.setUp(_noteItem);

        Animations.setAddAnimation(false, mFABFragment.mRootView);
        Animations.setListAnimation(false, mMainFragment.mList);
        mActionBarFragment.setUp(_noteItem.getTitle());
    }

    public static void deleteNote(NoteItem _noteItem){
        _noteItem.deleteFromDatabase();
        mMainFragment.mNoteAdapter.remove(_noteItem);
        mMainFragment.mNoteAdapter.notifyDataSetChanged();
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
