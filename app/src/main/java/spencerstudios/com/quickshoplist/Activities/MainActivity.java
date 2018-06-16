package spencerstudios.com.quickshoplist.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import spencerstudios.com.quickshoplist.Adapters.ItemAdapter;
import spencerstudios.com.quickshoplist.R;

public class MainActivity extends AppCompatActivity {

    private ListView itemListView;
    private SharedPreferences itemData;
    private SharedPreferences.Editor itemDataEditor;
    private ArrayList<String> itemList, suggestionList;
    private LinearLayout mainView;
    private AutoCompleteTextView addItem;
    private ArrayAdapter<String> autoAdapter;

    private String tempItemHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_layout);

        ImageView clr = findViewById(R.id.clear_icon);
        addItem = findViewById(R.id.actv);
        mainView = findViewById(R.id.main_view);

        itemData = getSharedPreferences("key", MODE_PRIVATE);
        suggestionList = temp2();

        autoAdapter = new ArrayAdapter<>(this, R.layout.custom_text_view, suggestionList);
        addItem.setThreshold(1);
        addItem.setAdapter(autoAdapter);


        itemDataEditor = itemData.edit();
        itemListView = findViewById(R.id.item_list_view);
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
                    String it = addItem.getText().toString().trim();

                    if (!suggestionList.contains(it)) {
                        suggestionList.add(it);
                        autoAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.custom_text_view, suggestionList);
                        addItem.setAdapter(autoAdapter);
                        addSuggestionItem(it);
                    }
                    addItem(it);
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
            for (int i = 0; i < jsonArray.length(); i++) {
                itemList.add(jsonArray.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addItem(String s) {

        getItemsFromJsonArray();
        if (!itemList.contains(s)) {
            itemList.add(0, s);
            addItem.setText("");
            saveJsonArray();
        } else {
            Toast.makeText(getApplicationContext(), "this item is already listed", Toast.LENGTH_LONG).show();
        }
    }

    private void removeItem(int itemPosition) {

        getItemsFromJsonArray();

        tempItemHolder = itemList.get(itemPosition);

        itemList.remove(itemPosition);
        saveJsonArray();


        Snackbar snackBar = Snackbar.make(mainView, "\"" + tempItemHolder + "\" has been removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addItem(tempItemHolder);
                        Snackbar.make(mainView, "item returned to list!", Snackbar.LENGTH_LONG).show();
                    }
                });
        snackBar.show();
    }

    private void saveJsonArray() {
        JSONArray jsonArray = new JSONArray(itemList);
        itemDataEditor.putString("items", jsonArray.toString());
        itemDataEditor.apply();
        setAdapter();
    }

    private void setAdapter() {
        itemListView.setAdapter(new ItemAdapter(this, itemList));
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
        final EditText et = editListItem.findViewById(R.id.et);
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
        } else {
            if (item.getItemId() == R.id.suggestions) {
                startActivity(new Intent(MainActivity.this, EditSuggestionsActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private ArrayList<String> temp2() {
        ArrayList<String> l = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(itemData.getString("sug_list", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                l.add(jsonArray.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return l;
    }

    private void addSuggestionItem(String item) {
        ArrayList<String> modList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(itemData.getString("sug_list", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                modList.add(jsonArray.get(i).toString());
            }
            modList.add(item);
            JSONArray ja = new JSONArray(modList);
            modList.clear();
            itemDataEditor.putString("sug_list", ja.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        suggestionList.clear();
        suggestionList = temp2();
        autoAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.custom_text_view, suggestionList);
        addItem.setAdapter(autoAdapter);
    }
}
