package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param setmealId
     * @param dishIds
     */
    // select setmeal_id from setmeal_dish where dish_id in (#{dishIds})
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
} 
