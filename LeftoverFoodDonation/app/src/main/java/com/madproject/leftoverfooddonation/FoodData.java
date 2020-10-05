package com.madproject.leftoverfooddonation;

public class FoodData {
    private String foodKey, foodName,foodQuantity,description, contactNumber,address;
    { foodKey="";}  //Assign default value
    public FoodData(){}

    //Used to create object while storing to firebase
    public FoodData(String foodName, String foodQuantity, String description, String contactNumber, String address){
        this.foodName=foodName;
        this.foodQuantity=foodQuantity;
        this.description=description;
        this.contactNumber=contactNumber;
        this.address=address;
    }

    //Used to retrieve data to show in cardview of food list
    public FoodData(String foodName, String foodQuantity, String contactNumber){
        this.foodName=foodName;
        this.foodQuantity=foodQuantity;
        this.contactNumber=contactNumber;
    }

    public String getFoodKey() {
        return foodKey;
    }

    public void setFoodKey(String foodKey) {
        this.foodKey = foodKey;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
