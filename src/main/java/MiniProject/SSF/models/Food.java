package MiniProject.SSF.models;

import jakarta.json.JsonObject;

public class Food {
    
    //instance variables
    private Integer id;

    private String recipeName;

    private String maxCalories;

    private String image;

    //getters and setters


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }




    public String getMaxCalories() {
        return this.maxCalories;
    }

    public void setMaxCalories(String maxCalories) {
        this.maxCalories = maxCalories;
    }
    
    
    

    public String getRecipeName() {
        return this.recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    //method to convert JSONObject to food object with the set instance variables
    public Food fromJSONToFood(JsonObject foodJSON){
        Food myFood = new Food();
        myFood.setRecipeName(foodJSON.getString("recipeName"));
        myFood.setImage(foodJSON.getString("image"));
        myFood.setId(foodJSON.getInt("id"));
        return myFood;
    }

}
