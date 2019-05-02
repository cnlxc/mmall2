package com.lxc.mall2.vo;

import java.math.BigDecimal;

/**
 * Created by 82138 on 2018/9/3.
 * 购物车和产品结合的一个抽象对象
 */
public class CartProductVo {
    private Integer id;
    private Integer userId;
    private Integer ProductId;
    private Integer Qunantity;
    private String productName;
    private String productSubtitlle;
    private BigDecimal price;//单价
    private Integer productStock;//产品库存，购物车产品数量不能大于库存
    private Integer checked;//产品是否勾选
    private String limitQuantity;//数量限制是否成功的一个返回结果
    private String mainImage;
    private BigDecimal productTotalPrice;//购物车中该产品的总价

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }



    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    private Integer productStatus;

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return ProductId;
    }

    public void setProductId(Integer productId) {
        ProductId = productId;
    }

    public Integer getQunantity() {
        return Qunantity;
    }

    public void setQunantity(Integer qunantity) {
        Qunantity = qunantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSubtitlle() {
        return productSubtitlle;
    }

    public void setProductSubtitlle(String productSubtitlle) {
        this.productSubtitlle = productSubtitlle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public String getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }


}
