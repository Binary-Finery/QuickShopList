package spencerstudios.com.quickshoplist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapterClass extends BaseAdapter {

    private ArrayList<String> itemList;
    private Context context;
    private LayoutInflater layOutInflater;

    public MyAdapterClass(MainActivity mainActivity, ArrayList<String> itemList) {
        this.itemList = itemList;
        this.context = mainActivity;
        layOutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = layOutInflater.inflate(R.layout.item_container, null);
        TextView item = (TextView)convertView.findViewById(R.id.text_view_itrem);
        item.setText(itemList.get(position));

        return convertView;
    }
}
