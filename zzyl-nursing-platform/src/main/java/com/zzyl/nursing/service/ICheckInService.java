package com.zzyl.nursing.service;

import java.util.List;
import com.zzyl.nursing.domain.CheckIn;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.vo.CheckInDetailVo;

/**
 * 入住信息Service接口
 * 
 * @author XuCheng
 * @date 2026-08-23
 */
public interface ICheckInService extends IService<CheckIn>
{
    /**
     * 查询入住信息
     * 
     * @param id 入住信息主键
     * @return 入住信息
     */
    public CheckIn selectCheckInById(Long id);

    /**
     * 查询入住信息列表
     * 
     * @param checkIn 入住信息
     * @return 入住信息集合
     */
    public List<CheckIn> selectCheckInList(CheckIn checkIn);

    /**
     * 新增入住信息
     * 
     * @param checkIn 入住信息
     * @return 结果
     */
    public int insertCheckIn(CheckIn checkIn);

    /**
     * 修改入住信息
     * 
     * @param checkIn 入住信息
     * @return 结果
     */
    public int updateCheckIn(CheckIn checkIn);

    /**
     * 批量删除入住信息
     * 
     * @param ids 需要删除的入住信息主键集合
     * @return 结果
     */
    public int deleteCheckInByIds(Long[] ids);

    /**
     * 删除入住信息信息
     * 
     * @param id 入住信息主键
     * @return 结果
     */
    public int deleteCheckInById(Long id);

    /**
     * 入住申请
     *
     * @param checkInApplyDto 入住申请信息
     * @return 结果
     */
    void apply(CheckInApplyDto checkInApplyDto);

    /**
     * 入住信息详情
     *
     * @param id 入住信息主键
     * @return 入住信息
     */
    CheckInDetailVo detail(Long id);
}
