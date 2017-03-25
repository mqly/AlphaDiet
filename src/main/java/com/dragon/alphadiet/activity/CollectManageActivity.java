package com.dragon.alphadiet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.dragon.alphadiet.R;
import com.dragon.alphadiet.adapter.CollectListAdapter;
import com.dragon.alphadiet.entity.Recipe;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dragon.alphadiet.utils.CacheUtil.getACache;

/**
 * Created by Administrator on 2017/3/23.
 */

public class CollectManageActivity extends AppCompatActivity {
    @BindView(R.id.collect_manage_toolbar)
    Toolbar toolbar;
    @BindString(R.string.menu_collection)
    String collectTitle;
    @BindView(R.id.collect_list)
    RecyclerView mRecyclerView;
    CollectListAdapter mCollectListAdapter;
    List<Recipe> mRecipeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_manage);
        ButterKnife.bind(this);
        initToolbar();
        mRecipeList = getRecipeList();
        Logger.d("mRecipeList.size():" + mRecipeList.size());
        mCollectListAdapter = new CollectListAdapter(this, mRecipeList);
        mCollectListAdapter.setOnItemClickListener(new CollectListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Recipe data) {
                //               Toast.makeText(CollectManageActivity.this, data.getRecipeName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CollectManageActivity.this, CollectDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("recipe", data);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCollectListAdapter);
    }

    public List<Recipe> getRecipeList() {
        String collects = getACache().getAsString("collected");
        List<Recipe> recipeList = new ArrayList<Recipe>();
        if (collects == null || TextUtils.isEmpty(collects)) {
            return recipeList;
        }
        recipeList = JSON.parseArray(collects, Recipe.class);
        return recipeList;

    }

    private void initToolbar() {
        toolbar.setTitle(collectTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, CollectManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecipeList = getRecipeList();
        mCollectListAdapter.setList(mRecipeList);
        mCollectListAdapter.notifyDataSetChanged();
    }
}
