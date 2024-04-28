package fr.efrei.wandershots.client.ui.home;

import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.List;

import fr.efrei.wandershots.client.data.Place;

public class HomeViewModel extends ViewModel {

    private final List<Place> trendingPlaces = Arrays.asList(
            new Place("Eiffel Tower", "https://cdn-imgix.headout.com/media/images/c90f7eb7a5825e6f5e57a5a62d05399c-25058-BestofParis-EiffelTower-Cruise-Louvre-002.jpg"),
            new Place("Louvre Museum", "https://static.actu.fr/uploads/2022/09/adobestock-303614313-editorial-use-only.jpeg"),
            new Place("Notre-Dame Cathedral", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Notre-Dame_de_Paris_2013-07-24.jpg/3348px-Mapcarta.jpg"),
            new Place("Palace of Versailles", "https://www.chateauversailles.fr/sites/default/files/styles/reseaux_sociaux/public/visuels_principaux/chateau-home.jpg?itok=ZicY5bTj")
    );

    public List<Place> getTrendingPlaces() {
        return trendingPlaces;
    }
}