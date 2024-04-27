package com.sky.Aspect;

import com.sky.Annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@Aspect
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.Annotation.AutoFill)")
    public void autofillpointcut(){}
    @Before("autofillpointcut()")
    public void autofill(JoinPoint joinPoint) throws NoSuchMethodException {
        //获得方法签名对象
        MethodSignature signature1 = (MethodSignature) joinPoint.getSignature();
        //获得方法上的注解
        AutoFill annotation = signature1.getMethod().getAnnotation(AutoFill.class);
        //获得注解中的操作类型
        OperationType value = annotation.value();

        //获取待自动注入的对象
        Object[] args = joinPoint.getArgs();
        Object arg = args[0];

        //获取当前修改用户，修改时间
        Long updateUser= BaseContext.getCurrentId();
        LocalDateTime updateTime= LocalDateTime.now();
        if(value.equals(OperationType.INSERT)){
            try {
                LocalDateTime createTime=updateTime;
                Long createUser=updateUser;
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setCreateTime.invoke(arg,createTime);
                setCreateUser.invoke(arg,createUser);
                setUpdateTime.invoke(arg,updateTime);
                setUpdateUser.invoke(arg,updateUser);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{

            try {
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(arg,updateTime);
                setUpdateUser.invoke(arg,updateUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
