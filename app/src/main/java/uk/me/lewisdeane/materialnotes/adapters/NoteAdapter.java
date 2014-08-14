package uk.me.lewisdeane.materialnotes.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

public class NoteAdapter extends ArrayAdapter<NoteItem> {

    private ArrayList<NoteItem> mNoteItems = new ArrayList<NoteItem>();
    private Context mContext;

    public NoteAdapter(Context context, int resource,
                       ArrayList<NoteItem> _noteItems) {

        super(context, resource, _noteItems);
        this.mContext = context;
        this.mNoteItems = _noteItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_note, null);
        }

        CustomTextView mText = (CustomTextView) v.findViewById(R.id.item_note_title);
        mText.setText(mNoteItems.get(position).getTitle());

        CustomTextView mItem = (CustomTextView) v.findViewById(R.id.item_note_item);

        mItem.setVisibility(View.VISIBLE);
        if(mNoteItems.get(position).getItem().length() != 0 && !mNoteItems.get(position).getIsFolder())
            mItem.setText(mNoteItems.get(position).getItem());
        else
            mItem.setVisibility(View.GONE);

        Button mImg = (Button) v.findViewById(R.id.item_note_img);

        if(mNoteItems.get(position).getIsFolder()){
            mImg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_folder_aqua));
            mImg.setVisibility(View.VISIBLE);
        } else
            mImg.setVisibility(View.GONE);

        return v;
    }

}
