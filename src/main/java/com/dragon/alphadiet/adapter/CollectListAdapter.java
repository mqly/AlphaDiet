package com.dragon.alphadiet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dragon.alphadiet.R;
import com.dragon.alphadiet.entity.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/2/19.
 */

public class CollectListAdapter extends RecyclerView.Adapter<CollectListAdapter.MyViewHolder> implements View.OnClickListener {
    private List<Recipe> recipes;
    private Context context;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public CollectListAdapter(Context context, List<Recipe> recipes) {
        this.recipes = recipes;
        this.context = context;
    }

    public void setList(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_manage, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Recipe recipe = recipes.get(position);
        holder.nameTextView.setText(recipe.getRecipeName());
        holder.itemView.setTag(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Recipe) v.getTag());
        }
    }

    //item点击回调接口
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Recipe data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_city_manage_name)
        public TextView nameTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
