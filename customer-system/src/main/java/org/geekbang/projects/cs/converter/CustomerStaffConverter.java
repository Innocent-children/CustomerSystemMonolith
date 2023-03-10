package org.geekbang.projects.cs.converter;

import org.geekbang.projects.cs.controller.vo.req.AddCustomerStaffReqVO;
import org.geekbang.projects.cs.controller.vo.req.UpdateCustomerStaffReqVO;
import org.geekbang.projects.cs.controller.vo.resp.CustomerStaffRespVO;
import org.geekbang.projects.cs.entity.staff.CustomerStaff;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CustomerStaffConverter {
    CustomerStaffConverter INSTANCE = Mappers.getMapper(CustomerStaffConverter.class);

    //VO-->Entity
    CustomerStaff addCustomerStaffReqVOToCustomerStaff(AddCustomerStaffReqVO addCustomerStaffReqVO);

    CustomerStaff updateCustomerStaffReqVOToCustomerStaff(UpdateCustomerStaffReqVO updateCustomerStaffReqVO);

    //Entity-->VO
    CustomerStaffRespVO customerStaffToCustomerStaffRespVO(CustomerStaff customerStaff);

    List<CustomerStaffRespVO> customerStaffsToCustomerStaffRespVOs(List<CustomerStaff> customerStaffList);

}
