package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.Annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {


    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);
    @Select("select  count(*) from setmeal  where category_id=#{id} group by category_id")
    Integer countByCategoryId(Long id);
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    SetmealVO getById(Long id);
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    Integer countBySetmealIds(List<Long> ids);

    void deleteByIds(List<Long> ids);
}
