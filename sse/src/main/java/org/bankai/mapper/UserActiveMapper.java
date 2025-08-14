package org.bankai.mapper;

import org.apache.ibatis.annotations.*;
import org.bankai.model.UserActive;

import java.util.Collection;
import java.util.Date;
import java.util.List;

//    todo： 分库分表代替全表扫描

@Mapper
public interface UserActiveMapper {


    @Insert("INSERT INTO user_active (user_id  ,last_active) " +
            "VALUES (#{userId} , #{lastActive})")
    int insertInit(UserActive userActive);


    /**
     * 根据用户ID更新活跃时间和过期时间
     */
    @Update("""
            UPDATE user_active
            SET last_active = #{lastActive} 
            WHERE user_id = #{userId}
            """)
    int updateActive(@Param("userId") Integer userId, @Param("lastActive") Date lastActive);

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


    // 添加缺失的方法
    @Select("<script>" +
            "SELECT * FROM user_active WHERE user_id IN " +
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<UserActive> selectInIds(@Param("list") Collection<Integer> uids);


    /**
     * 批量更新用户活跃状态（使用 JDK 17 文本块）
     */
    @Update("""
                <script>
                    <foreach collection='list' item='item' separator=';'>
                        UPDATE user_active
                        SET 
                            expire_time = #{item.expireTime}
                        WHERE user_id = #{item.userId}
                    </foreach>
                </script>
            """)
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    Integer batchUpdateUserActive(List<UserActive> userActives);

}
