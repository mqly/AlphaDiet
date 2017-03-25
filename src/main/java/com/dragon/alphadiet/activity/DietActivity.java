package com.dragon.alphadiet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dragon.alphadiet.R;
import com.dragon.alphadiet.application.MyApplication;
import com.dragon.alphadiet.entity.Diet;
import com.dragon.alphadiet.greendao.DietDao;
import com.dragon.alphadiet.utils.UUIDBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dragon.alphadiet.utils.CacheUtil.getACache;
import static java.lang.Integer.parseInt;

/**
 * Created by Administrator on 2017/3/22.
 */

public class DietActivity extends AppCompatActivity {
    @BindView(R.id.diet_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.diet_name)
    EditText dietName;
    @BindView(R.id.diet_weight)
    EditText dietWeight;
    @BindView(R.id.diet_type)
    Spinner dietType;
    String selectedType;
    String name;
    int weight;
    String type;
    String date;
    boolean isNewDiet = true;
    boolean isChanged = false;
    Diet dietGloable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        ButterKnife.bind(this);
        initViews();
        if (!isNewDiet) {
            dietGloable = getIntent().getBundleExtra("data").getParcelable("diet");
            dietName.setText(dietGloable.getDietName());
            dietWeight.setText(dietGloable.getDietWeight() + "");
            switch (dietGloable.getDietType()) {
                case "碳水化合物":
                    dietType.setSelection(0);
                    break;
                case "油脂":
                    dietType.setSelection(1);
                    break;
                case "蛋白质":
                    dietType.setSelection(2);
                    break;
                case "维生素":
                    dietType.setSelection(3);
                    break;
                case "无机盐":
                    dietType.setSelection(4);
                    break;
                case "水":
                    dietType.setSelection(5);
                    break;
            }
        }
    }

    private void initViews() {
        isNewDiet = getIntent().getBooleanExtra("isNew", true);
        if (isNewDiet) {
            mToolbar.setTitle("添加饮食");
        } else {
            mToolbar.setTitle("编辑饮食");
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setListeners();

    }

    private void setListeners() {
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_finish:
                        if (checkDietComplete()) {
                            //                            Toast.makeText(DietActivity.this, "信息完整", Toast.LENGTH_SHORT).show();
                            if (isChanged) {
                                saveDiet();
                            }
                            Intent intent = new Intent();
                            //把返回数据存入Intent
                            intent.putExtra("isChanged", isChanged);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(DietActivity.this, "请输入完整饮食信息", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });

        dietType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] dietTypes = getResources().getStringArray(R.array.dietTypes);
                type = dietTypes[position];
                //  Toast.makeText(DietActivity.this, "你点击的是:" + dietTypes[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //校验输入信息
    private boolean checkDietComplete() {
        name = dietName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        if (TextUtils.isEmpty(dietWeight.getText().toString())) {
            return false;
        }
        weight = parseInt(dietWeight.getText().toString());
        if (weight <= 0) {
            return false;
        }
        if (TextUtils.isEmpty(type)) {
            return false;
        }
        if (!isNewDiet) {
            if (!name.equals(dietGloable.getDietName()) || weight != dietGloable.getDietWeight() || !type.equals(dietGloable.getDietType())) {
                isChanged = true;
            }
        } else {
            isChanged = true;
        }
        return true;
    }

    //保存到数据库
    private void saveDiet() {
        String date = getACache().getAsString("days");
        if (TextUtils.isEmpty(date)) {
            date = "1";
        }
        DietDao dao = MyApplication.getInstance().getDaoSession().getDietDao();
        if (isNewDiet) {
            isChanged = true;
            Diet diet = new Diet(UUIDBuilder.getUUID(), name, weight, type, date);
            dao.insert(diet);
            countSubstance(diet.getDietType());
        } else {
            if (isChanged) {
                Diet diet = new Diet(dietGloable.getDietId(), name, weight, type, date);
                dao.insertOrReplace(diet);
                countSubstance(diet.getDietType());
            }
        }

    }

    //计数每日吃入的每种营养的次数
    public void countSubstance(String substance) {
        switch (substance) {
            case "碳水化合物":
                int cs = Integer.parseInt(getACache().getAsString("csCount")) + 1;
                getACache().put("csCount", cs + "");
                break;
            case "油脂":
                int yz = Integer.parseInt(getACache().getAsString("yzCount")) + 1;
                getACache().put("yzCount", yz + "");
                break;
            case "蛋白质":
                int db = Integer.parseInt(getACache().getAsString("dbCount")) + 1;
                getACache().put("dbCount", db + "");
                break;
            case "维生素":
                int ws = Integer.parseInt(getACache().getAsString("wsCount")) + 1;
                getACache().put("wsCount", ws + "");
                break;
            case "无机盐":
                int wj = Integer.parseInt(getACache().getAsString("wjCount")) + 1;
                getACache().put("wjCount", wj + "");
                break;
            case "水":
                int ss = Integer.parseInt(getACache().getAsString("ssCount")) + 1;
                getACache().put("ssCount", ss + "");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, DietActivity.class);
        context.startActivity(intent);
    }


}
