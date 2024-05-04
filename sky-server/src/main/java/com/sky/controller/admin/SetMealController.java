package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("SetMealAdminController")
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理相关接口")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.getCategoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setMealService.save(setmealDTO);
        return Result.success();

    }
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> PageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
    SetmealVO setmealVO=setMealService.getById(id);
    return Result.success(setmealVO);
    }
    @PostMapping("/status/{status}")
    @ApiOperation("起售或者禁售套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result StartOrStop(@PathVariable Integer status,Long id){
    //1.封装成dto对象然后调用service层的update函数
        SetmealDTO setmealDTO=new SetmealDTO();
        setmealDTO.setId(id);
        setmealDTO.setStatus(status);
        setmealDTO.setSetmealDishes(null);
        setMealService.update(setmealDTO);
        return Result.success();
    }
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
    setMealService.update(setmealDTO);
    return Result.success();
    }
    @DeleteMapping
    @ApiOperation("根据id删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteByIds(@RequestParam List<Long> ids){
        setMealService.deleteByIds(ids);
        return Result.success();
    }

}
