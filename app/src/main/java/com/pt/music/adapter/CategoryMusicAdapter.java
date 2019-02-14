package com.pt.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.pt.music.R;
import com.pt.music.config.GlobalValue;
import com.pt.music.object.CategoryMusic;

import java.util.List;

public class CategoryMusicAdapter extends BaseAdapter {
    private List<CategoryMusic> listCategoryMusics;
    private LayoutInflater layoutInflater;
    private AQuery listAq;

    public CategoryMusicAdapter(Context context, List<CategoryMusic> listCategoryMusics) {
        this.listCategoryMusics = listCategoryMusics;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listAq = new AQuery(context);
    }

    public int getCount() {
        return listCategoryMusics.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_category_music, null);
        }

        CategoryMusic item = listCategoryMusics.get(position);
        if (item != null) {
            AQuery aq = listAq.recycle(convertView);
            ((TextView) convertView.findViewById(R.id.lblCategotyMusicName)).setText(item.getTitle());
            ((TextView) convertView.findViewById(R.id.lblCategotyMusicName)).setSelected(true);
            aq.id(R.id.imgCategoryMusic).image(item.getImage(), true, true, 0, R.drawable.img_not_found,
                    GlobalValue.bmNoImageAvailable, AQuery.FADE_IN_NETWORK, 0);
        }

        return convertView;
    }
}
