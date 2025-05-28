package com.sky.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //向套餐表插入数据
        setmealMapper.insert(setmeal);
        //获取生成的套餐id
        Long setmealId = setmeal.getId();

        //向套餐菜品表插入数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);

    }


    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNumber = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNumber, pageSize);

        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //先测试通过拿到每个的id看看是不是启售如果是就不可以删除
        ids.forEach(id -> {
            // 根据套餐id查询套餐的数量
            Setmeal setmeal = setmealMapper.getById(id);

            if (StatusConstant.ENABLE == setmeal.getStatus()) {
                //起售中的套餐不能删除
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        // 如果可以删除
        ids.forEach(setmealId -> {
            //删除套餐表中的数据
            setmealMapper.deleteById(setmealId);
            //删除套餐菜品关系表中的数据
            setmealDishMapper.deleteBySetmealId(setmealId);
        });
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        // 设置套餐内的 dishes的id
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        // 设置套餐内的菜品的名称
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        // 设置套餐内的菜品的名称
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //更新套餐表
        setmealMapper.update(setmeal);

        //删除套餐菜品关系表中的数据
        setmealDishMapper.deleteBySetmealId(setmeal.getId());

        //重新插入套餐菜品关系表中的数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }


    /**
     * 修改菜品状态
     * @param status
     * @param id
     */
    @Transactional
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if (StatusConstant.ENABLE == status) {
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && dishList.size() > 0) {
                dishList.forEach(dish -> {
                    if (StatusConstant.DISABLE == dish.getStatus()) {
                        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        //修改套餐状态
        Setmeal setmeal = Setmeal.builder()
        .id(id)
        .status(status)
        .build();
        setmealMapper.update(setmeal);
    }
    
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
