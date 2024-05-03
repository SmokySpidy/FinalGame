package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;

import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    public void save(SetmealDTO setmealDTO) {
        //先判断套餐里的菜品是否都已经起售，如有未起售的菜品，则不能添加该套餐、、select count(*) from dish where status='0' and id in (dishids)
        List<Long> dishIds=new ArrayList<>();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> dishIds.add(setmealDish.getDishId()));
        List<Dish> dishes = dishMapper.selectOnStart(dishIds);
        Integer count=dishIds.size()-dishes.size();
        if(count>0){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }
        //1.往套餐表添加数据
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setMealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        //2.往套餐-菜品中间表添加数据
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setMealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public SetmealVO getById(Long id) {
        //1.获取categoryName以及setmeal属性，涉及到多表查询
        SetmealVO setmealVO=setMealMapper.getById(id);
        //2.获取套餐包含的菜品，根据套餐id查询菜品list，查setmealdish表
        List<SetmealDish>setmealDishes=setMealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }
    @Override
    public void update(SetmealDTO setmealDTO) {
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null){
            setMealDishMapper.deleteBySetMealId(setmealDTO.getId());
            if(setmealDishes.size()>0){
                setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
                setMealDishMapper.insertBatch(setmealDishes);
            }
        }
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setMealMapper.update(setmeal);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        //查询是否有正在售卖的套餐,select count(*) from setmeal where status and id in()
        Integer count=setMealMapper.countBySetmealIds(ids);
        if(count!=0)
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);

        //如果都符合要求，则根据id删除套餐
        setMealMapper.deleteByIds(ids);
    }



    @Override
    public List<DishItemVO> getDishesById(Long setMealId) {
        return setMealMapper.getBySetmealId(setMealId);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setMealMapper.list(setmeal);
    }
}
