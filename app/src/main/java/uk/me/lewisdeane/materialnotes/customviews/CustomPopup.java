package uk.me.lewisdeane.materialnotes.customviews;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

/**
 * Created by Lewis on 21/08/2014.
 */
public class CustomPopup extends PopupMenu {

    private Context mContext;
    private View mRootView;

    public CustomPopup(Context _context, View _view){
        super(_context, _view);
        mContext = _context;
        mRootView = _view;
        show();
    }
}
