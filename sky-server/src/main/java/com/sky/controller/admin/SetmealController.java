package com.sky.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



/**
 * 套餐管理
 */

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    // controller 中注入service
    @Autowired
    private SetmealService setmealService;


    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);

    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "批量删除套餐")
    public Result delete(@RequestParam("ids") List<Long> ids) {
        log.info("批量删除套餐：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询套餐信息")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐信息：{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }


    /**
     * 修改套餐状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("status/{status}")
    @ApiOperation(value = "套餐启售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("套餐启售停售：{}", id);
        setmealService.startOrStop(status, id);
        return Result.success();
    }
    
    
}
