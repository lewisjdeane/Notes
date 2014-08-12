package uk.me.lewisdeane.materialnotes.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.customviews.CustomEditText;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

public class DialogAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mItems = new ArrayList<String>();
    private Context mContext;
    private static int PREV_POSITION = -1;

    public DialogAdapter(Context context, int resource,
                       ArrayList<String> _items) {

        super(context, resource, _items);
        this.mContext = context;
        this.mItems = _items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_dialog, null);
        }

        EditText mText = (EditText) v.findViewById(R.id.item_dialog_text);
        mText.setText(mItems.get(position));

        /*
        When someone begins typing in an item, make another one visible.
        When that final item - 1 goes to 0 length remove final item.
         */

        return v;
    }

}
