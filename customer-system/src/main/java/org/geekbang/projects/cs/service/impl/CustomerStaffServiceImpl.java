package org.geekbang.projects.cs.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.geekbang.projects.cs.entity.staff.CustomerStaff;
import org.geekbang.projects.cs.entity.tenant.OutsourcingSystem;
import org.geekbang.projects.cs.infrastructure.exception.BizException;
import org.geekbang.projects.cs.infrastructure.page.PageObject;
import org.geekbang.projects.cs.integration.OutsourcingSystemClient;
import org.geekbang.projects.cs.mapper.CustomerStaffMapper;
import org.geekbang.projects.cs.service.ICustomerStaffService;
import org.geekbang.projects.cs.service.IOutsourcingSystemService;
import org.geekbang.projects.cs.servicebus.endpoint.CustomerStaffEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CustomerStaffServiceImpl extends ServiceImpl<CustomerStaffMapper, CustomerStaff> implements ICustomerStaffService {
    @Autowired
    IOutsourcingSystemService outsourcingSystemService;

    @Autowired
    OutsourcingSystemClient outsourcingSystemClient;

    @Autowired
    CustomerStaffEndpoint customerStaffEndpoint;

    @Override
    public PageObject<CustomerStaff> findCustomerStaffs(Long pageSize, Long pageIndex) {
        return getCustomerStaffPageObject(null, pageSize, pageIndex);
    }

    @Override
    public List<CustomerStaff> findCustomerStaffs() {
        return baseMapper.selectList(new LambdaQueryWrapper<CustomerStaff>());
    }

    @Override
    public PageObject<CustomerStaff> findCustomerStaffsByName(String staffName, Long pageSize, Long pageIndex) {
        return getCustomerStaffPageObject(staffName, pageSize, pageIndex);
    }

    private PageObject<CustomerStaff> getCustomerStaffPageObject(String staffName, Long pageSize, Long pageIndex) {
        LambdaQueryWrapper<CustomerStaff> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(CustomerStaff::getIsDeleted, false);

        if (!Objects.isNull(staffName)) {
            queryWrapper.like(CustomerStaff::getStaffName, staffName);
        }
        queryWrapper.orderByDesc(CustomerStaff::getCreateTime);

        IPage<CustomerStaff> page = new Page<>(pageIndex, pageSize);
        IPage<CustomerStaff> pagedResult = baseMapper.selectPage(page, queryWrapper);

        PageObject<CustomerStaff> pagedObject = new PageObject<CustomerStaff>();
        pagedObject.buildPage(pagedResult.getRecords(), pagedResult.getTotal(), pagedResult.getCurrent(), pagedResult.getSize());

        return pagedObject;
    }

    @Override
    public CustomerStaff findCustomerStaffById(Long staffId) {
        return baseMapper.selectById(staffId);
    }

    @Override
    public Boolean createCustomerStaff(CustomerStaff customerStaff) throws BizException {
        return this.save(customerStaff);
    }

    @Override
    public Boolean updateCustomerStaff(CustomerStaff customerStaff) {
        //??????????????????updateById(),????????????Entity??????????????????????????????LambdaQueryWrapper
        return this.update(customerStaff, new LambdaQueryWrapper<CustomerStaff>().eq(CustomerStaff::getId, customerStaff.getId()));
    }

    @Override
    public Boolean deleteCustomerStaffById(Long staffId) {
        return this.removeById(staffId);
    }

    @Override
    public void syncOutsourcingCustomerStaffsBySystemId(Long systemId) {
        //??????????????????
        OutsourcingSystem outsourcingSystem = outsourcingSystemService.findOutsourcingSystemById(systemId);

        //????????????????????????????????????
//        List<CustomerStaff> customerStaffs = outsourcingSystemClient.getCustomerStaffs(outsourcingSystem);


        List<CustomerStaff> customerStaffs = customerStaffEndpoint.fetchCustomerStaffs(outsourcingSystem);

        //??????????????????
        saveOrUpdateBatch(customerStaffs);
    }

}
