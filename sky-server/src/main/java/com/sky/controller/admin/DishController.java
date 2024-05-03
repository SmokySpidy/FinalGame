package com.sky.controller.admin;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("DishAdminController")
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品");
        dishService.save(dishDTO);
        String key="dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    @PostMapping("/status/{status}")
    @ApiOperation("起售或者禁售菜品")
    public Result StartOrStop(@PathVariable Integer status,Long id){
        DishDTO dishDTO=new DishDTO();
        dishDTO.setId(id);
        dishDTO.setStatus(status);
        dishDTO.setFlavors(null);
        dishService.update(dishDTO);
        cleanCache("dish_*");
        return Result.success();

    }
    @DeleteMapping
    @ApiOperation("菜品的删除")
    public Result deleteById(@RequestParam  List<Long> ids){
        dishService.deleteByIds(ids);
        cleanCache("dish_*");
        return Result.success();


    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVO=dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateWithDishFlavors(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);

        cleanCache("dish_*");
        return Result.success();
    }
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
    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
