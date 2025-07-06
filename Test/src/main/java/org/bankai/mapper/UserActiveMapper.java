package org.bankai.mapper;

import org.apache.ibatis.annotations.*;
import org.bankai.model.UserActive;

import java.util.Date;

//    todo： 分库分表代替全表扫描

@Mapper
public interface UserActiveMapper {

    @Insert("INSERT INTO user_active (user_id,  login_times) " +
            "VALUES (#{userId}, #{loginTime})")
    int insertInit(UserActive userActive);

    @Insert("""
                UPDATE user_active 
                    SET login_time = #{loginTime}
                    WHERE user_id = #{userId}
            """)
    int updateLogin(UserActive userActive);

    /**
     * 根据用户ID更新活跃时间和过期时间
     */
    @Update("""
            UPDATE user_active
            SET last_active = #{lastActive} 
            WHERE user_id = #{userId}
            """)
    int updateActive(@Param("userId") Integer userId, @Param("lastActive") Date lastActive, @Param("expireTime") Date expireTime);

    @Update("""
            UPDATE user_active
            SET  expire_time = #{expireTime} 
            WHERE user_id = #{userId}
            """)
    int updateExpire(@Param("userId") Integer userId, @Param("expireTime") Date expireTime);

    /**
     * 根据用户ID查询活跃记录
     */
    @Select("SELECT * FROM user_active WHERE user_id = #{userId}")
    UserActive selectByUserId(Integer userId);




}
