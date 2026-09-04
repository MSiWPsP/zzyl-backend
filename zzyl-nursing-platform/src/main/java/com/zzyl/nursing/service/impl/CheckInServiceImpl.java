package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.CodeGenerator;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.dto.CheckInElderDto;
import com.zzyl.nursing.dto.ElderFamilyDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.vo.CheckInConfigVo;
import com.zzyl.nursing.vo.CheckInDetailVo;
import com.zzyl.nursing.vo.CheckInElderVo;
import com.zzyl.nursing.vo.ElderFamilyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.nursing.service.ICheckInService;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

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

    /**
     * 入住申请
     *
     * @param checkInApplyDto 入住申请信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void apply(CheckInApplyDto checkInApplyDto) {
        //1.判断老人是否入住
        LambdaQueryWrapper<Elder> queryWrapper = new LambdaQueryWrapper<>();
        CheckInElderDto checkInElderDto = checkInApplyDto.getCheckInElderDto();
        queryWrapper.eq(Elder::getIdCardNo,checkInElderDto.getIdCardNo());
        queryWrapper.eq(Elder::getStatus,1);
        Elder elder = elderMapper.selectOne(queryWrapper);
        if (!ObjectUtil.isEmpty(elder)){
            throw new BaseException("该老人已入住");
        }
        //2.更新床位信息
        Bed bed = bedMapper.selectBedById(checkInApplyDto.getCheckInConfigDto().getBedId());
        bed.setBedStatus(1);
        bedMapper.updateById(bed);

        //3.插入或更新老人数据
        elder = insertOrUpdateElder(bed,checkInApplyDto.getCheckInElderDto());

        //4.保存合同信息
        insertContract(elder,checkInApplyDto);

        //5.新增入住信息
        CheckIn checkIn = insertCheckIn(elder,checkInApplyDto);

        //6.新增入住配置信息
        insertCheckInConfig(checkIn.getId(),checkInApplyDto);

    }

    @Override
    public CheckInDetailVo detail(Long id) {
        //创建返回的VO对象
        CheckInDetailVo checkInDetailVo = new CheckInDetailVo();
        //获取入住配置信息check_in/check_in_config
        CheckIn checkIn = checkInMapper.selectById(id);
        LambdaQueryWrapper<CheckInConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInConfig::getCheckInId,id);
        CheckInConfig checkInConfig = checkInConfigMapper.selectOne(wrapper);
        CheckInConfigVo checkInConfigVo = new CheckInConfigVo();
        BeanUtils.copyProperties(checkIn,checkInConfigVo);
        BeanUtils.copyProperties(checkInConfig,checkInConfigVo);
        checkInDetailVo.setCheckInConfigVo(checkInConfigVo);
        //获取老人信息
        CheckInElderVo checkInElderVo = new CheckInElderVo();
        Elder elder = elderMapper.selectElderById(checkIn.getElderId());
        BeanUtils.copyProperties(elder,checkInElderVo);
        checkInDetailVo.setCheckInElderVo(checkInElderVo);
        checkInElderVo.setAge(IdcardUtil.getAgeByIdCard(elder.getIdCardNo()));
        //设置家属信息
        String remark = checkIn.getRemark();
        List<ElderFamilyVo> elderFamilyVos = JSON.parseArray(remark, ElderFamilyVo.class);
        checkInDetailVo.setElderFamilyVoList(elderFamilyVos);
        //获取合同信息
        LambdaQueryWrapper<Contract> eldWrapper = new LambdaQueryWrapper<>();
        eldWrapper.eq(Contract::getElderId,elder.getId());
        Contract contract = contractMapper.selectOne(eldWrapper);
        checkInDetailVo.setContract(contract);
        return checkInDetailVo;
    }

    private void insertCheckInConfig(Long id, CheckInApplyDto checkInApplyDto) {
        //创建实体对象
        CheckInConfig checkInConfig = new CheckInConfig();

        //属性赋值
        checkInConfig.setCheckInId(id);
        BeanUtils.copyProperties(checkInApplyDto.getCheckInConfigDto(),checkInConfig);

        //保存
        checkInConfigMapper.insert(checkInConfig);
        
    }

    private CheckIn insertCheckIn(Elder elder, CheckInApplyDto checkInApplyDto) {
        //创建实体类对象
        CheckIn checkIn = new CheckIn();
        //属性赋值
        checkIn.setElderId(elder.getId());
        checkIn.setElderName(elder.getName());
        checkIn.setIdCardNo(elder.getIdCardNo());
        checkIn.setNursingLevelName(checkInApplyDto.getCheckInConfigDto().getNursingLevelName());
        checkIn.setStartDate(checkInApplyDto.getCheckInConfigDto().getStartDate());
        checkIn.setEndDate(checkInApplyDto.getCheckInConfigDto().getEndDate());
        checkIn.setBedNumber(elder.getBedNumber());
        List<ElderFamilyDto> elderFamilyDtoList = checkInApplyDto.getElderFamilyDtoList();
        checkIn.setRemark(JSON.toJSONString(elderFamilyDtoList));
        checkIn.setStatus(0);
        //保存
        checkInMapper.insert(checkIn);
        return checkIn;
    }

    private void insertContract(Elder elder, CheckInApplyDto checkInApplyDto) {
        //创建实体类对象
        Contract contract = new Contract();
        //属性赋值
        BeanUtils.copyProperties(checkInApplyDto.getCheckInContractDto(),contract);
        contract.setElderId(elder.getId());
        contract.setElderName(elder.getName());
        String contractNumber ="HT" + CodeGenerator.generateContractNumber();
        contract.setContractNumber(contractNumber);
        LocalDateTime startDate = checkInApplyDto.getCheckInConfigDto().getStartDate();
        LocalDateTime endDate = checkInApplyDto.getCheckInConfigDto().getEndDate();
        Integer status = startDate.isAfter(LocalDateTime.now()) ? 0 : 1;
        contract.setStatus(status);
        contract.setStartDate(startDate);
        contract.setEndDate(endDate);

        //调用对应的mapper,实现数据的保存
        contractMapper.insert(contract);
    }

    private Elder insertOrUpdateElder(Bed bed, CheckInElderDto checkInElderDto) {
        //查询老人数据
        LambdaQueryWrapper<Elder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Elder::getIdCardNo,checkInElderDto.getIdCardNo());
        wrapper.ne(Elder::getStatus,1);
        Elder elder = elderMapper.selectOne(wrapper);
        if (elder != null){
            //查到了老人信息,更新
            BeanUtils.copyProperties(checkInElderDto,elder);
            elder.setBedNumber(bed.getBedNumber());
            elder.setBedId(bed.getId());
            elder.setStatus(1);
            elderMapper.updateById(elder);
        }else {
            //没查到,插入
            elder = new Elder();
            BeanUtils.copyProperties(checkInElderDto,elder);
            elder.setBedNumber(bed.getBedNumber());
            elder.setBedId(bed.getId());
            elder.setStatus(1);
            elderMapper.insert(elder);
        }
        return elder;
    }
}
