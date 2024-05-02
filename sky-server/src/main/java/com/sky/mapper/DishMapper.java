package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.Annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);
    List<Long> selectOnStart(List<Long> ids);

    void deleteBatch(List<Long> ids);

    DishVO getById(Long id);

    List<DishVO> getByCategoryId(Long categoryId);

    Integer countByUnsaledDishes(List<Long> dishIds);
}
