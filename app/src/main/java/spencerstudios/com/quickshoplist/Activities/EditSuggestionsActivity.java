package spencerstudios.com.quickshoplist.Activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import spencerstudios.com.quickshoplist.R;
import spencerstudios.com.quickshoplist.Adapters.SuggestionsAdapter;

public class EditSuggestionsActivity extends AppCompatActivity {

    private SharedPreferences prefSugg;
    private SharedPreferences.Editor suggEditor;
    private ArrayList<String> savedItems;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_suggestions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        prefSugg = getSharedPreferences("key", MODE_PRIVATE);
        suggEditor = prefSugg.edit();

        savedItems = getSuggsList();

        lv = findViewById(R.id.item_list_view_suggestions);
        SuggestionsAdapter adapter = new SuggestionsAdapter(this, savedItems);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemClick(i);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewItemDialog();
            }
        });
    }

    private void itemClick(final int i) {

        final AlertDialog dialog = new AlertDialog.Builder(EditSuggestionsActivity.this).create();

        dialog.setTitle(savedItems.get(i));

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeSuggItem(i);
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private ArrayList<String> getSuggsList() {

        ArrayList<String> temp = new ArrayList<>();
        try {
            temp.clear();
            JSONArray jsonArray = new JSONArray(prefSugg.getString("sug_list", ""));
            for (int i = 0; i < jsonArray.length(); i++) {
                temp.add(jsonArray.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp;
    }

    private void removeSuggItem(int pos) {
        savedItems.remove(pos);
        JSONArray jsonArray = new JSONArray(savedItems);
        suggEditor.putString("sug_list", jsonArray.toString()).apply();
        refreshAdapter();
    }

    private void refreshAdapter() {
        lv.setAdapter(new SuggestionsAdapter(EditSuggestionsActivity.this, savedItems));
    }

    private void addNewItemDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View editListItem = inflater.inflate(R.layout.add_new_suggestion_dialog, null);
        final EditText et = editListItem.findViewById(R.id.et_new_suggestion);

        AlertDialog.Builder popup = new AlertDialog.Builder(EditSuggestionsActivity.this);
        popup.setView(editListItem);
        popup.setTitle("New Item");
        popup.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = et.getText().toString();
                if(item.length()>0){
                    commitNewItemToPrefs(item);
                }
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

    private void commitNewItemToPrefs(String item) {
        if (!savedItems.contains(item)){
            savedItems.add(0, item);
            JSONArray jsonArray = new JSONArray(savedItems);
            suggEditor.putString("sug_list", jsonArray.toString()).apply();
            refreshAdapter();
        }else{
            Toast.makeText(getApplicationContext(), "item already exists", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
