package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("DishUserController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "用户端菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/list")
    @ApiOperation("根据分类Id查询菜品")
    @Cacheable(cacheNames = "dishCache",key = "#categoryId")
    public Result<List<DishVO>> getByCategoryId(Long categoryId){
        List<DishVO> dishes=dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }
}
