package au.com.codeka.rps;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.codeka.rps.game.FindingOpponentState;

/**
 * This fragment is the one displayed when we're in the {@link FindingOpponentState}.
 */
public class FindingOpponentFragment extends Fragment {
    public FindingOpponentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finding_opponent, container, false);
    }
}
