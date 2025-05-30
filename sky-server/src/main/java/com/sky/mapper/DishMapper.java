package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     * @param id
     * @return
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);


    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 主键查询菜品
     * @param ids
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);


    /**
     * 根据主键删除菜品
     * @param dish
     */ 
    @Delete("delete from dish where id = #{id}")
    void deleteByIds(List<Long> ids);

    /**
     * 根据id动态修改菜品
     * @param ids
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);


    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    void updateStatus(Integer status, Long id);


    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id in (select dish_id from setmeal_dish where setmeal_id = #{id})")
    List<Dish> getBySetmealId(Long id);

}
