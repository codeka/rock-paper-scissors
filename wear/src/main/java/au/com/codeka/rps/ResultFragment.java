package au.com.codeka.rps;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * This fragment shows the result of the game.
 */
public class ResultFragment extends Fragment {
    private ImageView otherChoice;
    private ProgressBar otherLoading;
    private Handler handler;

    public static ResultFragment newInstance(String playerChoice) {
        Bundle args = new Bundle();
        args.putString("PlayerChoice", playerChoice);
        ResultFragment fragment = new ResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        handler = new Handler();

        View view = inflater.inflate(R.layout.fragment_result, container, false);
        setImage(((ImageView) view.findViewById(R.id.player_choice)), getArguments().getString("PlayerChoice"));
        otherChoice = (ImageView) view.findViewById(R.id.other_choice);
        otherLoading = (ProgressBar) view.findViewById(R.id.other_loading);
        return view;
    }

    public void setOtherChoice(final String choice) {
        handler.post(new Runnable() {
            @Override
            public void run() {
            setImage(otherChoice, choice);
            otherLoading.setVisibility(View.GONE);
            otherChoice.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setImage(ImageView iv, String choice) {
        if (choice.toLowerCase().equals("rock")) {
            iv.setImageResource(R.drawable.rock);
        } else if (choice.toLowerCase().equals("paper")) {
            iv.setImageResource(R.drawable.paper);
        } else if (choice.toLowerCase().equals("scissors")) {
            iv.setImageResource(R.drawable.scissors);
        } else {
            iv.setImageBitmap(null);
        }
    }
}
