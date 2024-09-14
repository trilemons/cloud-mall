package com.lemon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.domain.LoginMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMemberMapper extends BaseMapper<LoginMember> {
}