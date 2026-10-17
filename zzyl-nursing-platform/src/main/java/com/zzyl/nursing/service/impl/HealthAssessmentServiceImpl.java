package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.json.JSONUtil;
import com.zzyl.common.ai.AssessmentAgent;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.IDCardUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.dto.HealthAssessmentDto;
import com.zzyl.nursing.vo.health.HealthReportVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.nursing.mapper.HealthAssessmentMapper;
import com.zzyl.nursing.domain.HealthAssessment;
import com.zzyl.nursing.service.IHealthAssessmentService;

/**
 * 健康评估Service业务层处理
 *
 * @author XuCheng
 * @date 2026-09-27
 */
@Service
public class HealthAssessmentServiceImpl extends ServiceImpl<HealthAssessmentMapper,HealthAssessment> implements IHealthAssessmentService
{
    @Autowired
    private HealthAssessmentMapper healthAssessmentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AssessmentAgent assessmentAgent;

    /**
     * 查询健康评估
     *
     * @param id 健康评估主键
     * @return 健康评估
     */
    @Override
    public HealthAssessment selectHealthAssessmentById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询健康评估列表
     *
     * @param healthAssessment 健康评估
     * @return 健康评估
     */
    @Override
    public List<HealthAssessment> selectHealthAssessmentList(HealthAssessment healthAssessment)
    {
        return healthAssessmentMapper.selectHealthAssessmentList(healthAssessment);
    }

    /**
     * 新增健康评估
     *
     * @param healthAssessmentDto 健康评估
     * @return 结果
     */
    @Override
    public long insertHealthAssessment(HealthAssessmentDto healthAssessmentDto)
    {
        // 获取文件中的内容
        String content = (String) redisTemplate.opsForHash().get("healthReport", healthAssessmentDto.getIdCard());
        //拿到ai评估结果json数据
        String assessResult = assessmentAgent.assess(content);
        if (StringUtils.isEmpty(assessResult)){
            throw new BaseException("AI模型调用失败");
        }
        //json格式字符串封装为对象
        HealthReportVo healthReportVo = JSONUtil.toBean(assessResult, HealthReportVo.class);
        redisTemplate.opsForHash().delete("healthReport",healthAssessmentDto.getIdCard());
        //保存方法,返回主键信息
        return insertHealthAssessment(healthAssessmentDto, healthReportVo);
    }

    private Long insertHealthAssessment(HealthAssessmentDto healthAssessmentDto,
                                        HealthReportVo healthReportVo) {
        //创建实体类对象
        HealthAssessment healthAssessment = new HealthAssessment();
        //属性填充
        healthAssessment.setPhysicalExamInstitution(healthAssessmentDto.getPhysicalExamInstitution());
        healthAssessment.setElderName(healthAssessmentDto.getElderName());
        healthAssessment.setIdCard(healthAssessmentDto.getIdCard());
        healthAssessment.setBirthDate(IDCardUtils.getBirthDateByIdCard(healthAssessmentDto.getIdCard()));
        healthAssessment.setGender(IDCardUtils.getGenderFromIdCard(healthAssessmentDto.getIdCard()));
        healthAssessment.setAge(IDCardUtils.getAgeByIdCard(healthAssessmentDto.getIdCard()));

        double healthScore = healthReportVo.getHealthAssessment().getHealthIndex();
        healthAssessment.setHealthScore(String.valueOf(healthScore));
        healthAssessment.setRiskLevel(healthReportVo.getHealthAssessment().getRiskLevel());
        //是否建议入住
        healthAssessment.setSuggestionForAdmission(getSuggestionForAdmission(healthScore));
        healthAssessment.setNursingLevelName(getNursingLevelName(healthScore));
        healthAssessment.setAdmissionStatus(1);
        healthAssessment.setTotalCheckDate(healthReportVo.getTotalCheckDate());
        healthAssessment.setPhysicalReportUrl(healthAssessmentDto.getPhysicalReportUrl());
        healthAssessment.setAssessmentTime(LocalDateTime.now());
        healthAssessment.setReportSummary(healthReportVo.getSummarize());
        healthAssessment.setDiseaseRisk(JSONUtil.toJsonStr(healthReportVo.getRiskDistribution()));
        healthAssessment.setAbnormalAnalysis(JSONUtil.toJsonStr(healthReportVo.getAbnormalData()));
        healthAssessment.setSystemScore(JSONUtil.toJsonStr(healthReportVo.getSystemScore()));

        save(healthAssessment);
        return healthAssessment.getId();
    }

    private String getNursingLevelName(double healthScore) {
        // 处理边界
        if (healthScore < 0 || healthScore > 100) {
            throw new IllegalArgumentException("健康评分必须在0到100之间");
        }

        if (healthScore >= 80) {
            return "三级护理等级";
        }else if (healthScore >= 70) {
            return "二级护理等级";
        }else {
            return "一级护理等级";
        }
    }

    private Integer getSuggestionForAdmission(double healthScore) {
        if (healthScore >= 60){
            return 0;
        }
        return 1;
    }

    /**
     * 修改健康评估
     *
     * @param healthAssessment 健康评估
     * @return 结果
     */
    @Override
    public int updateHealthAssessment(HealthAssessment healthAssessment)
    {
        healthAssessment.setUpdateTime(DateUtils.getNowDate());
        return updateById(healthAssessment) ? 1 : 0;
    }

    /**
     * 批量删除健康评估
     *
     * @param ids 需要删除的健康评估主键
     * @return 结果
     */
    @Override
    public int deleteHealthAssessmentByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除健康评估信息
     *
     * @param id 健康评估主键
     * @return 结果
     */
    @Override
    public int deleteHealthAssessmentById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }
}
