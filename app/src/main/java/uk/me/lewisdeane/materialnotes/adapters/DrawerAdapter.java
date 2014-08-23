package uk.me.lewisdeane.materialnotes.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.objects.DrawerItem;
import uk.me.lewisdeane.materialnotes.utils.Colours;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    ArrayList<DrawerItem> mDrawerItems = new ArrayList<DrawerItem>();
    Context mContext;

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
            v = vi.inflate(R.layout.item_drawer, null);
        }

        DrawerItem di = mDrawerItems.get(position);

        CustomTextView titleView = (CustomTextView) v.findViewById(R.id.item_drawer_title);
        ImageView imgView = (ImageView) v.findViewById(R.id.item_drawer_img);

        titleView.setTypeface(Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Medium.ttf"));

        if(di.getIsSelected())
            titleView.setTextColor(mContext.getResources().getColor(R.color.blue_primary));
        else
            titleView.setTextColor(mContext.getResources().getColor(R.color.darkish_grey));

        Drawable drawable = Colours.getColouredDrawable(mContext.getResources().getDrawable(di.getRes()), mContext.getResources().getColor(di.getIsSelected() ? R.color.blue_primary : R.color.darkish_grey));
        imgView.setImageDrawable(drawable);

        titleView.setText(di.getTitle());

        return v;
    }
}
