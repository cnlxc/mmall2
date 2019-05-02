package com.lxc.mall2.service;

import com.github.pagehelper.PageInfo;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.Product;
import com.lxc.mall2.vo.ProductDetailVo;

/**
 * Created by 82138 on 2018/8/19.
 */
public interface IProductService {
    ServerResponse saveProduct(Product product);

    ServerResponse<ProductDetailVo> manageProductDetial(Integer productId);

    ServerResponse setProductStatus(Integer productId, Integer status);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
