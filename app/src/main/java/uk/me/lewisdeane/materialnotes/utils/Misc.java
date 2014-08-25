package uk.me.lewisdeane.materialnotes.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 23/08/2014.
 */
public class Misc {

    public static void toast(String _text){
        Toast.makeText(MainActivity.mContext, _text, Toast.LENGTH_SHORT).show();
    }

    public static void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) MainActivity.mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check if view has focus and if so hide keyboard
        View view = MainActivity.mAddFragment.getActivity().getCurrentFocus();
        if (view == null)
            return;

        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int getOccurences(String _text, String _lookingFor){
        int count = 0;

        for(int i = 0; i < _text.length(); i++){
            if((_text.charAt(i) + "").equals(_lookingFor))
                count++;
        }

        return count;
    }
}
