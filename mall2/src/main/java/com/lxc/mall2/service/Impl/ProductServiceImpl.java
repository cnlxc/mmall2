package com.lxc.mall2.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ResponseCode;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.dao.CategoryMapper;
import com.lxc.mall2.dao.ProductMapper;
import com.lxc.mall2.pojo.Category;
import com.lxc.mall2.pojo.Product;
import com.lxc.mall2.service.IProductService;
import com.lxc.mall2.util.DateTimeUtil;
import com.lxc.mall2.util.PropertiesUtil;
import com.lxc.mall2.vo.ProductDetailVo;
import com.lxc.mall2.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 82138 on 2018/8/19.
 */

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryServiceImpl categoryService;

    public ServerResponse saveProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALARGUMENT.getCode(),ResponseCode.ILLEGALARGUMENT.getDesc());
        }
        if(StringUtils.isNoneBlank(product.getSubImages())) {
            String[] subImagesArray = product.getSubImages().split(",");
            if(subImagesArray.length > 0) {
                product.setMainImage(subImagesArray[0]);
            }
        }
        if(product.getId() != null) {
            int resultCount = productMapper.updateByPrimaryKey(product);
            if(resultCount > 0) {
                return ServerResponse.createBySuccess("更新产品成功");
            }
            return ServerResponse.createByErrorMessage("更新产品失败");
        }else{
            int resultCount = productMapper.insert(product);
            if (resultCount > 0){
                return ServerResponse.createBySuccess("新增产品成功");
            }
            return ServerResponse.createByErrorMessage("新增产品失败");
        }
    }

    public ServerResponse setProductStatus(Integer productId, Integer status) {
        if(productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALARGUMENT.getCode(),ResponseCode.ILLEGALARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if(resultCount > 0) {
            return ServerResponse.createBySuccess("修改产品状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品状态失败");

    }
    public ServerResponse<ProductDetailVo> manageProductDetial(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALARGUMENT.getCode(),ResponseCode.ILLEGALARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }


    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImages(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailVo.setCategoryId(0);//种类表里没有的话，默认为根节点
        }else{
            productDetailVo.setCategoryId(product.getCategoryId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);//pagehelper使用方法 https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md
        /*跟在startPage后面的第一个查询会被分页*/
        List<Product> productList = productMapper.selectList();
        List<ProductListVO> productListVOs = Lists.newArrayList();
        for(Product productItem : productList) {
            ProductListVO productListVO = assembleProductListVO(productItem);
            productListVOs.add(productListVO);
        }
        PageInfo pageResult = new PageInfo(productList);//没明白要这个干啥
        pageResult.setList(productListVOs);
        return ServerResponse.createBySuccess(pageResult);
    }

    private static ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVo = new ProductListVO();

        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImages(product.getMainImage());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());

        return  productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndId(productId,productName);
        List<ProductListVO> productListVOs = Lists.newArrayList();
        for(Product productItem : productList) {
            ProductListVO productListVO = assembleProductListVO(productItem);
            productListVOs.add(productListVO);
        }
        PageInfo pageResult = new PageInfo(productList);//没明白要这个干啥
        pageResult.setList(productListVOs);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse getProductDetail(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALARGUMENT.getCode(),ResponseCode.ILLEGALARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        return ServerResponse.createBySuccess(product);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALARGUMENT.getCode(),ResponseCode.ILLEGALARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)) {
                //分类没找到，搜索关键字还为空
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVO = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVO);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList =  categoryService.getDeepCategoryId(categoryId).getData();
        }
        if(StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        //PageInfo 排序
        if(StringUtils.isNotBlank(orderBy) && Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy))
            PageHelper.startPage(pageNum,pageSize,orderBy.replace("_"," "));
        else
            //价格以外全部按更新时间排序
            PageHelper.startPage(pageNum,pageSize,"update_time desc");

        List<Product> list = productMapper.selectByNameAndCatrgoryIds(StringUtils.isBlank(keyword) ? null:keyword,
                                                                      categoryIdList.size()==0 ? null:categoryIdList);
        List<ProductListVO> ProductListVoList = Lists.newArrayList();
        for(Product product : list) {
            ProductListVO productListVO = assembleProductListVO(product);
            ProductListVoList.add(productListVO);
        }
        PageInfo pageInfo = new PageInfo(list);
        pageInfo.setList(ProductListVoList);
        return ServerResponse.createBySuccess(pageInfo);



    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
    public void transactionTest(){
        Product product = new Product();
        product.setId(35);
        product.setName("DOTA2 点卡");
        product.setCategoryId(10002);
        product.setPrice(new BigDecimal(100) );
        product.setStock(1000);
        product.setStatus(Const.ProductStatusEnum.ON_SALE.getCode());
        productMapper.insert(product);
        int i = 5/0;
    }
}
