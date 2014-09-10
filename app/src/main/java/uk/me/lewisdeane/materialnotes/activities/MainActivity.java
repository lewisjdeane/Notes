package uk.me.lewisdeane.materialnotes.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.williammora.snackbar.Snackbar;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.fragments.ActionBarFragment;
import uk.me.lewisdeane.materialnotes.fragments.AddFragment;
import uk.me.lewisdeane.materialnotes.fragments.FABFragment;
import uk.me.lewisdeane.materialnotes.fragments.MainFragment;
import uk.me.lewisdeane.materialnotes.fragments.NavigationDrawerFragment;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Animations;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;
import uk.me.lewisdeane.materialnotes.utils.Misc;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Fragments that are used in main activity.
    public static ActionBarFragment mActionBarFragment;
    public static MainFragment mMainFragment;
    public static AddFragment mAddFragment;
    public static FABFragment mFABFragment;
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    // Layouts defined in activity_main.xml
    public static FrameLayout mContainer;
    public static RelativeLayout mMainContainer;

    // Global context accessible from static contexts.
    public static Context mContext;

    // booleans storing state of elements.
    public static boolean DRAWER_OPEN = false, FAB_HIDDEN = false;

    // Enums defining different modes available.
    public static enum AddMode {
        NONE, ADD, VIEW
    }

    public static enum NoteMode {EVERYTHING, UPCOMING, ARCHIVE}

    // Current Path loading notes from.
    public static String PATH = "/";

    // Selected item in navigation drawer.
    public static int CURRENT_SELECTED_POSITION = 0;

    // Items storing current Note and Add Mode.
    public static AddMode mAddMode = AddMode.NONE;
    public static NoteMode mNoteMode = NoteMode.EVERYTHING;

    public static ArrayList<NoteItem> mDeletedNotes = new ArrayList<NoteItem>();

    public static boolean mIsCurrentlyShowing = false;
    private CountDownTimer mTimer;
    public static Snackbar mSnackbar;

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
        mNavigationDrawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void onDrawerOpened() {
        // Set properties applicable when drawer opened.
        DRAWER_OPEN = true;
        mActionBarFragment.onDrawerOpened();
        MainActivity.mFABFragment.mRootView.setClickable(true);
    }

    public void onDrawerClosed() {
        // Set properties applicable when drawer closed.
        DRAWER_OPEN = false;
        mActionBarFragment.onDrawerClosed();
        MainActivity.mFABFragment.mRootView.setClickable(false);
    }

    public static void loadNotes() {
        // Load notes from database based on current Note Mode.
        clearNoteList();
        mMainFragment.mNoteAdapter.addAll(new DatabaseHelper(mContext).getNotesFromDatabase());
        mMainFragment.applyListViewFeatures();
    }

    public static void loadSearchResults(String _search) {
        clearNoteList();
        mMainFragment.mNoteAdapter.addAll(new DatabaseHelper(mContext).getNotesFromDatabase());
        mMainFragment.applyListViewFeatures();
    }

    public static void loadSettings() {
        // Go to settings.
    }

    public static void loadInfo() {
        // Go to info page.
    }

    public static void clearNoteList() {
        mMainFragment.mNoteItems.clear();
        mMainFragment.mNoteAdapter.notifyDataSetChanged();
    }

    public static void openNote(boolean _shouldEdit, NoteItem _noteItem) {
        // Called whenever open or edit clicked on a note/folder.

        if (_noteItem.getIsFolder() && !_shouldEdit) {
            // Append current path so that sub items of folder will be shown.
            if(MainActivity.FAB_HIDDEN)
                MainActivity.mFABFragment.show();
            PATH += _noteItem.getTitle().trim() + "/";
        } else {
            // If it's a note open it in the add fragment and animate views.
            mAddMode = _shouldEdit ? AddMode.ADD : AddMode.VIEW;
            openAdd(_shouldEdit, _noteItem);
            PATH = new DatabaseHelper(mContext).getPrevPath(_noteItem.getPath());
        }

        mActionBarFragment.mActionBar1.setVisibility(View.VISIBLE);
        mActionBarFragment.mActionBar2.setVisibility(View.GONE);
        mActionBarFragment.mSearchBox.setText("");

        // Set up action bar and load notes.
        mActionBarFragment.setUp(_noteItem.getTitle());
        loadNotes();
    }

    public static void deleteNote(NoteItem _noteItem) {
        // Delete the note from the database and sub items if applicable.
        _noteItem.deleteFromDatabase();
        mMainFragment.mNoteAdapter.remove(_noteItem);
        mMainFragment.mNoteAdapter.notifyDataSetChanged();
        Animations.animateFABAboveSnackbar(true);
    }

    public static NoteMode getNoteMode(int _position) {
        // Return NoteMode from position clicked.
        switch (_position) {
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

    public static void openAdd(boolean _shouldEdit, NoteItem _noteItem){
        mActionBarFragment.setUp("");
        mAddFragment.setUp(_shouldEdit, _noteItem);
        Animations.animateFAB(true, true, MainActivity.mContext.getResources().getDrawable(_shouldEdit ? R.drawable.ic_fab_done : R.drawable.ic_fab_edit));
        Animations.setListAnimation(true, MainActivity.mMainFragment.mList);
        mActionBarFragment.mSearch.setVisibility(View.GONE);
        if(mIsCurrentlyShowing){
            finishSnackbar(false);
            mSnackbar.dismiss();
        }
    }

    public static void closeAdd(){
        Misc.hideKeyboard();
        Animations.animateFAB(true, false, MainActivity.mContext.getResources().getDrawable(R.drawable.ic_fab_add));
        Animations.setListAnimation(false, MainActivity.mMainFragment.mList);
        mActionBarFragment.mSearch.setVisibility(View.VISIBLE);
    }

    public void createSnackbar(String _title){

        mIsCurrentlyShowing = true;

        mSnackbar = Snackbar.with(this) // context
                .text("Deleted " + _title + "!") // text to display
                .actionLabel("Undo") // action button label
                .actionColor(getResources().getColor(R.color.blue_primary))
                .customDuration(5000)
                .actionListener(new Snackbar.ActionClickListener() {
                    @Override
                    public void onActionClicked() {
                        restoreNotes();
                    }
                });
        mSnackbar.show(this);



        mTimer = new CountDownTimer(5000, 1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if(mIsCurrentlyShowing)
                    finishSnackbar(true);
            }
        };
        mTimer.start();
    }

    public static void finishSnackbar(boolean _shouldAnimate){
        MainActivity.mDeletedNotes.clear();
        mIsCurrentlyShowing = false;
        if(_shouldAnimate)
            Animations.animateFABAboveSnackbar(false);
    }

    private void restoreNotes(){
        for(int i = 0; i < MainActivity.mDeletedNotes.size(); i++){
            NoteItem noteItem = MainActivity.mDeletedNotes.get(i);
            new DatabaseHelper(mContext).addNoteToDatabase(false, noteItem);
        }
        MainActivity.loadNotes();
        mTimer.cancel();
        finishSnackbar(true);
    }

    public static void restoreNote(NoteItem _noteItem){
        _noteItem.addToDatabase();
        loadNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMainFragment.mNoteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        mActionBarFragment.goBack(true);
    }
}
