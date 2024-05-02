package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void save(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void update(DishDTO dishDTO);

    void deleteByIds(List<Long> ids);

    DishVO getByIdWithFlavor(Long id);

    List<DishVO> getByCategoryId(Long categoryId);
}
