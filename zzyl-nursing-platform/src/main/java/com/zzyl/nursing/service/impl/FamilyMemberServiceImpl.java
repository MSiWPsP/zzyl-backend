package com.zzyl.nursing.service.impl;

import java.util.*;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.framework.web.service.TokenService;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.service.WeChatService;
import com.zzyl.nursing.vo.LoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.nursing.mapper.FamilyMemberMapper;
import com.zzyl.nursing.domain.FamilyMember;
import com.zzyl.nursing.service.IFamilyMemberService;

/**
 * 老人家属Service业务层处理
 *
 * @author XuCheng
 * @date 2026-10-24
 */
@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper,FamilyMember> implements IFamilyMemberService
{
    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private TokenService tokenService;

    /**
     * 查询老人家属
     *
     * @param id 老人家属主键
     * @return 老人家属
     */
    @Override
    public FamilyMember selectFamilyMemberById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人家属列表
     *
     * @param familyMember 老人家属
     * @return 老人家属
     */
    @Override
    public List<FamilyMember> selectFamilyMemberList(FamilyMember familyMember)
    {
        return familyMemberMapper.selectFamilyMemberList(familyMember);
    }

    /**
     * 新增老人家属
     *
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int insertFamilyMember(FamilyMember familyMember)
    {
        familyMember.setCreateTime(DateUtils.getNowDate());
         return save(familyMember) ? 1 : 0;
    }

    /**
     * 修改老人家属
     *
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int updateFamilyMember(FamilyMember familyMember)
    {
        familyMember.setUpdateTime(DateUtils.getNowDate());
        return updateById(familyMember) ? 1 : 0;
    }

    /**
     * 批量删除老人家属
     *
     * @param ids 需要删除的老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除老人家属信息
     *
     * @param id 老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }

    @Override
    public LoginVo login(UserLoginRequestDto userLoginRequestDto) {
        //1.获取openid
        String openId = weChatService.getOpenId(userLoginRequestDto.getCode());
        //2.根据openid查询客户信息
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getOpenId,openId);
        FamilyMember familyMember = getOne(wrapper);
        //3.如果客户不存在,则创建客户信息(随机创建nickName),并且赋值openid
        if (familyMember == null){
            familyMember = new FamilyMember();
            familyMember.setOpenId(openId);
        }
        //4.获取手机号码
        String phone = weChatService.getPhone(userLoginRequestDto.getPhoneCode());
        //5.新增或更新用户信息
        saveOrUpdateMember(familyMember,phone);
        //6.生成JWT(封装用户id和昵称)
        Map<String, Object> claim = new HashMap<>();
        claim.put("userId",familyMember.getId());
        claim.put("nickName",familyMember.getName());
        String token = tokenService.createToken(claim);
        //7.返回LoginVO
        return LoginVo.builder()
                .token(token)
                .nickName(familyMember.getName())
                .build();
    }

    static List<String> DEFAULT_NICKNAME_PREFIX = ListUtil.of(
            "生活更美好",
            "大桔大利",
            "日富一日",
            "好柿开花",
            "柿柿如意",
            "一椰暴富",
            "大柚所为",
            "杨梅吐气",
            "天生荔枝"
    );

    private void saveOrUpdateMember(FamilyMember familyMember, String phone) {
        //判断手机号码是否和之前的一样
        if (!Objects.equals(familyMember.getPhone(), phone)) {
            familyMember.setPhone(phone);
        }
        //根据对象有没有id 去新增或更新
        if (familyMember.getId() != null){
            //这个对象是数据库查出来的,做更新操作
            updateById(familyMember);
            return;
        }
        //id不存在 保存
        //随机生成昵称(成语+手机号码后四位)
        String nickName = DEFAULT_NICKNAME_PREFIX.get(new Random().nextInt(DEFAULT_NICKNAME_PREFIX.size())) + phone.substring(phone.length() - 4);
        familyMember.setName(nickName);
        save(familyMember);
    }
}
