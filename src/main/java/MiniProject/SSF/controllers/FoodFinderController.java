package MiniProject.SSF.controllers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.hash.Hashing;

import MiniProject.SSF.models.Food;
import MiniProject.SSF.repositories.FoodRepository;
import MiniProject.SSF.services.FoodFinderService;

@Controller

@RequestMapping(path = "/")
public class FoodFinderController {

    @Autowired
    private FoodFinderService ffSvc;

    @Autowired FoodRepository foodRepo;

    @GetMapping(path = "/loginpage")
    public String displayLoginPage()
    {
        return "loginpage";
    }

    @PostMapping(path = "/logintest")
    public String displayLoginTest(
        @RequestParam(name = "username") String username,
        @RequestParam(name = "password") String password,
        Model model)
    {   
        
        if(ffSvc.retrieveValue(username+"acct").equals(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString())){
            
            model.addAttribute("username", username);
            return "searchrecipe";
        }
        else
        
        return "loginfail";
    }

    @GetMapping(path = "/displayrecipe")
    public String displayRandomRecipe(
        @RequestParam(name = "userInput", defaultValue = "chinese") String input,
        @RequestParam(name = "maxCalorieInput", defaultValue = "800") String maxCalInput,
        @RequestParam(name = "username") String username,
        Model model
    )
    {

        model.addAttribute("username", username);
        model.addAttribute("inputz", input);
        model.addAttribute("maxCalzInput", maxCalInput);
        
        Food myFood = ffSvc.getFood(input, maxCalInput);
        model.addAttribute("thefood", myFood);

        //hidden form fields
        model.addAttribute("hiddenid",myFood.getId());
        model.addAttribute("hiddenrecipename",myFood.getRecipeName());
        model.addAttribute("hiddenimage",myFood.getImage());
        model.addAttribute("hiddencalories",myFood.getCalories());
        model.addAttribute("hiddenurl",myFood.getUrl());
        
        return "displayrecipe";
    }

    @PostMapping(path = "/savedpage")
    public String displaySavedPage(
        @RequestParam(name = "username") String username,
        @RequestParam(name = "id") Integer id,
        @RequestParam(name = "recipename") String recipename,
        @RequestParam(name = "image") String image,
        @RequestParam(name = "url") String url,
        @RequestParam(name = "calories") Integer calories,
        Model model
    )
    {   
        ffSvc.saveRecipe(username, id, recipename, image, url, calories);

        model.addAttribute("id",id);
        model.addAttribute("recipename",recipename);
        model.addAttribute("image",image);
        model.addAttribute("username", username);

        return "savedpage";
    }

    @GetMapping(path = "searchrecipe")
    public String displaySearchRecipe(
        @RequestParam(name = "username") String username,
        Model model
    )
    {
        model.addAttribute("username", username);
        return "searchrecipe";
    }

    @GetMapping(path = "/signuppage")
    public String displaySignUpPage(){
        return "signuppage";
    }

    @PostMapping(path = "/signupresultpage")
    public String displaySignUpSuccessPage(
        @RequestParam(name = "username") String usernameInput,
        @RequestParam(name = "password") String passwordInput,
        Model model
    )
    {
        if(foodRepo.retrieveIt(usernameInput)!="nothing"){
            return "signupfailurepage";
        }
        else
        {
            ffSvc.savingUsers(usernameInput, passwordInput);
            model.addAttribute("username", usernameInput);
            return "signupsuccesspage";
        }
    }

    @GetMapping(path = "/savedrecipes")
    public String savedrecipes(
        @RequestParam(name = "username") String username,
        Model model
    )
    {
            model.addAttribute("username", username);

            if(foodRepo.retrieveIt(username).equals("nothing")){
                return "emptysavedrecipes";
            }

            else {
                ArrayList<Food> foodlist = ffSvc.getSavedRecipes(username);

                model.addAttribute("foodlist", foodlist);
                return "savedrecipes";
        }
    }

}
