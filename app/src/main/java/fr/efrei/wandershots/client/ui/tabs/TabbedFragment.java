package fr.efrei.wandershots.client.ui.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentTabbedBinding;


public class TabbedFragment extends Fragment {
    private static final int[] TAB_TITLES = new int[]{R.string.home_tab, R.string.walks_history_tab};
    private static final int[] TAB_ICONS = new int[]{R.drawable.round_home_24, R.drawable.round_access_time_filled_24};

    public static TabbedFragment newInstance() {
        return new TabbedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTabbedBinding binding = FragmentTabbedBinding.inflate(inflater, container, false);
        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 tabsPager = binding.tabsPager;
        tabsPager.setAdapter(new TabsPagerAdapter(getChildFragmentManager(), getLifecycle()));

        new TabLayoutMediator(tabLayout, tabsPager, (tab, position) -> {
            tab.setText(TAB_TITLES[position]);
            tab.setIcon(TAB_ICONS[position]);
        }).attach();

        return binding.getRoot();
    }
}