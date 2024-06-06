package fr.efrei.wandershots.client.ui.history;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.efrei.wandershots.client.entities.Walk;
import fr.efrei.wandershots.client.repositories.WalkRepository;

public class HistoryViewModel extends ViewModel {

    private final WalkRepository walkRepository;
    private List<Walk> walks;

    public HistoryViewModel() {
        walkRepository = WalkRepository.getInstance();
        walks = new ArrayList<>();
        loadWalks();
    }
    private void loadWalks() {
        walks = walkRepository.getAllWalks();
    }
    public List<Walk> getWalks(){
        return walks;
    }

}