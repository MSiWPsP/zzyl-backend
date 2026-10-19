package com.zzyl.nursing.job;

import com.zzyl.nursing.service.IContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("contractJob")
public class ContractJob {

    @Autowired
    private IContractService iContractService;

    //定时更新合同状态 
    public void updateContractStatus(){
        iContractService.updateContractStatus();
    }

}
