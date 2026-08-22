package com.zzyl.nursing.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.zzyl.common.core.domain.R;
import com.zzyl.nursing.vo.NursingProjectVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 护理项目Controller
 *
 * @author xucheng
 * @date 2026-08-17
 */
@Api(tags = "护理项目管理")
@RestController
@RequestMapping("/nursing/project")
public class NursingProjectController extends BaseController
{
    @Autowired
    private INursingProjectService nursingProjectService;

    /**
     * 查询护理项目列表
     */
    @ApiOperation("查询护理项目列表")
    @PreAuthorize("@ss.hasPermi('nursing:project:list')")
    @GetMapping("/list")
    public TableDataInfo<NursingProject> list(
            @ApiParam(value = "护理项目查询条件")
            NursingProject nursingProject)
    {
        startPage();
        List<NursingProject> list =
                nursingProjectService.selectNursingProjectList(nursingProject);

        return getDataTable(list);
    }

    /**
     * 导出护理项目列表
     */
    @ApiOperation("导出护理项目列表")
    @PreAuthorize("@ss.hasPermi('nursing:project:export')")
    @Log(title = "护理项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(
            @ApiParam(value = "HTTP响应对象", hidden = true)
            HttpServletResponse response,

            @ApiParam(value = "护理项目查询条件")
            NursingProject nursingProject)
    {
        List<NursingProject> list =
                nursingProjectService.selectNursingProjectList(nursingProject);

        ExcelUtil<NursingProject> util =
                new ExcelUtil<NursingProject>(NursingProject.class);

        util.exportExcel(response, list, "护理项目数据");
    }

    /**
     * 获取护理项目详细信息
     */
    @ApiOperation("根据ID获取护理项目详细信息")
    @PreAuthorize("@ss.hasPermi('nursing:project:query')")
    @GetMapping(value = "/{id}")
    public R<NursingProject> getInfo(
            @ApiParam(value = "护理项目ID", required = true, example = "1")
            @PathVariable("id") Long id)
    {
        return R.ok(nursingProjectService.selectNursingProjectById(id));
    }

    /**
     * 新增护理项目
     */
    @ApiOperation("新增护理项目")
    @PreAuthorize("@ss.hasPermi('nursing:project:add')")
    @Log(title = "护理项目", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(
            @ApiParam(value = "护理项目信息", required = true)
            @RequestBody NursingProject nursingProject)
    {
        nursingProjectService.insertNursingProject(nursingProject);
        return R.ok();
    }

    /**
     * 修改护理项目
     */
    @ApiOperation("修改护理项目")
    @PreAuthorize("@ss.hasPermi('nursing:project:edit')")
    @Log(title = "护理项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(
            @ApiParam(value = "护理项目信息", required = true)
            @RequestBody NursingProject nursingProject)
    {
        nursingProjectService.updateNursingProject(nursingProject);
        return R.ok();
    }

    /**
     * 删除护理项目
     */
    @ApiOperation("删除护理项目")
    @PreAuthorize("@ss.hasPermi('nursing:project:remove')")
    @Log(title = "护理项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(
            @ApiParam(
                    value = "护理项目ID数组",
                    required = true,
                    example = "1,2,3"
            )
            @PathVariable Long[] ids)
    {
        nursingProjectService.deleteNursingProjectByIds(ids);
        return R.ok();
    }

    @GetMapping("/all")
    @ApiOperation("查询所有护理项目")
    public AjaxResult listAll(){
        List<NursingProjectVO> list = nursingProjectService.selectAll();
        return success(list);
    }


}