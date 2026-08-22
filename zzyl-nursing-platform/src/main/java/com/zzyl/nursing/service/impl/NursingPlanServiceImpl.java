package com.zzyl.nursing.service.impl;

import java.util.List;
import java.util.Arrays;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.domain.NursingProjectPlan;
import com.zzyl.nursing.dto.NursingPlanDto;
import com.zzyl.nursing.mapper.NursingProjectPlanMapper;
import com.zzyl.nursing.vo.NursingPlanVo;
import com.zzyl.nursing.vo.NursingProjectPlanVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.nursing.mapper.NursingPlanMapper;
import com.zzyl.nursing.domain.NursingPlan;
import com.zzyl.nursing.service.INursingPlanService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 护理计划Service业务层处理
 *
 * @author XuCheng
 * @date 2026-08-21
 */
@Service
public class NursingPlanServiceImpl extends ServiceImpl<NursingPlanMapper,NursingPlan> implements INursingPlanService
{
    @Autowired
    private NursingPlanMapper nursingPlanMapper;

    @Autowired
    private NursingProjectPlanMapper nursingProjectPlanMapper;


    /**
     * 查询护理计划
     *
     * @param id 护理计划主键
     * @return 护理计划
     */
    @Override
    public NursingPlanVo selectNursingPlanById(Long id)
    {
        //查询护理计划表
        NursingPlan nursingPlan = getById(id);

        NursingPlanVo nursingPlanVo = new NursingPlanVo();

        BeanUtils.copyProperties(nursingPlan, nursingPlanVo);

        //查询护理项目计划中间表
        List<NursingProjectPlanVo> projectPlans = nursingProjectPlanMapper.selectNursingProjectPlanByPlanId(id);

        nursingPlanVo.setProjectPlans(projectPlans);

        return nursingPlanVo;
    }

    /**
     * 查询护理计划列表
     *
     * @param nursingPlan 护理计划
     * @return 护理计划
     */
    @Override
    public List<NursingPlan> selectNursingPlanList(NursingPlan nursingPlan)
    {
        return nursingPlanMapper.selectNursingPlanList(nursingPlan);
    }

    /**
     * 新增护理计划
     *
     * @param nursingPlanDto 护理计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNursingPlan(NursingPlanDto nursingPlanDto)
    {
        //1.保存计划表数据
        NursingPlan nursingPlan = new NursingPlan();
        BeanUtils.copyProperties(nursingPlanDto,nursingPlan);
        save(nursingPlan);
        //2.保存中间表数据
        List<NursingProjectPlan> projectPlans = nursingPlanDto.getProjectPlans();
        if (projectPlans != null && !projectPlans.isEmpty())
        {
            return nursingProjectPlanMapper.batchInsert(
                    projectPlans,
                    nursingPlan.getId()
            );
        }

        return 0;
    }

    /**
     * 修改护理计划
     *
     * @param nursingPlan 护理计划
     * @return 结果
     */
    @Override
    public int updateNursingPlan(NursingPlan nursingPlan)
    {
        nursingPlan.setUpdateTime(DateUtils.getNowDate());
        return updateById(nursingPlan) ? 1 : 0;
    }

    /**
     * 批量删除护理计划
     *
     * @param ids 需要删除的护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除护理计划信息
     *
     * @param id 护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }
}
