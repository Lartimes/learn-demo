package org.bankai.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.bankai.model.User;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user")
    List<User> selectAll();

    @Select("SELECT * FROM user WHERE uid = #{id}")
    User selectById(Long id);


    @Select("SELECT * FROM user WHERE name = #{name} and password = #{password}")
    User selectByInfo(@Param("name") String name, @Param("password") String password);


    // 添加缺失的方法
    @Select("<script>" +
            "SELECT * FROM user WHERE uid IN " +
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<User> selectInIds(@Param("list") List<Integer> uids);

    @Update("UPDATE user SET  login_time = #{loginTime} where uid = #{uid}")
    Integer updateLogin(User user);


}
