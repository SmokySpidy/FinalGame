package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("DishUserController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/list")
    @ApiOperation("根据分类Id查询菜品")
    public Result<List<DishVO>> getByCategoryId(Long categoryId){
        String key="dish_"+categoryId;
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if(list!=null&&list.size()>0){
            return Result.success(list);
        }
        List<DishVO> dishes=dishService.getByCategoryId(categoryId);
        redisTemplate.opsForValue().set(key,dishes);
        return Result.success(dishes);
    }
}
