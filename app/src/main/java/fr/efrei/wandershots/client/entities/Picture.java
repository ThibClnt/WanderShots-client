package fr.efrei.wandershots.client.entities;

public class Picture {

    private int pictureId;
    private int walkId;
    private String title;
    private double latitude;
    private double longitude;
    private byte[] image;

    public Picture() {
    }

    public Picture(int pictureId, int walkId, String title, double latitude, double longitude, byte[] image) {
        this.pictureId = pictureId;
        this.walkId = walkId;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public int getWalkId() {
        return walkId;
    }

    public void setWalkId(int walkId) {
        this.walkId = walkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
