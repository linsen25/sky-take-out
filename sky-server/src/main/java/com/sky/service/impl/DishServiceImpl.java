package com.sky.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味数据
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        // 因为dishDTO中有菜品的基本信息和口味信息，所以需要将其拆分成两个对象
        Dish dish = new Dish();
        //1.将dishDTO中的基本信息拷贝到dish中
        BeanUtils.copyProperties(dishDTO, dish);

        //1.向餐品表添加一个菜品
        dishMapper.insert(dish);

        //获取菜品id 通过dish对象的id属性 从xml生成的 获取insert语句的主键值
        Long dishId = dish.getId();

        //2.向菜品口味表添加n的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 口味数据是否存在
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                //设置菜品id
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch( flavors);
        }
    }


    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }



    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断当前菜品是否能够删除---是否存在起售中的菜品品？？
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //菜品正在起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品是否能够删除---是否被套餐关联了？？
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        
        //删除菜品表中的菜品口味数据

        // 我们来做个优化变成一条sql
        // for (Long id : ids) {
        //       dishMapper.deleteById(id);
        //       dishFlavorMapper.deleteByDishId(id);

            
        // }

        // 根据菜品id集合批量删除菜品数据
        // sql: delete from dish where id in (?,?,?)
        dishMapper.deleteByIds(ids); 
        // 根据菜品id集合批量删除菜品口味数据
        dishFlavorMapper.deleteByDishIds(ids);
         // sql: delete from dish_flavor where dish_id in (?,?,?)
    }


    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
        //1.根据id查询菜品
        Dish dish = dishMapper.getById(id);
        if (dish == null) {
            // 如果未查询到菜品，抛出异常或返回空结果
            throw new IllegalArgumentException("菜品不存在，id: " + id);
        }
        //2.根据菜品查口味信息
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //3.将菜品基本信息和口味信息封装到DishVO中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);// categoryName 不需要所以拷贝不过来没有关系
        dishVO.setFlavors(flavors);
        return dishVO;
    }


    /**
     * 修改菜品
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        //1.将dishDTO中的基本信息拷贝到dish中
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        
        //2.修改菜品表中的基本信息
        dishMapper.update(dish);
        //3.修改菜品口味表中的口味信息
        //3.1 先删除菜品口味表中的口味信息
        dishFlavorMapper.deleteByDishIds(List.of(dish.getId()));
        //3.2 再添加菜品口味表中的口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 口味数据是否存在
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                //新增的口味数据是没有dish_id的，所以需要设置
                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }
    
    /**
     * 修改菜品状态
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        log.info("修改菜品状态：{}, {}", status, id);
        dishMapper.updateStatus(status, id);
    }


    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
    */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
            .categoryId(categoryId)
            .status(StatusConstant.ENABLE)
            .build();
        return dishMapper.list(dish);
    }


    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


    
}
