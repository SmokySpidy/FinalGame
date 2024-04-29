package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    //根据dishid查找对应的套餐ids
    List<Long> selectByDishIdReturnSetMealId(List<Long> ids);

    void insertBatch(List<SetmealDish> setmealDishes);
    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getBySetmealId(Long id);
    @Delete("delete from setmeal_dish where setmeal_id=#{id}")
    void deleteBySetMealId(Long id);
}
