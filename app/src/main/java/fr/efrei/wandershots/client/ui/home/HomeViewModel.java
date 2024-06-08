package fr.efrei.wandershots.client.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.entities.Place;
import fr.efrei.wandershots.client.entities.User;
import fr.efrei.wandershots.client.repositories.UserRepository;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<Double> totalDistanceLiveData = new MutableLiveData<>(0.0);
    public LiveData<Double> getTotalDistance() { return totalDistanceLiveData; }

    private final List<Place> trendingPlaces = Arrays.asList(
            new Place("Eiffel Tower", "https://cdn-imgix.headout.com/media/images/c90f7eb7a5825e6f5e57a5a62d05399c-25058-BestofParis-EiffelTower-Cruise-Louvre-002.jpg"),
            new Place("Louvre Museum", "https://static.actu.fr/uploads/2022/09/adobestock-303614313-editorial-use-only.jpeg"),
            new Place("Notre-Dame Cathedral", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Notre-Dame_de_Paris_2013-07-24.jpg/3348px-Mapcarta.jpg"),
            new Place("Palace of Versailles", "https://www.chateauversailles.fr/sites/default/files/styles/reseaux_sociaux/public/visuels_principaux/chateau-home.jpg?itok=ZicY5bTj")
    );

    public List<Place> getTrendingPlaces() {
        return trendingPlaces;
    }

    public HomeViewModel(Application application) throws IllegalStateException {
        super(application);
        UserRepository userRepository = UserRepository.getInstance();
        User user = CredentialsManager.getInstance(application).getCredentialsFromCache();

        if (user == null)
            throw new IllegalStateException("User must be logged in to access home view model");

        new Thread(() -> {
            double totalDistance = 0;
            try {
                totalDistance = userRepository.getUserDistance(user.getUserId());
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), "Failed to get user distance", e);
            }
            totalDistanceLiveData.postValue(totalDistance);
        }).start();
    }
}