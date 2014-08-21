package uk.me.lewisdeane.materialnotes.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.objects.DrawerItem;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    ArrayList<DrawerItem> mDrawerItems = new ArrayList<DrawerItem>();
    Context mContext;
    ArrayList<Typeface> mTypefaces = new ArrayList<Typeface>();

    public DrawerAdapter(Context context, int resource,
                         ArrayList<DrawerItem> _drawerItems) {

        super(context, resource, _drawerItems);
        this.mContext = context;
        this.mDrawerItems = _drawerItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            if (position < 3)
                v = vi.inflate(R.layout.item_drawer, null);
            else
                v = vi.inflate(R.layout.item_drawer_small, null);
        }

        DrawerItem di = mDrawerItems.get(position);

        if (!di.getIsSmall()) {
            CustomTextView titleView = (CustomTextView) v.findViewById(R.id.item_drawer_title);
            TextView divider = (TextView) v.findViewById(R.id.item_drawer_bottom_divider);

            if (di.getIsSelected())
                titleView.setTypeface(Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Medium.ttf"));
            else
                titleView.setTypeface(Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Light.ttf"));

            titleView.setText(di.getTitle());
        } else {
            CustomTextView titleView = (CustomTextView) v.findViewById(R.id.item_drawer_small_title);
            ImageView imageView = (ImageView) v.findViewById(R.id.item_drawer_small_image);
            TextView topDivider = (TextView) v.findViewById(R.id.item_drawer_small_top_divider);

            if(position == 2 || position == 4)
                topDivider.setBackgroundColor(Color.parseColor("#F7F7F7"));

            titleView.setText(di.getTitle());
            imageView.setBackgroundDrawable(getContext().getResources().getDrawable(di.getRes()));
        }

        return v;
    }
}
