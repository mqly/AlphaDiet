package com.dragon.alphadiet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.dragon.alphadiet.R;
import com.dragon.alphadiet.entity.Recipe;
import com.dragon.alphadiet.utils.CacheUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dragon.alphadiet.utils.CacheUtil.getACache;

/**
 * Created by Administrator on 2017/3/23.
 */

public class CollectDetailActivity extends AppCompatActivity {
    @BindView(R.id.collect_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.collect_detail_image)
    ImageView collectImage;
    @BindView(R.id.collect_detail_name)
    TextView collectName;
    @BindView(R.id.collect_detail_collect)
    ImageView collectCollect;
    @BindView(R.id.collect_detail_material)
    TextView collectMaterial;
    @BindView(R.id.collect_detail_step)
    TextView collectStep;
    boolean isCollected = false;
    Recipe recipe;
    private UMShareListener umShareListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_detail);
        ButterKnife.bind(this);
        recipe = getIntent().getBundleExtra("data").getParcelable("recipe");
        initToolbar();
        setData(recipe);
        if (getACache().getAsObject(recipe.getRecipeId()) != null) {
            isCollected = true;
            collectCollect.setBackground(getResources().getDrawable(R.drawable.ic_collected));
        }
        initUMShare();

    }

    private void initUMShare() {
        umShareListener = new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //分享开始的回调
            }

            @Override
            public void onResult(SHARE_MEDIA platform) {
                Log.d("plat", "platform" + platform);

                Toast.makeText(CollectDetailActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                Toast.makeText(CollectDetailActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(CollectDetailActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
            }
        };
    }

    //收藏或者取消收藏点击事件
    @OnClick(R.id.collect_detail_collect)
    public void collect() {
        if (isCollected) {
            cacleCollect();
        } else {
            collectRecipe();
        }
    }

    //收藏recipe
    private void collectRecipe() {
        collectCollect.setBackground(getResources().getDrawable(R.drawable.ic_collected));
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
        collectCollect.setBackground(getResources().getDrawable(R.drawable.ic_collect));
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
        Glide.with(CollectDetailActivity.this).load(getResourceByName(recipe.getRecipeImage())).fitCenter().into(collectImage);
        collectName.setText(recipe.getRecipeName());
        collectMaterial.setText(recipe.getRecipeMaterial());
        collectStep.setText(recipe.getRecipeStep());
    }

    //通过文件名获取到drawable资源int  id
    public int getResourceByName(String imageName) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }

    private void initToolbar() {
        toolbar.setTitle(recipe.getRecipeName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        UMImage image = new UMImage(CollectDetailActivity.this, R.drawable.cj);
                        UMImage thumb =  new UMImage(CollectDetailActivity.this, R.drawable.ft);
                        image.setThumb(thumb);
                        new ShareAction(CollectDetailActivity.this).withText("hello")
                                .withMedia(image)
                                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SMS)
                                .setCallback(umShareListener)
                                .open();
                        break;
                }
                return true;
            }
        });
    }

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, CollectDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }
}
