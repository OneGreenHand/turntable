package com.ogh.turntable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;


public class ZhuanPanActivity extends AppCompatActivity implements View.OnClickListener {
    private LuckPanLayout luckPanLayout;
    private RotatePan rotatePan;
    private ImageView go;

    public static void openActivity(Context context, int num) {
        Intent intent = new Intent(context, ZhuanPanActivity.class);
        intent.putExtra("num", num);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuan_pan);
        int num = getIntent().getIntExtra("num", 8);
        TextView github = findViewById(R.id.github);
        luckPanLayout = findViewById(R.id.luckpan_layout);
        rotatePan = findViewById(R.id.rotatePan);
        go = findViewById(R.id.go);
        go.setOnClickListener(this);
        github.setOnClickListener(this);
        rotatePan.setDatas(getNames(num),getImgs(num));//设置转盘数量
        luckPanLayout = (LuckPanLayout) findViewById(R.id.luckpan_layout);
        luckPanLayout.setAnimationEndListener(new LuckPanLayout.AnimationEndListener() {
            @Override
            public void endAnimation(int position) {
                Toast.makeText(ZhuanPanActivity.this, "Position = " + position, Toast.LENGTH_LONG).show();
            }
        });
//方式一
//        Glide.with(this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {//将网络图片转为bitmap
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                //TODO do something
//            }
//        });
//方式二
//    Glide.with(mContext)//将图片地址转化为bitmap,io线程
//                .asBitmap()
//                .load(s)
//                .submit(40, 40)//建议指定尺寸大小(单位为px)
//                .get();
    }

    private List<String> getNames(int num) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < num; i++)
            data.add("数据" + i);
        return data;
    }

    private List<Bitmap> getImgs(int num) {
        List<Bitmap> data = new ArrayList<>();
        for (int i = 0; i < num; i++)
            data.add(BitmapFactory.decodeResource(this.getResources(), new Random().nextBoolean() ? R.mipmap.sports : R.mipmap.adventure));
        return data;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go:
                luckPanLayout.rotate(-1, 100);
                break;
            case R.id.github:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://github.com/Nipuream/LuckPan"));
                    ZhuanPanActivity.this.startActivity(intent);
                } catch (Exception e) {
                }
                break;
        }

    }
}
