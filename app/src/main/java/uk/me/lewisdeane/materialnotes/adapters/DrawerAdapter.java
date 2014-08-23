package uk.me.lewisdeane.materialnotes.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.objects.DrawerItem;
import uk.me.lewisdeane.materialnotes.utils.Colours;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    private ArrayList<DrawerItem> mDrawerItems = new ArrayList<DrawerItem>();
    private Context mContext;
    private CustomTextView mTitle;
    private ImageView mIcon;

    public DrawerAdapter(Context context, int resource,
                         ArrayList<DrawerItem> _drawerItems) {

        super(context, resource, _drawerItems);
        this.mContext = context;
        this.mDrawerItems = _drawerItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // Inflate correct layout.
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_drawer, null);

        // Get current item from list.
        DrawerItem drawerItem = mDrawerItems.get(position);

        // Initialise views.
        mTitle = (CustomTextView) v.findViewById(R.id.item_drawer_title);
        mIcon = (ImageView) v.findViewById(R.id.item_drawer_img);

        // Set data from list.
        mTitle.setText(drawerItem.getTitle());
        mIcon.setImageDrawable(Colours.getColouredDrawable(mContext.getResources().getDrawable(drawerItem.getRes()), mContext.getResources().getColor(drawerItem.getIsSelected() ? R.color.blue_primary : R.color.darkish_grey)));

        // Apply properties based on if drawer item is currently selected.
        mTitle.setTextColor(drawerItem.getIsSelected() ? Colours.getPrimaryColour() : Colours.getDarkColour());

        return v;
    }
}
