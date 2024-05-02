package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("SetMealUserController")
@RequestMapping("/user/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    public Result<List> getByCategoryId(Long categoryId){
    List<Setmeal> setmeals=setMealService.getByCategoryId(categoryId);
    return Result.success(setmeals);
    }
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询所有包含的菜品")
    public Result<List<DishItemVO>> getAllDishesById(@PathVariable Long setMealId){
        List<DishItemVO> dishItems=setMealService.getDishesById(setMealId);
        return Result.success(dishItems);
    }
}
