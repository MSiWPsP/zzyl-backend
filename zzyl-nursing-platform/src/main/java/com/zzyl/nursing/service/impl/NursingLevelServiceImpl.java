package com.zzyl.nursing.service.impl;

import java.sql.Wrapper;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.vo.NursingLevelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingLevelMapper;
import com.zzyl.nursing.domain.NursingLevel;
import com.zzyl.nursing.service.INursingLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 护理等级Service业务层处理
 * 
 * @author alexis
 * @date 2024-12-30
 */
@Service
public class NursingLevelServiceImpl extends ServiceImpl<NursingLevelMapper, NursingLevel> implements INursingLevelService
{
    @Autowired
    private NursingLevelMapper nursingLevelMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String CACHE_KEY_FIX = "nursingLevel:all";

    /**
     * 查询护理等级
     * 
     * @param id 护理等级主键
     * @return 护理等级
     */
    @Override
    public NursingLevel selectNursingLevelById(Long id)
    {
        return nursingLevelMapper.selectById(id);
    }

    /**
     * 查询护理等级列表
     * 
     * @param nursingLevel 护理等级
     * @return 护理等级
     */
    @Override
    public List<NursingLevel> selectNursingLevelList(NursingLevel nursingLevel)
    {
        return nursingLevelMapper.selectNursingLevelList(nursingLevel);
    }

    /**
     * 新增护理等级
     * 
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int insertNursingLevel(NursingLevel nursingLevel)
    {
        int result = nursingLevelMapper.insert(nursingLevel);
        deleteCache();
        return result;
    }

    /**
     * 修改护理等级
     * 
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int updateNursingLevel(NursingLevel nursingLevel)
    {
        int result = nursingLevelMapper.updateById(nursingLevel);
        deleteCache();
        return result;
    }

    /**
     * 批量删除护理等级
     * 
     * @param ids 需要删除的护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelByIds(Long[] ids)
    {
        int result = nursingLevelMapper.deleteBatchIds(Arrays.asList(ids));
        deleteCache();
        return result;
    }

    /**
     * 删除护理等级信息
     * 
     * @param id 护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelById(Long id)
    {
        int i = nursingLevelMapper.deleteById(id);
        //删除缓存
        deleteCache();
        return i;
    }

    private void deleteCache() {
        redisTemplate.delete(CACHE_KEY_FIX);
    }

    /**
     * 查询护理等级Vo列表
     *
     * @param nursingLevel 条件
     * @return 结果
     */
    @Override
    public List<NursingLevelVo> selectNursingLevelVoList(NursingLevel nursingLevel) {
        return nursingLevelMapper.selectNursingLevelVoList(nursingLevel);
    }

    /**
     * 查询所有护理等级
     * @return
     */
    @Override
    public List<NursingLevel> listAll() {
        //先从缓存查
        List<NursingLevel> list = (List<NursingLevel>) redisTemplate.opsForValue().get(CACHE_KEY_FIX);
        //判断是否命中数据
        if (list != null && !list.isEmpty()){
            return list;
        }
        LambdaQueryWrapper<NursingLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NursingLevel::getStatus, 1);
        list = list(wrapper);

        //写入 Redis
        if (list != null && !list.isEmpty()) {
            redisTemplate.opsForValue().set(CACHE_KEY_FIX, list);
        }
        return list;
    }
}
