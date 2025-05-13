package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import com.sky.entity.DishFlavor;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入菜品口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     * @param id
     * @return
     */
    @Delete("select * from dish_flavor where dish_id = #{id}")
    void deleteByDishIds (List<Long> ids);
    
}
