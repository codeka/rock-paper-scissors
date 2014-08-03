package au.com.codeka.rps;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * This Fragment displays the actual game where you choose your rock, paper or scissors.
 *
 */
public class GameFragment extends Fragment {
    private GridViewPager gridViewPager;
    private TextView timerText;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        gridViewPager = (GridViewPager) view.findViewById(R.id.pager);
        gridViewPager.setAdapter(new MyGridViewPagerAdapter());

        handler = new Handler();

        timerText = (TextView) view.findViewById(R.id.timer);
        setTimer(3);

        return view;
    }

    private void setTimer(final int value) {
        Animation timerAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.timer_text);
        timerAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                timerText.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        timerText.setVisibility(View.VISIBLE);
        timerText.setText(Integer.toString(value));
        timerText.setAnimation(timerAnimation);

        if (value > 1) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTimer(value - 1);
                }
            }, 2000);
        }
    }

    public String getCurrentChoice() {
        String[] choices = {"Rock", "Paper", "Scissors"};
        return choices[gridViewPager.getCurrentItem().y % 3];
    }

    private class MyGridViewPagerAdapter extends GridPagerAdapter {
        @Override
        public int getColumnCount(int arg0) {
            return 1;
        }

        @Override
        public int getRowCount() {
            return 100; // TODO: infinite?
        }

        @Override
        protected Object instantiateItem(ViewGroup container, int row, int col) {
            ImageView iv = new ImageView(getActivity(), null);
            if (row % 3 == 0) {
                iv.setImageResource(R.drawable.rock);
            } else if (row % 3 == 1) {
                iv.setImageResource(R.drawable.paper);
            } else {
                iv.setImageResource(R.drawable.scissors);
            }
            container.addView(iv);
            return iv;
        }

        @Override
        protected void destroyItem(ViewGroup container, int row, int col, Object view) {
            container.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
