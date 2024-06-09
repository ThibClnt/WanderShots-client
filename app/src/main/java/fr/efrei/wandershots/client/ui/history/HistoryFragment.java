package fr.efrei.wandershots.client.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentHistoryBinding;
import fr.efrei.wandershots.client.entities.Walk;
import fr.efrei.wandershots.client.ui.WandershotsFragment;

/**
 * This fragment is responsible for displaying the history of the user.
 * It shows the walks of the user.
 */
public class HistoryFragment extends WandershotsFragment<FragmentHistoryBinding> {

    private HistoryViewModel viewModel;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        viewModel.getWalks().observe(getViewLifecycleOwner(), this::displayWalks);
    }

    /**
     * This function is called when the walks of the user are retrieved, to display them.
     */
    private void displayWalks(List<Walk> walks) {
        LinearLayout walksContainer = binding.walksContainer;
        walksContainer.removeAllViews();

        for (Walk walk : walks) {
            View walkView = createWalkView(walk);
            walksContainer.addView(walkView);
        }
    }

    /**
     * Create a list item for a walk.
     */
    private View createWalkView(Walk walk) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View walkView = inflater.inflate(R.layout.item_walk, null, false);

        TextView title = walkView.findViewById(R.id.title);
        TextView details = walkView.findViewById(R.id.details);

        title.setText(walk.getTitle());
        String walkDetails = String.format(Locale.getDefault(), "Start: %s\nDuration: %d min\nDistance: %.2f km",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(walk.getStartTime()),
                walk.getDuration() / 60000, // Convert ms to minutes
                walk.getDistance() / 1000); // Convert m to km
        details.setText(walkDetails);

        return walkView;
    }
}
