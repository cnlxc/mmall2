package com.lxc.mall2.controller.portal;

import com.github.pagehelper.PageInfo;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 82138 on 2018/9/1.
 */

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(Integer categoryId) {
        return iProductService.getProductDetail(categoryId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required=false)String keyword,
                                         @RequestParam(value="categoryid" ,required=false)Integer categoryId,
                                         @RequestParam(value="pagenum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pagesize",defaultValue = "10")int pageSize,
                                         @RequestParam(value = "orderby",defaultValue = "")String orderBy) {
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
