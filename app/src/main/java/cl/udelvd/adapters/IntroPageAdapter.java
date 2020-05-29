package cl.udelvd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import cl.udelvd.R;
import cl.udelvd.models.IntroItem;

public class IntroPageAdapter extends PagerAdapter {

    private Context context;
    private List<IntroItem> introItemList;
    private ImageView imgSlide;
    private TextView title;
    private TextView description;
    private View layoutScreen;
    private Animation itemAnimation;

    public IntroPageAdapter(Context context, List<IntroItem> introItemList) {
        this.context = context;
        this.introItemList = introItemList;
        itemAnimation = AnimationUtils.loadAnimation(context, R.anim.intro_item_animation);
    }


    @Override
    public int getCount() {
        return introItemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {


            layoutScreen = inflater.inflate(R.layout.intro_item, null);

            imgSlide = layoutScreen.findViewById(R.id.iv_intro);
            title = layoutScreen.findViewById(R.id.tv_intro_title);
            description = layoutScreen.findViewById(R.id.tv_intro_description);

            title.setText(introItemList.get(position).getTitle());
            description.setText(introItemList.get(position).getDescription());
            imgSlide.setImageResource(introItemList.get(position).getIdResource());

            container.addView(layoutScreen);

            layoutScreen.setTag("view" + position);
            return layoutScreen;
        }
        return super.instantiateItem(container, position);
    }
}
