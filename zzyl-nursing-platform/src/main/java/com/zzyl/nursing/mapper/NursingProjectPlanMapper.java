package com.zzyl.nursing.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzyl.nursing.vo.NursingProjectPlanVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.zzyl.nursing.domain.NursingProjectPlan;

/**
 * 护理计划和项目关联Mapper接口
 * 
 * @author XuCheng
 * @date 2026-08-22
 */
@Mapper
public interface NursingProjectPlanMapper extends BaseMapper<NursingProjectPlan>
{
    /**
     * 查询护理计划和项目关联
     * 
     * @param id 护理计划和项目关联主键
     * @return 护理计划和项目关联
     */
    public NursingProjectPlan selectNursingProjectPlanById(Long id);

    /**
     * 查询护理计划和项目关联列表
     * 
     * @param nursingProjectPlan 护理计划和项目关联
     * @return 护理计划和项目关联集合
     */
    public List<NursingProjectPlan> selectNursingProjectPlanList(NursingProjectPlan nursingProjectPlan);

    /**
     * 新增护理计划和项目关联
     * 
     * @param nursingProjectPlan 护理计划和项目关联
     * @return 结果
     */
    public int insertNursingProjectPlan(NursingProjectPlan nursingProjectPlan);

    /**
     * 修改护理计划和项目关联
     * 
     * @param nursingProjectPlan 护理计划和项目关联
     * @return 结果
     */
    public int updateNursingProjectPlan(NursingProjectPlan nursingProjectPlan);

    /**
     * 删除护理计划和项目关联
     * 
     * @param id 护理计划和项目关联主键
     * @return 结果
     */
    public int deleteNursingProjectPlanById(Long id);

    /**
     * 批量删除护理计划和项目关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNursingProjectPlanByIds(Long[] ids);

    /**
     * 批量新增护理计划和项目关联
     *
     * @param projectPlans 护理计划和项目关联
     * @param id
     * @return 结果
     */
    public int batchInsert(@Param("list") List<NursingProjectPlan> projectPlans,@Param("planId") Long id);

    /**
     * 根据计划id查询护理项目计划关联
     *
     * @param  planId 计划id
     * @return 护理项目计划关联
     */
    List<NursingProjectPlanVo> selectNursingProjectPlanByPlanId(Long planId);
}
