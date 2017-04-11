package com.dragon.alphadiet.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dragon.alphadiet.entity.Recipe;
import com.dragon.alphadiet.greendao.DaoMaster;
import com.dragon.alphadiet.greendao.DaoSession;
import com.dragon.alphadiet.greendao.RecipeDao;
import com.dragon.alphadiet.utils.CacheUtil;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.dragon.alphadiet.activity.MainActivity.clearCount;

/**
 * Created by Administrator on 2017/2/19.
 */

public class MyApplication extends Application {
    private static Context context;
    private static MyApplication instance;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private DaoMaster.DevOpenHelper mHelper;

    {
        Config.DEBUG = true;
//        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setQQZone("1106062562", "H5XMhX10XeDSGVeU");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UMShareAPI.get(this);
        context = getApplicationContext();
        instance = this;
        initDatabase();
        initLogger();
        //首次启动
        if (TextUtils.isEmpty(CacheUtil.getACache().getAsString("isFirst"))) {
            CacheUtil.getACache().put("isFirst", "no");
            CacheUtil.getACache().put("days", "1");
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
            String date = sDateFormat.format(new java.util.Date());
            CacheUtil.getACache().put("lastTime", date);
            CacheUtil.getACache().put("leastEat", "维生素");
            CacheUtil.getACache().put("collected", "");
            clearCount();
            String jsonString = loadRecipeJson();
            List<Recipe> recipeList = getAllRecipeList(jsonString);
            saveToDB(recipeList);
        }
    }

    private void initLogger() {
        Logger.init("AlphaDiet")                 // default PRETTYLOGGER or use just init()
                .methodCount(2)                 // default 2
                //                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);
    }

    //通过jsonarray解析City List
    public List<Recipe> getAllRecipeList(String jsonString) {
        List<Recipe> recipes = new ArrayList<Recipe>();
        recipes = JSON.parseArray(jsonString, Recipe.class);
        Logger.d(recipes.size());
        return recipes;
    }

    //reciple数据存入数据库
    public void saveToDB(List<Recipe> recipes) {
        RecipeDao dao = MyApplication.getInstance().getDaoSession().getRecipeDao();
        dao.insertInTx(recipes);
    }

    //读取assets的json数据为string
    private String loadRecipeJson() {
        try {
            InputStream is = getAssets().open("recipe.json");
            byte[] buffer = new byte[2048];
            int readBytes = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while ((readBytes = is.read(buffer)) > 0) {
                stringBuilder.append(new String(buffer, 0, readBytes));
            }
            //            Logger.d(stringBuilder.toString());
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Context getContext() {
        return context;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 设置greenDao
     */
    private void initDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "alpha-db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}


