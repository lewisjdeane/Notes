package uk.me.lewisdeane.materialnotes.activities;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.fragments.ActionBarFragment;
import uk.me.lewisdeane.materialnotes.fragments.MainFragment;

public class MainActivity extends Activity implements CustomDialog.ClickListener{

    public static ActionBarFragment mActionBarFragment;
    public static MainFragment mMainFragment;
    public static FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        getActionBar().hide();

        mActionBarFragment = (ActionBarFragment) getFragmentManager().findFragmentById(R.id.fragment_action_bar);
        mMainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
        mContainer = (FrameLayout) findViewById(R.id.container);
    }


    public void onConfirmClick(){
    }

    public void onCancelClick(){
    }
}
