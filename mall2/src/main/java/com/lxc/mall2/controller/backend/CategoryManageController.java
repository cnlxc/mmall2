package com.lxc.mall2.controller.backend;

import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ResponseCode;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.service.ICategoryService;
import com.lxc.mall2.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 82138 on 2018/8/15.
 */

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
        public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员，放心添加品类逻辑
            return iCategoryService.addCategory(categoryName,parentId);

        }else//不是管理员
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");

    }
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String newName) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员，可以更新品类名字
            return iCategoryService.updateCategoryName(categoryId,newName);

        }else//不是管理员
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");

    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategoryId(HttpSession session, @RequestParam(value="categoryId" ,defaultValue = "0") int categoryId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员，获取子节点
            return iCategoryService.getChildrenParallelCategoryId(categoryId);
        }else//不是管理员
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
    }


    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepCategoryId(HttpSession session, @RequestParam(value="categoryId" ,defaultValue = "0") int categoryId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员，用递归获取所有子节点
            return iCategoryService.getDeepCategoryId(categoryId);
        }else//不是管理员
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
    }

}