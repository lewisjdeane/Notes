package uk.me.lewisdeane.materialnotes.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.objects.AddItem;

/**
 * Created by Lewis on 19/08/2014.
 */
public class AddAdapter extends ArrayAdapter<AddItem> {

    private ArrayList<AddItem> mAddItems;

    public AddAdapter(Context _context, int _res, ArrayList<AddItem> _addItems) {
        super(_context, _res, _addItems);

        this.mAddItems = _addItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_add, null);
        }

        // Get current item in list.
        final AddItem addItem = mAddItems.get(position);

        // Initialise views.
        final EditText mTitle = (EditText) v.findViewById(R.id.item_add_text);
        final ImageView mIcon = (ImageView) v.findViewById(R.id.item_add_img);
        final ImageButton mClear = (ImageButton) v.findViewById(R.id.item_add_clear);

        mIcon.setImageDrawable(addItem.getImg());

        mTitle.setHint(mAddItems.get(position).getHint());
        mTitle.setText(mAddItems.get(position).getText());

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTitle.setText("");
            }
        });

        boolean isEditable = MainActivity.ADD_MODE == MainActivity.AddMode.ADD ? true : false;
        mTitle.setClickable(isEditable);
        mTitle.setFocusable(isEditable);
        mTitle.setFocusableInTouchMode(isEditable);
        mClear.setVisibility(isEditable && mTitle.getText().length() > 0 ? View.VISIBLE : View.GONE);

        // Add a text changed listener to listen for changes to the field to alter the visibility of clear.
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                addItem.setText(mTitle.getText().toString());
                mClear.setVisibility(mTitle.getText().toString().length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return v;
    }
}
