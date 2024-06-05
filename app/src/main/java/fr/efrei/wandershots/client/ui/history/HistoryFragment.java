package fr.efrei.wandershots.client.ui.history;

import fr.efrei.wandershots.client.databinding.FragmentHistoryBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;


public class HistoryFragment extends WandershotsFragment<FragmentHistoryBinding> {

    private HistoryViewModel historyViewModel;
    private FragmentHistoryBinding binding;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }
}