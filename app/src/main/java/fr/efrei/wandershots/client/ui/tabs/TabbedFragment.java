package fr.efrei.wandershots.client.ui.tabs;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.databinding.FragmentTabbedBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.authentication.AuthenticationFragment;

/**
 * This fragment is responsible for the tabbed view of the application.
 */
public class TabbedFragment extends WandershotsFragment<FragmentTabbedBinding> {

    private static final int[] TAB_TITLES = new int[]{R.string.home_tab, R.string.walks_history_tab};
    private static final int[] TAB_ICONS = new int[]{R.drawable.round_home_24, R.drawable.round_access_time_filled_24};

    public static TabbedFragment newInstance() {
        return new TabbedFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup the tabs
        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 tabsPager = binding.tabsPager;

        tabsPager.setAdapter(new TabsPagerAdapter(getChildFragmentManager(), getLifecycle()));

        new TabLayoutMediator(tabLayout, tabsPager, (tab, position) -> {
            tab.setText(TAB_TITLES[position]);
            tab.setIcon(TAB_ICONS[position]);
        }).attach();

        // Handle back navigation
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CredentialsManager.getInstance(getContext()).signOut();
                navigateToFragment(AuthenticationFragment.newInstance(), false);
            }
        });
    }
}