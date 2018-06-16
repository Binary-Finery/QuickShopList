package spencerstudios.com.quickshoplist.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import spencerstudios.com.quickshoplist.Activities.MainActivity;
import spencerstudios.com.quickshoplist.R;

public class ItemAdapter extends BaseAdapter {

    private ArrayList<String> itemList;
    private LayoutInflater layOutInflater;

    public ItemAdapter(MainActivity mainActivity, ArrayList<String> itemList) {
        this.itemList = itemList;
        Context context = mainActivity;
        layOutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private static class ViewHolder{
        TextView item;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView==null) {

            viewHolder = new ViewHolder();
            convertView = layOutInflater.inflate(R.layout.item_container, null);
            viewHolder.item = (TextView) convertView.findViewById(R.id.text_view_itrem);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.item.setText(itemList.get(position));

        return convertView;
    }
}
