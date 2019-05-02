package com.lxc.mall2.controller.backend;

import com.google.common.collect.Maps;
import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.Product;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.service.IFileService;
import com.lxc.mall2.service.IProductService;
import com.lxc.mall2.service.IUserService;
import com.lxc.mall2.util.CookieUtil;
import com.lxc.mall2.util.JsonUtil;
import com.lxc.mall2.util.PropertiesUtil;
import com.lxc.mall2.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by 82138 on 2018/8/19.
 */

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    IUserService iUserService;
    @Autowired
    IProductService iProductService;
    @Autowired
    IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest request, Product product) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.saveProduct(product);
        }
        return ServerResponse.createByErrorMessage("不是管理员");
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> setProductStatus(HttpServletRequest request, Integer productId, Integer status) {
       // User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setProductStatus(productId,status);
        }
        return ServerResponse.createByErrorMessage("不是管理员");
    }

    @RequestMapping("get_detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpServletRequest request, Integer productId) {
       //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
       }
       if(iUserService.checkAdminRole(user).isSuccess()) {
           return iProductService.manageProductDetial(productId);
       }else
           return ServerResponse.createByErrorMessage("不是管理员,无权限");
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpServletRequest request, @RequestParam(value="pageNum",defaultValue="1")  int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum,pageSize);
        }else
            return ServerResponse.createByErrorMessage("不是管理员,无权限");
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpServletRequest request, @RequestParam(value="pageNum",defaultValue="1")  int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize, String productName, Integer productId) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else
            return ServerResponse.createByErrorMessage("不是管理员,无权限");
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFilename = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFilename;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFilename);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else
            return ServerResponse.createByErrorMessage("不是管理员,无权限");
    }
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richTextImgUpload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            resultMap.put("success",false);
            resultMap.put("msg","用戶未登錄");
            return resultMap;
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFilename = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFilename)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFilename;
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            resultMap.put("success",false);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }

    }
}

