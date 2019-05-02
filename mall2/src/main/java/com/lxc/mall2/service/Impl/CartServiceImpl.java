package com.lxc.mall2.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ResponseCode;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.dao.CartMapper;
import com.lxc.mall2.dao.ProductMapper;
import com.lxc.mall2.pojo.Cart;
import com.lxc.mall2.pojo.Product;
import com.lxc.mall2.service.ICartService;
import com.lxc.mall2.util.BigDecimalUtil;
import com.lxc.mall2.util.PropertiesUtil;
import com.lxc.mall2.vo.CartProductVo;
import com.lxc.mall2.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 82138 on 2018/9/2.
 */

@Service("ICartService")
public class CartServiceImpl implements ICartService{

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count) {
        if(count == null || productId == null) {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALARGUMENT.getCode(),ResponseCode.ILLEGALARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart == null) {
            Cart cart1 = new Cart();
            cart1.setUserId(userId);
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(cart1);
        }else {
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);

    }

    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count) {
        if(count == null || productId == null) {
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALARGUMENT.getCode(),ResponseCode.ILLEGALARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    public ServerResponse<CartVo> productDelete(Integer userId,String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList) ){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALARGUMENT.getCode(),ResponseCode.ILLEGALARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductId(userId,productList);
        CartVo cartVo = this.getCartLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked) {
        cartMapper.selectOrUnselectAllProduct(userId,productId,checked);
        return this.list(userId);
    }


    private CartVo getCartLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVos = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setUserId(cartItem.getUserId());
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null) {
                    cartProductVo.setProductId(product.getId());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtitlle(product.getSubtitle());
                    cartProductVo.setMainImage(product.getMainImage());
                    cartProductVo.setProductStatus(product.getStatus());
                    //判断库存
                    int buyCountLimit = 0;
                    if(product.getStock() >= cartItem.getQuantity()) {
                        //库存充足的时候
                        buyCountLimit = cartItem.getQuantity();

                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_SUCCESS);
                    }else{
                        buyCountLimit = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_FAILED);
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyCountLimit);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQunantity(buyCountLimit);
                    //计算该产品在购物车中的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),buyCountLimit));
                    cartProductVo.setChecked(cartItem.getChecked());
                }
                if(cartProductVo.getChecked() == Const.Cart.CHECKED) {
                    //如果是勾选状态，则加入总价
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVos.add(cartProductVo);
            }
            cartVo.setCartProductVoList(cartProductVos);
            cartVo.setCartTotalPrice(cartTotalPrice);
            cartVo.setAllChecked(this.getAllCheckedStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        }
        return cartVo;
    }
    private boolean getAllCheckedStatus(Integer userId) {
        if(userId == null) {
            return false;
        }
        return cartMapper.selectCheckedByUserId(userId)== 0;

    }


}
