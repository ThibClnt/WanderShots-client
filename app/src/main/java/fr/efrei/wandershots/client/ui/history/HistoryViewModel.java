package fr.efrei.wandershots.client.ui.history;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.entities.User;
import fr.efrei.wandershots.client.entities.Walk;
import fr.efrei.wandershots.client.repositories.WalkRepository;

/**
 * This class is the ViewModel for the HistoryFragment.
 * It is responsible for managing the data of the history of the user.
 */
public class HistoryViewModel extends AndroidViewModel {

    private final WalkRepository walkRepository;

    private final MutableLiveData<List<Walk>> walks = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<Walk>> getWalks() { return walks; }

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        walkRepository = WalkRepository.getInstance();
        loadWalks(application.getApplicationContext());
    }

    /**
     * Load the walks of the user.
     */
    private void loadWalks(Context context) {
        new Thread(() -> {
            User user = CredentialsManager.getInstance(context).getCredentialsFromCache();
            if (user != null) {
                List<Walk> walksList = walkRepository.getAllUserWalks(user);
                walks.postValue(walksList);
            }
        }).start();
    }
}