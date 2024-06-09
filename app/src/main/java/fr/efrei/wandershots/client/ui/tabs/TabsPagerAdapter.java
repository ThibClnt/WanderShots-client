package fr.efrei.wandershots.client.ui.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fr.efrei.wandershots.client.ui.history.HistoryFragment;
import fr.efrei.wandershots.client.ui.home.HomeFragment;

/**
 * This class is the adapter for the tabs of the application.
 */
public class TabsPagerAdapter extends FragmentStateAdapter {

    public TabsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return HomeFragment.newInstance();
        }
        return HistoryFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
