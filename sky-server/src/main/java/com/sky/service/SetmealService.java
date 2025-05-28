package com.sky.service;

import java.util.List;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

public interface SetmealService {



   /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
   PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 更新套餐表
     * @param setmealId
     */
    void update(SetmealDTO setmealDTO);

    
    /**
     * 修改菜品状态
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
    
}
