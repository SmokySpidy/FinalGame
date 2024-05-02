package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Service
@Transactional
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Override
    public void save(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(dishFlavor-> dishFlavor.setDishId(id));
        }
        dishFlavorMapper.insertbatch(flavors);
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void update(DishDTO dishDTO) {
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null) {
            //1.修改dishflavor表
            dishFlavorMapper.deleteByDishId(dishDTO.getId());

            if (flavors.size() > 0) {
                flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
                dishFlavorMapper.insertbatch(flavors);
            }

        }


        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //2.更新dish表
        dishMapper.update(dish);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        //1.判断当前ids里面是否有正在起售的菜品，有的话不能删除
        //select id from dish where status=1 and id in ()
        List<Dish> list = dishMapper.selectOnStart(ids);
        if(list!=null&&list.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        //2.判断当前菜品是否与套餐相关联，如有关联，则无法删除
        List<Long> setMealIds = setMealDishMapper.selectByDishIdReturnSetMealId(ids);
        if(setMealIds!=null&&setMealIds.size()>0){
         throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //3.根据id、删除菜品及对应口味
        dishMapper.deleteBatch(ids);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO=dishMapper.getById(id);
        List<DishFlavor> flavors=dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    public List<DishVO> getByCategoryId(Long categoryId) {
        List<DishVO> dishVOS=dishMapper.getByCategoryId(categoryId);
        dishVOS.forEach(dishVO-> dishVO.setFlavors(dishFlavorMapper.getByDishId(dishVO.getId())));
        return dishVOS;
    }
}
