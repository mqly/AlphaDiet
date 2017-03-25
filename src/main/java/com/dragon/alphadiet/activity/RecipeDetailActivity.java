package com.dragon.alphadiet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.dragon.alphadiet.R;
import com.dragon.alphadiet.application.MyApplication;
import com.dragon.alphadiet.entity.Recipe;
import com.dragon.alphadiet.greendao.RecipeDao;
import com.dragon.alphadiet.utils.CacheUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dragon.alphadiet.utils.CacheUtil.getACache;

/**
 * Created by Administrator on 2017/3/23.
 */

public class RecipeDetailActivity extends AppCompatActivity {
    @BindView(R.id.recipe_toolbar)
    Toolbar toolbar;
    @BindString(R.string.menu_recommend)
    String recommendTitle;
    @BindView(R.id.recipe_image)
    ImageView recipeImage;
    @BindView(R.id.recipe_name)
    TextView recipeName;
    @BindView(R.id.recipe_collect)
    ImageView recipeCollect;
    @BindView(R.id.recipe_material)
    TextView recipeMaterial;
    @BindView(R.id.recipe_step)
    TextView recipeStep;
    boolean isCollected = false;
    Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        initToolbar();
        List<Recipe> recipeList = getRecipeFromDB();
        Random random = new Random();
        int position = random.nextInt(recipeList.size() - 1);
        recipe = recipeList.get(position);
        setData(recipe);
        if (getACache().getAsObject(recipe.getRecipeId()) != null) {
            isCollected = true;
            recipeCollect.setBackground(getResources().getDrawable(R.drawable.ic_collected));
        }
    }

    //收藏或者取消收藏点击事件
    @OnClick(R.id.recipe_collect)
    public void collect() {
        if (isCollected) {
            cacleCollect();
        } else {
            collectRecipe();
        }
    }

    //收藏recipe
    private void collectRecipe() {
        recipeCollect.setBackground(getResources().getDrawable(R.drawable.ic_collected));
        getACache().put(recipe.getRecipeId(), recipe);
        List<Recipe> recipes = getRecipeList();
        recipes.add(recipe);
        String json = JSON.toJSON(recipes).toString();
        CacheUtil.getACache().remove("collected");
        CacheUtil.getACache().put("collected", json);
        isCollected = true;
    }

    //从缓存中取已收藏的食谱
    public List<Recipe> getRecipeList() {
        String collects = getACache().getAsString("collected");
        List<Recipe> recipeList = new ArrayList<Recipe>();
        if (collects == null || TextUtils.isEmpty(collects)) {
            return recipeList;
        }
        recipeList = JSON.parseArray(collects, Recipe.class);
        return recipeList;

    }

    //取消收藏
    private void cacleCollect() {
        recipeCollect.setBackground(getResources().getDrawable(R.drawable.ic_collect));
        getACache().remove(recipe.getRecipeId());
        List<Recipe> recipes = getRecipeList();
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getRecipeId().equals(recipe.getRecipeId())) {
                recipes.remove(i);
                break;
            }
        }
        //        recipes.remove(recipe);
        String json = JSON.toJSON(recipes).toString();
        CacheUtil.getACache().remove("collected");
        CacheUtil.getACache().put("collected", json);
        isCollected = false;
    }

    //设置数据
    private void setData(Recipe recipe) {
        Glide.with(RecipeDetailActivity.this).load(getResourceByName(recipe.getRecipeImage())).fitCenter().into(recipeImage);
        recipeName.setText(recipe.getRecipeName());
        recipeMaterial.setText(recipe.getRecipeMaterial());
        recipeStep.setText(recipe.getRecipeStep());
    }

    //通过文件名获取到drawable资源int  id
    public int getResourceByName(String imageName) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }

    private void initToolbar() {
        toolbar.setTitle(recommendTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //从数据库获取今日推荐食谱
    public List<Recipe> getRecipeFromDB() {
        List<Recipe> recipeList = new ArrayList<Recipe>();
        String type = getACache().getAsString("leastEat");
        String recipeType = new String();
        switch (type) {
            case "csCount":
                recipeType = "碳水化合物";
                break;
            case "yzCount":
                recipeType = "油脂";
                break;
            case "dbCount":
                recipeType = "蛋白质";
                break;
            case "wsCount":
                recipeType = "维生素";
                break;
            case "wjCount":
                recipeType = "无机盐";
                break;
            case "ssCount":
                recipeType = "水";
                break;
        }
        try {
            RecipeDao dao = MyApplication.getInstance().getDaoSession().getRecipeDao();
            recipeList = dao.queryBuilder().where(RecipeDao.Properties.RecipeType.eq(recipeType)).build().list();
        } catch (Exception e) {
            Logger.e(e.getMessage(), e);
        }
        Logger.d("recipeList.size()" + recipeList.size());
        return recipeList;
    }

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        context.startActivity(intent);
    }
}
