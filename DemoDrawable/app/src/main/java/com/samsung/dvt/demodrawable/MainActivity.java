package com.samsung.dvt.demodrawable;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageView imgBitMapDrawable, imgInsetDrawable, imgLayerListDrawable, imgRotateDrawable;
    private TextView tvShapeDrawable;
    private Button btnStateDrawble;
    private ImageButton btnTransition;
    private ImageView imgLevelList;
    private ImageView imgClip;
    private Button btnColorState;
    private int level = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO BitmapDrawable
        //ảnh tổ chức theo ma trận các điểm
        imgBitMapDrawable = findViewById(R.id.img_bitmap_drawable);
        imgBitMapDrawable.setImageResource(R.drawable.facebook);

        //TODO ClipDrawable
        //vẽ từ các hướng khác nhau tràn ra
        imgClip = findViewById(R.id.img_clip_drawable);
        final ClipDrawable clipDrawable = (ClipDrawable) imgClip.getDrawable();
        imgClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgClip.setImageLevel(clipDrawable.getLevel() + 1000);
            }
        });
        //TODO ShapeDrawble
        //TODO GradientDrawable
        //các hình và chế độ gradient
        tvShapeDrawable = findViewById(R.id.tv_shape_drawable);
        tvShapeDrawable.setBackgroundResource(R.drawable.demo_shape_drawable);

        //TODO StateDrawable
        //mô tả các trạng thái khác nhau
        btnStateDrawble = findViewById(R.id.btn_state_drawable);
        btnStateDrawble.setBackgroundResource(R.drawable.state_list_drawable);

        //TODO Level-List
        //nhiều level khác nhau
        imgLevelList = findViewById(R.id.img_level_list);
        final LevelListDrawable levelListDrawable = (LevelListDrawable) getDrawable(R.drawable.demo_level_list);
        imgLevelList.setImageDrawable(levelListDrawable);
        imgLevelList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (level > 3) {
                    level = 0;
                }
                imgLevelList.setImageLevel(level++);
            }
        });

        //TODO Layer-List
        //Nhiều lớp layer
        imgLayerListDrawable = findViewById(R.id.img_layer_list);
        imgLayerListDrawable.setImageResource(R.drawable.demo_layer_list2);

        //TODO InsetDrawable
        //Thay thuộc tính padding
        imgInsetDrawable = findViewById(R.id.img_inset_drawable);
        imgInsetDrawable.setBackgroundResource(R.drawable.inset_drawable);

        //TODO TransitionDrawable
        //chuyển đổi giữa 2 hình ảnh. ví dụ bóng đèn tối sáng
        btnTransition = (ImageButton) findViewById(R.id.btn_transition_drawable);
        btnTransition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionDrawable drawable = (TransitionDrawable) btnTransition.getDrawable();
                drawable.startTransition(5000);
            }
        });


        //TODO AnimationDrawable
        //hình gif
        ImageView image = (ImageView) findViewById(R.id.img_animation_drawable);
        AnimationDrawable frameAnimation = (AnimationDrawable) image.getDrawable(); // nạp ảnh
        frameAnimation.setOneShot(false);
        frameAnimation.start();

        //TODO ColorState List
        //tương tự như state drawable nhưng trong thư mục color
        btnColorState = findViewById(R.id.btn_color_state);
//        btnColorState.setColo(R.color.color_press);
        btnColorState.setTextColor(getResources().getColorStateList(R.color.color_press));

        //TODO RotateDrawable
        imgRotateDrawable = findViewById(R.id.img_rotate_drawable);
        RotateDrawable rotateDrawable = (RotateDrawable) imgRotateDrawable.getDrawable();
        imgRotateDrawable.setImageResource(R.drawable.rorate_drawable);

        //TODO NinePatchDrawable
        //TODO RotateDrawable
        //TODO ScaleDrawable
        //TODO DrawableContainer
        //tự tìm hiểu

    }
}
