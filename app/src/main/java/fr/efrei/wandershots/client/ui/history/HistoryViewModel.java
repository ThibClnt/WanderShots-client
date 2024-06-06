package fr.efrei.wandershots.client.ui.history;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.efrei.wandershots.client.entities.Walk;
import fr.efrei.wandershots.client.repositories.WalkRepository;

public class HistoryViewModel extends ViewModel {

    private final WalkRepository walkRepository;

    private final MutableLiveData<List<Walk>> walks = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<Walk>> getWalks() { return walks; }

    public HistoryViewModel() {
        walkRepository = WalkRepository.getInstance();
        loadWalks();
    }

    private void loadWalks() {
        new Thread(() -> {
            List<Walk> walksList = walkRepository.getAllWalks();
            walks.postValue(walksList);
        }).start();
    }
}