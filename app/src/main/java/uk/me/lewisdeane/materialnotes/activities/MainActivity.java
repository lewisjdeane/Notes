package uk.me.lewisdeane.materialnotes.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import uk.me.lewisdeane.lnavigationdrawer.NavigationItem;
import uk.me.lewisdeane.lnavigationdrawer.NavigationListView;
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

public class MainActivity extends Activity implements NavigationListView.NavigationItemClickListener{

    // Fragments that are used in main activity.
    public static ActionBarFragment mActionBarFragment;
    public static MainFragment mMainFragment;
    public static AddFragment mAddFragment;
    public static FABFragment mFABFragment;
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    // Layouts defined in activity_main.xml
    public static FrameLayout mContainer;
    public static RelativeLayout mMainContainer;

    // Global context accessable from static contexts.
    public static Context mContext;

    // booleans storing state of elements.
    public static boolean DRAWER_OPEN = false, FAB_HIDDEN = false;

    // Enums defining different modes available.
    public static enum AddMode {NONE, ADD, VIEW};
    public static enum NoteMode { EVERYTHING, UPCOMING, ARCHIVE }

    // Current Path loading notes from.
    public static String PATH = "/";

    // Selected item in navigation drawer.
    public static int CURRENT_SELECTED_POSITION = 0;

    // Items storing current Note and Add Mode.
    public static AddMode ADD_MODE = AddMode.NONE;
    public static NoteMode NOTE_MODE = NoteMode.EVERYTHING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise variables.
        init();

        // Set up the custom action bar to show correct info.
        MainActivity.mActionBarFragment.setUp(null);
    }

    private void init() {
        // Hide action bar so we can use our own.
        getActionBar().hide();

        // Initialise context and reference fragments and views.
        mContext = this;

        mActionBarFragment = (ActionBarFragment) getFragmentManager().findFragmentById(R.id.fragment_action_bar);
        mMainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
        mAddFragment = (AddFragment) getFragmentManager().findFragmentById(R.id.fragment_add);
        mFABFragment = (FABFragment) getFragmentManager().findFragmentByTag("FAB");
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        mContainer = (FrameLayout) findViewById(R.id.container);
        mMainContainer = (RelativeLayout) findViewById(R.id.main_container);

        // Set up the navigation drawer.
        mNavigationDrawerFragment.setUp(
                R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    public void onDrawerOpened(){
        // Set properties applicable when drawer opened.
        DRAWER_OPEN = false;
        mActionBarFragment.onDrawerOpened();
        MainActivity.mFABFragment.mRootView.setClickable(true);
    }

    public void onDrawerClosed(){
        // Set properties applicable when drawer closed.
        DRAWER_OPEN = true;
        mActionBarFragment.onDrawerClosed();
        MainActivity.mFABFragment.mRootView.setClickable(false);
    }

    public void onNavigationItemSelected(String _item, ArrayList<NavigationItem> _items, int _position){
    }

    public void onNavigationItemSelected(NoteMode _item, ArrayList<DrawerItem> _items, int _position){

        if(!_item.equals(NOTE_MODE)){

            // If a new drawer item selected then load it.
            NOTE_MODE = _item;
            PATH = "/";
            CURRENT_SELECTED_POSITION = _position;
            ADD_MODE = AddMode.NONE;

            // Deselect all items.
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

            // Set items swipe able and highlight newly selected item.
            mMainFragment.applyListViewFeatures();
            //mNavigationDrawerFragment.mDrawerAdapter.notifyDataSetChanged();
        }

        // Close drawer and set up actio nbar.
        mActionBarFragment.setUp(null);
        mNavigationDrawerFragment.mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public static void loadNotes(){
        // Load notes from database based on current Note Mode.
        clearNoteList();
        mMainFragment.mNoteAdapter.addAll(DatabaseHelper.getNotesFromDatabase());
        mMainFragment.applyListViewFeatures();
    }

    private void loadSettings(){
        // Go to settings.
    }

    private void loadInfo(){
        // Go to info page.
    }

    public static void clearNoteList(){
        mMainFragment.mNoteItems.clear();
    }

    public static void openNote(NoteItem _noteItem){
        if (_noteItem.getIsFolder()) {
            // Append current path so that sub items of folder will be shown.
            PATH += _noteItem.getTitle() + "/";
        } else {
            // If it's a note open it in the add fragment and animate views.
            ADD_MODE = AddMode.VIEW;
            mAddFragment.setUp(_noteItem);

            Animations.setAddAnimation(false, mFABFragment.mRootView);
            Animations.setListAnimation(false, mMainFragment.mList);
        }

        // Set up action bar and load notes.
        mActionBarFragment.setUp(_noteItem.getTitle());
        loadNotes();
    }

    public static void editNote(NoteItem _noteItem){
        // Open note in editable mode and apply transformations needed to set up.
        ADD_MODE = AddMode.ADD;
        mAddFragment.setUp(_noteItem);

        Animations.setAddAnimation(false, mFABFragment.mRootView);
        Animations.setListAnimation(false, mMainFragment.mList);
        mActionBarFragment.setUp(_noteItem.getTitle());
    }

    public static void deleteNote(NoteItem _noteItem){
        // Delete the note from the database and sub items if applicable.
        _noteItem.deleteFromDatabase();
        mMainFragment.mNoteAdapter.remove(_noteItem);
        mMainFragment.mNoteAdapter.notifyDataSetChanged();
    }

    public static NoteMode getNoteMode(int _position){
        // Return NoteMode from position clicked.
        switch(_position){
            case 0:
                return NoteMode.EVERYTHING;
            case 1:
                return NoteMode.UPCOMING;
            case 2:
                return NoteMode.ARCHIVE;
            default:
                return NoteMode.EVERYTHING;
        }
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
