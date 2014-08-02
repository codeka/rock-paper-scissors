package au.com.codeka.rps;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * This Fragment displays the actual game where you choose your rock, paper or scissors.
 *
 */
public class GameFragment extends Fragment {
    private GridViewPager gridViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        gridViewPager = (GridViewPager) view.findViewById(R.id.pager);
        gridViewPager.setAdapter(new MyGridViewPagerAdapter());
        return view;
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
