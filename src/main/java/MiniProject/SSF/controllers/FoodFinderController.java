package MiniProject.SSF.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import MiniProject.SSF.models.Food;
import MiniProject.SSF.services.FoodFinderService;

@Controller

@RequestMapping(path = "/")
public class FoodFinderController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FoodFinderService ffSvc;

    

    @GetMapping(path = "/displayrecipe")
    public String displayRandomRecipe(
        @RequestParam(name = "userInput", defaultValue = "chicken") String input,
        @RequestParam(name = "maxCalorieInput", defaultValue = "800") String maxCalInput,
        Model model
    )
    {

        model.addAttribute("inputz", input);
        model.addAttribute("maxCalzInput", maxCalInput);
        
        List<Food> finalFoodList = ffSvc.getFood(input, maxCalInput);
        model.addAttribute("foodListz", finalFoodList);

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object greetings = ops.get("manila");
        model.addAttribute("hello", greetings.toString());
        // String hello = "adad";
        // model.addAttribute("hello", hello);

        ValueOperations<String, Object> valueOp = redisTemplate.opsForValue();
        valueOp.set("something", input);
        Object theThing = valueOp.get("something");
        model.addAttribute("somethingzxc", theThing.toString());

        ValueOperations<String, Object> op2 = redisTemplate.opsForValue();
        Object zxc =  valueOp.get("1");
        model.addAttribute("onezxc", zxc);

        String anotherThing = ffSvc.savingIt("how", "time");

        model.addAttribute("how", anotherThing);

        
        return "displayrecipe";


    }

    @PostMapping(path = "/savedpage")
    public String displaySavedPage(
        Model model
    )
    {
        //method to save the recipe from foodservice class
        return "savedpage";
    }
}
