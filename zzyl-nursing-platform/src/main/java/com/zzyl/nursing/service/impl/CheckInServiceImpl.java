package com.zzyl.nursing.service.impl;

import java.util.List;
import java.util.Arrays;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.nursing.mapper.CheckInMapper;
import com.zzyl.nursing.domain.CheckIn;
import com.zzyl.nursing.service.ICheckInService;

/**
 * 入住信息Service业务层处理
 *
 * @author XuCheng
 * @date 2026-08-23
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper,CheckIn> implements ICheckInService
{
    @Autowired
    private CheckInMapper checkInMapper;

    /**
     * 查询入住信息
     *
     * @param id 入住信息主键
     * @return 入住信息
     */
    @Override
    public CheckIn selectCheckInById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询入住信息列表
     *
     * @param checkIn 入住信息
     * @return 入住信息
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn)
    {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住信息
     *
     * @param checkIn 入住信息
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn)
    {
        checkIn.setCreateTime(DateUtils.getNowDate());
         return save(checkIn) ? 1 : 0;
    }

    /**
     * 修改入住信息
     *
     * @param checkIn 入住信息
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn)
    {
        checkIn.setUpdateTime(DateUtils.getNowDate());
        return updateById(checkIn) ? 1 : 0;
    }

    /**
     * 批量删除入住信息
     *
     * @param ids 需要删除的入住信息主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除入住信息信息
     *
     * @param id 入住信息主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }
}
