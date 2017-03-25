package com.dragon.alphadiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dragon.alphadiet.R;
import com.dragon.alphadiet.adapter.DietListAdapter;
import com.dragon.alphadiet.application.MyApplication;
import com.dragon.alphadiet.entity.Diet;
import com.dragon.alphadiet.greendao.DietDao;
import com.dragon.alphadiet.utils.CacheUtil;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dragon.alphadiet.utils.CacheUtil.getACache;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.diet_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.diet_colayout)
    CoordinatorLayout colayout;
    List<Diet> diets;
    DietListAdapter dietListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        initDate();
        initLeastEat();
        diets = getDietsFromDB();
        dietListAdapter = new DietListAdapter(this, diets, colayout);
        dietListAdapter.setOnItemClickListener(new DietListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Diet data) {
                Intent intent = new Intent(MainActivity.this, DietActivity.class);
                intent.putExtra("isNew", false);
                Bundle bundle = new Bundle();
                bundle.putParcelable("diet", data);
                intent.putExtra("data", bundle);
                startActivityForResult(intent, 1);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(dietListAdapter);
    }

    //初始化吃入最少的营养
    private void initLeastEat() {
        int cs = Integer.parseInt(getACache().getAsString("csCount"));
        int yz = Integer.parseInt(getACache().getAsString("yzCount"));
        int db = Integer.parseInt(getACache().getAsString("dbCount"));
        int ws = Integer.parseInt(getACache().getAsString("wsCount"));
        int wj = Integer.parseInt(getACache().getAsString("wjCount"));
        int ss = Integer.parseInt(getACache().getAsString("ssCount"));
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("csCount", cs);
        map.put("yzCount", yz);
        map.put("dbCount", db);
        map.put("wsCount", ws);
        map.put("wjCount", wj);
        map.put("ssCount", ss);
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue() - o2.getValue());
            }
        });
        Logger.d(list);
        CacheUtil.getACache().put("leastEat", list.get(0).getKey());
    }

    //若当前日期小于上次使用日期，日期被手动修改数据异常；若大于上次使用日期，当前日期设置为最近一次日期，days+1
    private void initDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = sDateFormat.format(new java.util.Date());
        int lastTime = parseInt(getACache().getAsString("lastTime"));
        int current = parseInt(date);
        if (current < lastTime) {
            Toast.makeText(this, "日期数据异常", Toast.LENGTH_SHORT).show();
        }
        if (current > lastTime) {
            getACache().put("lastTime", date);
            int currentDay = parseInt(CacheUtil.getACache().getAsString("days"));
            CacheUtil.getACache().put("days", currentDay + 1 + "");
            clearCount();
        }
        Logger.d(CacheUtil.getACache().getAsString("days"));

    }

    public static void clearCount() {
        getACache().put("csCount", "0");
        getACache().put("yzCount", "0");
        getACache().put("dbCount", "0");
        getACache().put("wsCount", "0");
        getACache().put("wjCount", "0");
        getACache().put("ssCount", "0");
    }

    //从数据库读取今日饮食
    private List<Diet> getDietsFromDB() {
        List<Diet> dietList = new ArrayList<Diet>();
        try {
            DietDao dao = MyApplication.getInstance().getDaoSession().getDietDao();
            dietList = dao.queryBuilder().where(DietDao.Properties.DietDate.eq(CacheUtil.getACache().getAsString("days"))).build().list();
        } catch (Exception e) {
            Toast.makeText(this, "暂无数据，请先添加", Toast.LENGTH_SHORT).show();
        }
        return dietList;
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DietActivity.class);
                intent.putExtra("isNew", true);
                startActivityForResult(intent, 1);
            }
        });
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_recommend:
                RecipeDetailActivity.activityStart(MainActivity.this);
                break;
            case R.id.nav_collection:
                CollectManageActivity.activityStart(MainActivity.this);
                break;
            case R.id.nav_menu_about:
                AboutActivity.activityStart(MainActivity.this);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //        Toast.makeText(this, "返回到主界面", Toast.LENGTH_SHORT).show();
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean isChanged = data.getBooleanExtra("isChanged", false);
            Logger.d(isChanged);
            if (isChanged) {
                diets = getDietsFromDB();
                dietListAdapter.setList(diets);
                Logger.d("当前列表长度" + diets.size());
                dietListAdapter.notifyDataSetChanged();
            }
        }
    }
}
