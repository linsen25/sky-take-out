package com.sky.aspect;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;

// import javassist.bytecode.SignatureAttribute.MethodSignature;
import org.aspectj.lang.reflect.MethodSignature;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.Join;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 * 自定义切面类 AutoFillAspect，统一拦截加入了 AutoFill注解的方法，通过反射为公共字段赋值
 */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
	/**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
        // 定义切入点
         
    }
    /**
     * 前置通知,在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")
    public void autofill(JoinPoint joinPoint) {
        log.info("开始对公共字段进行填充...");
        
        // 获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signiture = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signiture.getMethod().getAnnotation(AutoFill.class);//获取到当前被拦截的方法上的注解对象
        OperationType operationType = autoFill.value();//获取到当前被拦截的方法上的数据库操作类型
        //获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args.length == 0 || args[0] == null){
            log.error("当前被拦截的方法没有传入参数");
            return;
        }
        Object entity = args[0];//获取到当前被拦截的方法的参数--实体对象，不局限于employee

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //反射为4个公共字段赋值 create time、update time、createUser、updateUser
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射调用方法
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
                log.info("公共字段赋值成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE){
            //为2个公共字段赋值 update time和updateUser
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射调用方法
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
                log.info("公共字段赋值成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }
}
