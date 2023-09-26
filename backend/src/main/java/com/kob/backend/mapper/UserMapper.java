package com.kob.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 引入Mybaits-plus实现增删改查
public interface UserMapper extends BaseMapper<User> {

}
