package com.example.tenanteye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    private final Context context;
    private final int[] images = {R.drawable.tenant_eye_logo, R.drawable.search, R.drawable.create_task, R.drawable.task_done, R.drawable.chat};
    private final int[] titles = {R.string.on_boarding_welcome_title, R.string.on_boarding_search_title, R.string.on_boarding_task_title, R.string.on_boarding_task_done_title, R.string.on_boarding_chat_title};
    private final int[] descriptions = {R.string.on_boarding_welcome_description, R.string.on_boarding_search_description, R.string.on_boarding_task_description, R.string.on_boarding_task_done_description, R.string.on_boarding_chat_description};

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slides_layout, container, false);
        ImageView imageView = view.findViewById(R.id.slider_layout_image_view);
        TextView textViewTitle = view.findViewById(R.id.slider_layout_title_text_view);
        TextView textViewDescription = view.findViewById(R.id.slider_layout_description_text_view);

        imageView.setImageResource(images[position]);
        textViewTitle.setText(titles[position]);
        textViewDescription.setText(descriptions[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
