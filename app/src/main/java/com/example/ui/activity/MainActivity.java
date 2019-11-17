package com.example.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.example.callback.ConnectionPoolCallback;
import com.example.callback.DoCallBack;
import com.example.callback.GetResultBabyCallback;
import com.example.callback.ManageImageCallback;
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
import com.example.model.HistoryRecord;
import com.example.ui.fragment.HistoryFragment;
import com.example.ui.fragment.ResultFragment;
import com.example.ui.fragment.SavedChildFragment;
import com.example.ui.fragment.ShowImageFragment;
import com.example.ui.fragment.UpdateMultipleImageFragment;
import com.example.ui.fragment.UploadImageFragment;
import com.example.dao.HistoryRecordRepository;
import com.example.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity
        implements UploadImageRemoteCallback,
        UploadSavedDataCallback,
        UploadBabyCharacteristicCallback,
        GetResultBabyCallback,
        TransitionFragmentCallback,
        DoCallBack,
        ManageImageCallback,
        ConnectionPoolCallback {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private HistoryRecord historyRecord;
    private HistoryRecordRepository repository;
    private BabyCharacteristic babyCharacteristic;
    private ResultFragment resultFragment;
    private UploadImageFragment uploadImageFragment;
    private HistoryFragment historyFragment;
    private ShowImageFragment showImageFragment;
    private UpdateMultipleImageFragment updateMultipleImageFragment;
    private List<Uri> partner1Uris;
    private List<Uri> partner2Uris;
    private List<API> connectionPool;
    private List<String> urls;
    private boolean isMultipleImage = false;
    private AtomicInteger workCounter;

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
            case R.id.nav_add:
                Log.e("Add","show:"+showImageFragment.isVisible());
                break;
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
//        actionBar.setTitle(R.string.app_name);


        partner1Uris = new ArrayList<>();
        partner2Uris = new ArrayList<>();
        connectionPool = new ArrayList<>();
        urls = new ArrayList<>();
        workCounter = new AtomicInteger();

        repository = new HistoryRecordRepository(getApplicationContext());
        historyRecord = new HistoryRecord();
        showImageFragment = new ShowImageFragment(this);
        updateMultipleImageFragment = new UpdateMultipleImageFragment(this,
                connectionPool,
                partner1Uris,
                partner2Uris);


        connectionPool.add(new LuxandAPI());
        new InitConnection(connectionPool.get(0)).execute();

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
        HistoryRecord historyRecord = (HistoryRecord) param;
        SavedChildFragment savedChildFragment = new SavedChildFragment(this, historyRecord);
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
        new DoMakeBaby(connectionPool.get(0)).execute(babyCharacteristic);
    }

    @Override
    public void uploadDataToRemote(int action, File file) {
        new MainActivity.UploadImage(action, connectionPool.get(0)).execute(file);
    }

    @Override
    public void uploadPartner1Path(String path) {
        historyRecord.setPartner1(path);
    }

    @Override
    public void uploadPartner2Path(String path) {
        historyRecord.setPartner2(path);
    }

    @Override
    public void uploadChildPath(String path) {
        historyRecord.setChild(path);
    }

    @Override
    public void UploadBabyCharacteristic(BabyCharacteristic babyCharacteristic) {
        this.babyCharacteristic = babyCharacteristic;
    }

    @Override
    public void openManageImage(int action) {
        if(action == 1){
            showImageFragment.setUris(partner1Uris);
        }else{
            showImageFragment.setUris(partner2Uris);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_content,showImageFragment);
        transaction.commit();
    }

    @Override
    public void openPrevious() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_content,updateMultipleImageFragment);
        transaction.commit();
    }

    @Override
    public void initPoolConnection() {

        for (API api:connectionPool) {
            new InitConnection(api).execute();
        }
    }

    @Override
    public void uploadImage() {
        int index = 0;
        for(int i = 0; i < partner1Uris.size(); i ++){
            File file1 = new File(Utils.getImageFilePath(partner1Uris.get(i),this));
            for(int j = 0; j < partner2Uris.size(); j ++){
                File file2 = new File(Utils.getImageFilePath(partner2Uris.get(j),this));
                new UploadImage(1,connectionPool.get(index)).execute(file1);
                new UploadImage(2,connectionPool.get(index)).execute(file2);
                index++;
            }
        }
    }

    @Override
    public void showResult() {
        isMultipleImage = true;
        workCounter.set(connectionPool.size());
        for (int i = 0; i < connectionPool.size(); i++){
            new DoMakeBaby(connectionPool.get(i)).execute(babyCharacteristic);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if(manager.getBackStackEntryCount()>0){
            manager.popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    //
    public class UploadImage extends AsyncTask<File, Void, Void> {
        private int action;
        private API api;

        public UploadImage(int action, API api) {
            this.api = api;
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
        private API api;
        public InitConnection( API api) {
            this.api = api;
        }
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
        private API api;

        public DoMakeBaby(API api) {
            this.api = api;
        }

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
            if(!isMultipleImage){
                ((UpdateViewCallback) resultFragment).doUpdateView(null);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals(Constant.RESULT_SUCCESS)) {
                String url = api.getURLChildImage();
                //save image to local
                urls.add(url);

                String name = url.substring(url.lastIndexOf("/"), url.length());
                String pathChild = Utils.saveImage(api, name);
                historyRecord.setChild(pathChild);
                repository.insert(historyRecord.getPartner1()
                        , historyRecord.getPartner2()
                        , historyRecord.getChild());
                if(isMultipleImage){
                    Log.e("Count",workCounter.get()+"");
                    if(workCounter.get()==1){
                        showImageFragment.setUris(null);
                        showImageFragment.setLinks(urls);

                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.container_content,showImageFragment);
                        transaction.commit();
                    }
                }else{
                    ((UpdateViewCallback) resultFragment).doUpdateView(url);
                }
                workCounter.decrementAndGet();
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
        repository = new HistoryRecordRepository(this);
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        boolean isShowed = showImageFragment.isVisible();
        if(!isShowed){
            menu.findItem(R.id.nav_add).setVisible(isShowed);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void initListItems() {
        navItems = new ArrayList<>();
        navItems.add(new NavItem("Home",
                "Meetup destination",
                R.drawable.ic_home_black_24dp));
        navItems.add(new NavItem("Your friend Children",
                "Your friend Children",
                R.drawable.ic_home_black_24dp));
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

    private void selectItemFromDrawer(int position) {
        switch (position) {
            case 0:
                previousFragment();
                break;
            case 2:
                openHistoryFragment();
                break;
            case 1:
                openPrevious();
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
