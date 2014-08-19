package uk.me.lewisdeane.materialnotes.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.objects.AddItem;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

/**
 * Created by Lewis on 19/08/2014.
 */
public class AddAdapter extends ArrayAdapter<AddItem> {

    private ArrayList<AddItem> mAddItems;

    public AddAdapter(Context _context, int _res, ArrayList<AddItem> _addItems){
        super(_context, _res, _addItems);

        this.mAddItems = _addItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_add, null);
        }

        final ImageView mImg = (ImageView) v.findViewById(R.id.item_add_img);
        final EditText mText = (EditText) v.findViewById(R.id.item_add_text);
        final ImageButton mClear = (ImageButton) v.findViewById(R.id.item_add_clear);

        final AddItem addItem = mAddItems.get(position);

        mImg.setImageDrawable(addItem.getImg());
        mClear.setVisibility(View.GONE);

        // Check for edit...
        mText.setHint(addItem.getHint());

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mText.setText("");
            }
        });

        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                addItem.setText(mText.getText().toString());

                mClear.setVisibility(mText.getText().toString().length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }
}
