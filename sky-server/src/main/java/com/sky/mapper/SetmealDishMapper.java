package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.SetmealDish;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param setmealId
     * @param dishIds
     */
    // select setmeal_id from setmeal_dish where dish_id in (#{dishIds})
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);


    /**
     * 批量插入套餐菜品关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐菜品关系
     * @param setmealId
     */
    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param setmealId
*/
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询套餐菜品关系
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long id);


    /**
     * 更改状态
     * @param status
     * @param id
     */
    @Select("update setmeal_dish set status = #{status} where id = #{id}")
    void setStatus(Integer status, Long id);
} 
