package com.dragon.alphadiet.adapter;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragon.alphadiet.R;
import com.dragon.alphadiet.application.MyApplication;
import com.dragon.alphadiet.entity.Diet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dragon.alphadiet.utils.CacheUtil.getACache;

/**
 * Created by Administrator on 2017/2/19.
 */

public class DietListAdapter extends RecyclerView.Adapter<DietListAdapter.MyViewHolder> implements View.OnClickListener {
    private List<Diet> diets;
    private Context context;
    private CoordinatorLayout colayout;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public DietListAdapter(Context context, List<Diet> diets, CoordinatorLayout colayout) {
        this.diets = diets;
        this.context = context;
        this.colayout = colayout;
    }

    public void setList(List<Diet> diets) {
        this.diets = diets;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diet, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Diet diet = diets.get(position);
        holder.nameTextView.setText(diet.getDietName());
        holder.typeTextView.setText(diet.getDietType());
        holder.weightTextView.setText(diet.getDietWeight() + "g");
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    reduceSubstance(diets.get(position).getDietType());
                    MyApplication.getInstance().getDaoSession().getDietDao().delete(diet);
                    diets.remove(position);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    Snackbar.make(colayout, "删除失败", Snackbar.LENGTH_SHORT).show();
                }
                Snackbar.make(colayout, "删除成功", Snackbar.LENGTH_SHORT).show();

            }
        });
        holder.itemView.setTag(diet);
    }

    //计数每日吃入的每种营养的次数
    public void reduceSubstance(String substance) {
        switch (substance) {
            case "碳水化合物":
                int cs = Integer.parseInt(getACache().getAsString("csCount")) - 1;
                getACache().put("csCount", cs + "");
                break;
            case "油脂":
                int yz = Integer.parseInt(getACache().getAsString("yzCount")) - 1;
                getACache().put("yzCount", yz + "");
                break;
            case "蛋白质":
                int db = Integer.parseInt(getACache().getAsString("dbCount")) - 1;
                getACache().put("dbCount", db + "");
                break;
            case "维生素":
                int ws = Integer.parseInt(getACache().getAsString("wsCount")) - 1;
                getACache().put("wsCount", ws + "");
                break;
            case "无机盐":
                int wj = Integer.parseInt(getACache().getAsString("wjCount")) - 1;
                getACache().put("wjCount", wj + "");
                break;
            case "水":
                int ss = Integer.parseInt(getACache().getAsString("ssCount")) - 1;
                getACache().put("ssCount", ss + "");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return diets.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Diet) v.getTag());
        }
    }

    //item点击回调接口
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Diet data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_diet_name)
        public TextView nameTextView;
        @BindView(R.id.item_diet_type)
        public TextView typeTextView;
        @BindView(R.id.item_diet_weight)
        public TextView weightTextView;
        @BindView(R.id.item_diet_delete)
        public ImageView deleteImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
