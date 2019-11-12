package com.example.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.adapter.DrawerListAdapter;
import com.example.callback.DoCallBack;
import com.example.callback.GetResultBabyCallback;
import com.example.callback.TransitionFragmentCallback;
import com.example.callback.UpdateViewCallback;
import com.example.callback.UploadBabyCharacteristicCallback;
import com.example.callback.UploadImageRemoteCallback;
import com.example.callback.UploadSavedDataCallback;
import com.example.connection.API;
import com.example.connection.LuxandAPI;
import com.example.constant.Constant;
import com.example.model.BabyCharacteristic;
import com.example.model.NavItem;
import com.example.model.SavedInformation;
import com.example.ui.fragment.HistoryFragment;
import com.example.ui.fragment.ResultFragment;
import com.example.ui.fragment.SavedChildFragment;
import com.example.ui.fragment.UploadImageFragment;
import com.example.utils.SavedInformationRepository;
import com.example.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements UploadImageRemoteCallback,
        UploadSavedDataCallback,
        UploadBabyCharacteristicCallback,
        GetResultBabyCallback,
        TransitionFragmentCallback,
        DoCallBack {

    private API api;
    private SavedInformation savedInformation;
    private SavedInformationRepository repository;
    private BabyCharacteristic babyCharacteristic;
    private ResultFragment resultFragment;
    private UploadImageFragment uploadImageFragment;
    private HistoryFragment historyFragment;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.app_name);
        api = LuxandAPI.getApi();
        new InitConnection().execute();

        repository = new SavedInformationRepository(getApplicationContext());
        savedInformation = new SavedInformation();

        //slide bar initiate
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        initListItems();
        initHamburgerIcon();
        initActionbarIcon();

        //init fragment
        openFragmentUploadImage();
        verifyStoragePermissions(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void openFragmentUploadImage() {
        uploadImageFragment = new UploadImageFragment(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container_content, uploadImageFragment)
                .commit();
    }

    private void openHistoryFragment() {
        if (historyFragment == null) {
            historyFragment = new HistoryFragment(this);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_content, historyFragment)
                .commit();
    }

    @Override
    public void doCallback(Object param) {
        SavedInformation savedInformation = (SavedInformation) param;
        SavedChildFragment savedChildFragment = new SavedChildFragment(this, savedInformation);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container_content, savedChildFragment)
                .commit();
    }

    @Override
    public void openResultFragment() {
        resultFragment = new ResultFragment(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_content, resultFragment);
        transaction.commit();
    }

    @Override
    public void previousFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_content, uploadImageFragment)
                .commit();
    }

    @Override
    public void GetResultBaby() {
        new DoMakeBaby().execute(babyCharacteristic);
    }

    @Override
    public void uploadDataToRemote(int action, File file) {
        new MainActivity.UploadImage(action).execute(file);
    }

    @Override
    public void uploadPartner1Path(String path) {
        savedInformation.setPartner1(path);
    }

    @Override
    public void uploadPartner2Path(String path) {
        savedInformation.setPartner2(path);
    }

    @Override
    public void uploadChildPath(String path) {
        savedInformation.setChild(path);
    }

    @Override
    public void UploadBabyCharacteristic(BabyCharacteristic babyCharacteristic) {
        this.babyCharacteristic = babyCharacteristic;
    }

    //
    public class UploadImage extends AsyncTask<File, Void, Void> {
        private int action;

        public UploadImage(int action) {
            this.action = action;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Update File successfully", Toast.LENGTH_LONG).show();
            Log.e("Result", "Update File successfully");
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(File... files) {
            if (files != null && files.length > 0) {
                File file = files[0];
                api.uploadImage(file, action);
            }
            return null;
        }
    }

    private class InitConnection extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return api.initiateConnection();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            Log.e("Result", aVoid);
            Toast.makeText(getApplicationContext(),aVoid,Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);
        }
    }

    private class DoMakeBaby extends AsyncTask<BabyCharacteristic, Void, String> {
        @Override
        protected String doInBackground(BabyCharacteristic... babyCharacteristics) {
            String result = null;
            if (babyCharacteristics != null && babyCharacteristics.length > 0) {
                result = api.makeBaby(babyCharacteristics[0].getSex(), babyCharacteristics[0].getSkin());
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((UpdateViewCallback) resultFragment).doUpdateView(null);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals(Constant.RESULT_SUCCESS)) {
                String url = api.getURLChildImage();
                //save image to local

                String name = url.substring(url.lastIndexOf("/"), url.length());
                String pathChild = Utils.saveImage(api, name);
                savedInformation.setChild(pathChild);
                repository.insert(savedInformation.getPartner1()
                        , savedInformation.getPartner2()
                        , savedInformation.getChild());
                ((UpdateViewCallback) resultFragment).doUpdateView(url);
            } else {
                Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
                Log.e("Information Image", result);
            }
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        repository.closeConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        repository = new SavedInformationRepository(this);
    }

    //init slide bar

    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ArrayList<NavItem> navItems;

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerPane);
//        menu.findItem(R.id.).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void initListItems() {
        navItems = new ArrayList<>();
        navItems.add(new NavItem("Home",
                "Meetup destination",
                R.drawable.ic_home_black_24dp));
        //TODO: history option
        navItems.add(new NavItem("History",
                "History of prediction child",
                R.drawable.ic_history_black_24dp));
        navItems.add(new NavItem("About",
                "Get to know about us",
                R.drawable.ic_info_black_24dp));
    }

    private void initHamburgerIcon() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerPane = findViewById(R.id.drawerPane);
        mDrawerList = findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, navItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
    }

    private void initActionbarIcon() {
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.e("d", "on");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.e("d", "off");
                invalidateOptionsMenu();
            }
        };
    }

    //TODO: create fragment
    private void selectItemFromDrawer(int position) {
        switch (position) {
            case 0:
                previousFragment();
                break;
            case 1:
                openHistoryFragment();
                break;
            default:
                break;
        }
        mDrawerList.setItemChecked(position, true);
        setTitle(navItems.get(position).getmTitle());
        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }


}
