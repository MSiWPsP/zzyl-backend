package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.nursing.mapper.ContractMapper;
import com.zzyl.nursing.domain.Contract;
import com.zzyl.nursing.service.IContractService;

/**
 * 合同Service业务层处理
 *
 * @author XuCheng
 * @date 2026-08-23
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper,Contract> implements IContractService
{
    @Autowired
    private ContractMapper contractMapper;

    /**
     * 查询合同
     *
     * @param id 合同主键
     * @return 合同
     */
    @Override
    public Contract selectContractById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询合同列表
     *
     * @param contract 合同
     * @return 合同
     */
    @Override
    public List<Contract> selectContractList(Contract contract)
    {
        return contractMapper.selectContractList(contract);
    }

    /**
     * 新增合同
     *
     * @param contract 合同
     * @return 结果
     */
    @Override
    public int insertContract(Contract contract)
    {
        contract.setCreateTime(DateUtils.getNowDate());
         return save(contract) ? 1 : 0;
    }

    /**
     * 修改合同
     *
     * @param contract 合同
     * @return 结果
     */
    @Override
    public int updateContract(Contract contract)
    {
        contract.setUpdateTime(DateUtils.getNowDate());
        return updateById(contract) ? 1 : 0;
    }

    /**
     * 更新合同状态
     */
    @Override
    public void updateContractStatus() {
        //查询所有 状态为0的合同 && 合同的开始时间<当前时间
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getStatus,0)
                .le(Contract::getStartDate, LocalDateTime.now());
        List<Contract> contracts = list(wrapper);
        //更改状态
        contracts.forEach(contract -> contract.setStatus(1));
        updateBatchById(contracts);
    }

    /**
     * 批量删除合同
     *
     * @param ids 需要删除的合同主键
     * @return 结果
     */
    @Override
    public int deleteContractByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除合同信息
     *
     * @param id 合同主键
     * @return 结果
     */
    @Override
    public int deleteContractById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }
}
