package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryShopTypeList() {
        // 1.从redis查询商品类型缓存
        String key = "cache:shopType";
        String shopTypeJson = stringRedisTemplate.opsForValue().get(key);

        // 2.判断redis中是否存在
        if(StrUtil.isNotBlank(shopTypeJson)){
            // 3.redis中存在,直接返回，用toList()转化为列表
            List<ShopType> shopTypeList = JSONUtil.toList(shopTypeJson, ShopType.class);
            return Result.ok(shopTypeList);
        }

        // 4.redis中不存在,查询所有类型,并排序
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();

        // 5.数据库中不存在,返回错误
        if(shopTypeList.size() == 0){
            return Result.fail("店铺类型列表为空!");
        }

        // 6.数据库中存在,写入redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shopTypeList));

        // 7.返回
        return Result.ok(shopTypeList);
    }
}
