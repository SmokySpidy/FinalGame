package com.sky.service.impl;

import com.sky.service.ShoppingCartService;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;
    /**
     * 添加购物车数据
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //1.查询该数据是否已经存在购物车中
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList=shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList!=null&&shoppingCartList.size()>0){//当前数据在购物车中
        shoppingCart=shoppingCartList.get(0);
        shoppingCart.setNumber(shoppingCart.getNumber()+1);
        shoppingCartMapper.updateNumberById(shoppingCart);
        }else{//当前数据不在购物车中
            Long dishId=shoppingCartDTO.getDishId();
        // 2.先判断是套餐还是菜品
            if(dishId!=null){
                DishVO dishVO = dishMapper.getById(dishId);
                shoppingCart.setAmount(dishVO.getPrice());
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setImage(dishVO.getImage());

            }
            else{
                SetmealVO setmealVO = setMealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());

            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartMapper.insert(shoppingCart);
        }


        //3.调用dishmapper或者setmealmapper查询对应的image，name，amount，number，createtime
        //3.如果不在，则往购物车表中添加一条数据，如果存在，则修改购物车里面的该条数据，设置其数量+1
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void cleanShoppingCart() {
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.delete(shoppingCart);
    }

    @Override
    public void delShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList=shoppingCartMapper.list(shoppingCart);
        shoppingCart=shoppingCartList.get(0);
        if(shoppingCart.getNumber()>1){
            shoppingCart.setNumber(shoppingCart.getNumber()-1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        }else
        shoppingCartMapper.delete(shoppingCart);
    }
}
