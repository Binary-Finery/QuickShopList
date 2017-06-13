package spencerstudios.com.quickshoplist;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView itemListView;
    private ImageView clr;
    private SharedPreferences itemData;
    private SharedPreferences.Editor itemDataEditor;
    private ArrayList<String> itemList;
    private EditText addItem;
    private LinearLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clr = (ImageView) findViewById(R.id.clear_icon);
        addItem = (EditText) findViewById(R.id.item);
        mainView = (LinearLayout) findViewById(R.id.main_view);
        itemData = getSharedPreferences("key", MODE_PRIVATE);
        itemDataEditor = itemData.edit();
        itemListView = (ListView) findViewById(R.id.item_list_view);
        itemList = new ArrayList();

        getItemsFromJsonArray();
        setAdapter();

        clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem.setText("");
            }
        });

        addItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && addItem.getText().length() > 0) {
                    addItem(addItem.getText().toString().trim());
                }
                return true;
            }
        });

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayItemActionDialog(position, itemList.get(position), 0);
            }
        });
    }

    private void getItemsFromJsonArray() {
        try {
            itemList.clear();
            JSONArray jsonArray = new JSONArray(itemData.getString("items", ""));
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    itemList.add(jsonArray.get(i).toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addItem(String s) {
        getItemsFromJsonArray();
        itemList.add(0, s);
        addItem.setText("");
        saveJsonArray();
    }

    private void removeItem(int itemPosition) {
        getItemsFromJsonArray();
        Snackbar.make(mainView, "\"" + itemList.get(itemPosition) + "\" has been removed from the list", Snackbar.LENGTH_LONG).show();
        itemList.remove(itemPosition);
        saveJsonArray();
    }

    private void saveJsonArray() {
        JSONArray jsonArray = new JSONArray(itemList);
        itemDataEditor.putString("items", jsonArray.toString());
        itemDataEditor.apply();
        setAdapter();
    }

    private void setAdapter() {
        itemListView.setAdapter(new MyAdapterClass(this, itemList));
    }

    private void clearList() {
        itemList.clear();
        saveJsonArray();
    }

    private void commitEditChanges(int p, String i) {
        getItemsFromJsonArray();
        itemList.set(p, i);
        saveJsonArray();
    }

    private void displayEditItemDialog(final int p, final String i) {

        LayoutInflater inflater = getLayoutInflater();
        View editListItem = inflater.inflate(R.layout.edit_item_dialog, null);
        final EditText et = (EditText) editListItem.findViewById(R.id.et);
        et.setText(i);
        et.setSelection(et.getText().length());
        AlertDialog.Builder popup = new AlertDialog.Builder(MainActivity.this);
        popup.setView(editListItem);
        popup.setTitle("Edit Item");
        popup.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                commitEditChanges(p, et.getText().toString());
            }
        });
        popup.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog d = popup.create();
        d.show();
    }


    private void displayItemActionDialog(final int itemPosition, String title, final int mode) {

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.setTitle(title);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, (mode == 0 ? "Remove" : "Yes, delete all items"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mode == 1)
                    clearList();
                else
                    removeItem(itemPosition);
            }
        });

        if (mode == 0) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    displayEditItemDialog(itemPosition, itemList.get(itemPosition));
                }
            });
        }
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_list) {
            getItemsFromJsonArray();
            if (itemList.size() > 0)
                displayItemActionDialog(-1, "Delete all items from the list?", 1);
        }
        return super.onOptionsItemSelected(item);
    }
}
