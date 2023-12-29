package com.example.contactsbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.example.contactsbuddy.R;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView contactRv;

    private DbHelper dbHelper;

    private AdapterContact adapterContact;

//    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        actionBar = getSupportActionBar();

        // init db
        dbHelper = new DbHelper(this);

        // initialization
        fab = findViewById(R.id.fab);
        contactRv = findViewById(R.id.contactRv);

        contactRv.setHasFixedSize(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add Listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to new activity to add contact
                Intent intent = new Intent(MainActivity.this, AddEditContact.class);
                intent.putExtra("isEditMode",false);
                startActivity(intent);
            }
        });
        loadData();
    }

    private void loadData() {
        adapterContact = new AdapterContact(this,dbHelper.getAllData());
        contactRv.setAdapter(adapterContact);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_top_menu, menu);

        MenuItem item = menu.findItem(R.id.searchContact);
        if (item != null) {
            View actionView = item.getActionView();
            if (actionView instanceof SearchView) {
                SearchView searchView = (SearchView) actionView;
                searchView.setMaxWidth(Integer.MAX_VALUE);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchContact(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchContact(newText);
                        return true;
                    }
                });

                return true;
            } else {
                Log.e("SearchView", "Action view is not a SearchView");
            }
        } else {
            Log.e("MenuItem", "MenuItem with ID R.id.searchContact not found");
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void searchContact(String query) {
        adapterContact = new AdapterContact(this,dbHelper.getSearchContact(query));
        contactRv.setAdapter(adapterContact);
    }

    // Example of handling item selection in the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
////        return super.onOptionsItemSelected(item);
//
//        switch (item.getItemId()){
//            case R.id.deleteAllContact:
//                dbHelper.deleteAllContact();
//                onResume();
//                break;
//        }
//        return true;
//    }
}