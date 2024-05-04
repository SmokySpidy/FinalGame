package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "用户端商店相关接口")
@Slf4j
public class ShopController {
    public static final String KEY="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取当前店铺状态")
    public Result<Integer> getStatus(){
       /* int status = Integer.parseInt((String) redisTemplate.opsForValue().get(KEY));*/
        int status =(Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(status);
    }
}
