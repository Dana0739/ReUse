package com.example.playground1.model;

public class ItemModel {

    private int id;

    private String name;

    private String description;

    private UserModel owner;

    private String pictureUri;

    private UserModel takenBy;

    public ItemModel() {
    }

    public ItemModel(int id, String name, String description, UserModel owner, String pictureUri) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.owner = owner;
        this.pictureUri = pictureUri;
    }

    public ItemModel setId(int id) {
        this.id = id;
        return this;
    }

    public ItemModel setName(String name) {
        this.name = name;
        return this;
    }

    public ItemModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public ItemModel setOwner(UserModel owner) {
        this.owner = owner;
        return this;
    }

    public ItemModel setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
        return this;
    }

    public ItemModel setTakenBy(UserModel takenBy) {
        this.takenBy = takenBy;
        return this;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UserModel getOwner() {
        return owner;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public UserModel getTakenBy() {
        return takenBy;
    }
}
