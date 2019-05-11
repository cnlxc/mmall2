package com.lxc.mall2.service.Impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lxc.mall2.dao.*;
import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.*;
import com.lxc.mall2.service.IOrderService;
import com.lxc.mall2.util.BigDecimalUtil;
import com.lxc.mall2.util.DateTimeUtil;
import com.lxc.mall2.util.FTPUtil;
import com.lxc.mall2.util.PropertiesUtil;
import com.lxc.mall2.vo.OrderItemVo;
import com.lxc.mall2.vo.OrderProductVo;
import com.lxc.mall2.vo.OrderVo;
import com.lxc.mall2.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by 82138 on 2018/9/15.
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService{

    @Autowired
    private OrderMapper ordermapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    public ServerResponse buy(Long orderNo,Integer userId,String path){
        Map resultMap = Maps.newHashMap();
        Order order = ordermapper.selectOrderByUserIdAndOrderNo(userId,orderNo);
        if(order == null) {
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(orderNo);

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("happymall扫码支付,订单号: ").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("总共金额").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = orderItemMapper.getByUserIdAndOrderNo(userId,orderNo);
        for(OrderItem orderItem : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString()
                    ,orderItem.getProductName()
                    ,BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100)).longValue()
                    ,orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);
        }
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        //private static AlipayTradeService   tradeWithHBService;
        //tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                File folder = new File(path);
                if(!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }


                // 需要修改为运行机器上的路径
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                File targetFile = new File(path,qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("上传二维码异常",e);
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                resultMap.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(resultMap);
                //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");

        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServerResponse aliCallBack(Map<String,String> params) {
        long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order  = ordermapper.selectByOrder(orderNo);
        if(order==null)
            return ServerResponse.createByErrorMessage("订单不存在，回调验证失败");

        if(order.getStatus() > Const.OrderStatus.PAID.getCode())
            return ServerResponse.createBySuccess("支付宝重复通知");

        if(Const.AlipayCallBack.TRADE_STATUS_TRADE_SUCCESS.equals(params.get(tradeStatus))) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")).toDate());
            order.setStatus(Const.OrderStatus.PAID.getCode());
            ordermapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);
        return  ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderStatus(Integer userId,long orderNo){
        Order order = ordermapper.selectOrderByUserIdAndOrderNo(userId,orderNo);
        if(order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if(order.getStatus() > Const.OrderStatus.PAID.getCode()) {
            return ServerResponse.createBySuccess("订单已支付");
        }
        return ServerResponse.createByErrorMessage("订单未支付");

    }

    public ServerResponse createOrder(Integer userId,Integer shippingId) {
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse =  getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess())
            return ServerResponse.createByErrorMessage("创建订单失败");
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = this.assembleOrder(userId,shippingId,payment);
        if(order == null) {
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        if(orderItemList == null) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for(OrderItem item : orderItemList) {
            item.setOrderNo(order.getOrderNo());
        }
        //mybatis batchInsert
        orderItemMapper.batchInsert(orderItemList);
        //20180923
        //减少库存    20181001 0:02
        this.productStockReduce(orderItemList);
        //清空购物车
        this.cleanCart(cartList);
        OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);

    }
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList) {
        OrderVo ordervo = new OrderVo();
        ordervo.setOrderNo(order.getOrderNo());
        ordervo.setPaymentType(order.getPaymentType());
        order.setPayment(order.getPayment());
        ordervo.setPaymentTypeDesc(Const.PaymentTypeEnum.getPaymentTypeDesc(order.getPaymentType()));

        ordervo.setPostage(order.getPostage());
        ordervo.setStatus(order.getStatus());
        ordervo.setStatusDesc(Const.OrderStatus.getOrderStauts(order.getStatus()).getValue());
        ordervo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            ordervo.setReceiverName(shipping.getReceiverName());
            ordervo.setShippingVo(this.assembleShippingVo(shipping));
        }
        ordervo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        ordervo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        ordervo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        ordervo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        ordervo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        //201810010:42

        List<OrderItemVo> orderItemVoList =  Lists.newArrayList();
        for(OrderItem orderItem : orderItemList) {
            OrderItemVo o  = this.assembleOrderItemVo(orderItem);
            orderItemVoList.add(o);
        }
        ordervo.setOrderItemVoList(orderItemVoList);
        return ordervo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItem.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;

    }
    private void cleanCart(List<Cart> cartList) {
        for(Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void productStockReduce(List<OrderItem> orderItemList){
        for(OrderItem orderItem: orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment) {
        long orderNo =this.orderNoGenerate();
        Order order = new Order();
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatus.NO_PAY.getCode());
        order.setUserId(userId);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        //现在是创建订单阶段，所以发货时间，交易结束时间等不在这里设定。
        int rowCount = ordermapper.insert(order);
        if(rowCount > 0)
            return order;
        return null;
    }

    private long orderNoGenerate() {
        return System.currentTimeMillis()+ new Random().nextInt(100);
    }
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment  = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }
    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for(Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if(Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus())
                return ServerResponse.createByErrorMessage(product.getName()+"已经下架。");
            //校验库存
            if(product.getStock() < cart.getQuantity())
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足。");

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity()));
            orderItemList.add(orderItem);
        }

        return ServerResponse.createBySuccess(orderItemList);
    }

    public ServerResponse<String> cancle(Integer userId,Long orderNo) {
        Order order = ordermapper.selectOrderByUserIdAndOrderNo(userId,orderNo);
        if(order == null) {
            return ServerResponse.createByErrorMessage("此用户不存在该订单");
        }
        if(order.getStatus() != Const.OrderStatus.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("订单已支付，无法取消");
        }
        Order updateOrder = new Order();
        updateOrder.setOrderNo(orderNo);
        updateOrder.setUserId(userId);
        updateOrder.setStatus(Const.OrderStatus.CANCELED.getCode());
        int row = ordermapper.updateByPrimaryKeySelective(updateOrder);
        if(row > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId) {
        OrderProductVo orderProductVo = new OrderProductVo();
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse response = this.getCartOrderItem(userId,cartList);
        if(!response.isSuccess())
            return response;
        List<OrderItem> orderItemList = (List<OrderItem>)response.getData();
        List<OrderItemVo> orderItemVos = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");

        for(OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVos.add(this.assembleOrderItemVo(orderItem));
        }
        orderProductVo .setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVos);

        return ServerResponse.createBySuccess(orderProductVo);
    }

    public ServerResponse getOrderDetial(Integer userId,Long orderNo) {
        Order order = ordermapper.selectOrderByUserIdAndOrderNo(userId,orderNo);
        if(order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByUserIdAndOrderNo(userId,orderNo);
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("no found order");

    }

    public ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int PageSize) {
        PageHelper.startPage(pageNum,PageSize);
        List<Order> orders = ordermapper.selectOrderByUserId(userId);
        List<OrderVo> orderVos = this.assembleOrderVoList(orders,userId);
        PageInfo pageResult = new PageInfo(orders);
        pageResult.setList(orderVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    private  List<OrderVo> assembleOrderVoList(List<Order> orders,Integer userId) {
        List<OrderVo> orderVos = Lists.newArrayList();
        for(Order order: orders) {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if(userId == null) {
                //管理员不需要传userID,将所有订单物品详情取出
                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            }else{
                orderItemList = orderItemMapper.getByUserIdAndOrderNo(userId,order.getOrderNo());
            }
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            orderVos.add(orderVo);

        }
        return orderVos;
    }

    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = ordermapper.selectAllOrder();
        List<OrderVo> orderVos = assembleOrderVoList(orders,null);
        PageInfo pageInfo = new PageInfo(orders);
        pageInfo.setList(orderVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<OrderVo> manageDetail(Long orderNo) {
        Order order = ordermapper.selectByOrder(orderNo);
        if(order != null) {
            List<OrderItem> orderItemList= orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("该订单不存在");
    }

    //二期进行模糊查询，用户姓名，日期等的查询支持，目前只进行了订单号查询，跟上面那个函数一样。
    public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Order order = ordermapper.selectByOrder(orderNo);
        if(order != null) {
            List<OrderItem> orderItemList= orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
            pageResult.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("该订单不存在");
    }

    public ServerResponse<String> manageSendGoods(Long orderNo) {
        Order order = ordermapper.selectByOrder(orderNo);
        if(order != null) {
            if(order.getStatus() == Const.OrderStatus.PAID.getCode())
                order.setStatus(Const.OrderStatus.SHIPPED.getCode());
                order.setSendTime(new Date());
                ordermapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("发货成功");
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    //关闭订单距离现在hour时间的未付款订单
    public void closeOrder(int hour){
        Date deadLine = DateUtils.addHours(new Date(),-hour);
        List<Order> orderList =ordermapper.selectOrderByStatusAndCreatetime(Const.OrderStatus.NO_PAY.getCode(), DateTimeUtil.dateToStr(deadLine) );
        logger.info("将要被关闭的订单" + orderList.toString());
        for (Order order : orderList) {
            List<OrderItem> orderItems = orderItemMapper.getByOrderNo(order.getOrderNo());
            for(OrderItem orderItem : orderItems) {
                //关单前的库存 该查询会加行锁
                Integer count = productMapper.selectStockByProductID(orderItem.getProductId());
                if (count == null)
                    continue;
                Product product = new Product();
                //关单前先把单中的数量加到库存上
                product.setStock(count + orderItem.getQuantity());
                product.setId(orderItem.getProductId());
                productMapper.updateByPrimaryKeySelective(product);
                //删除订单中的物品
                //orderItemMapper.deleteByPrimaryKey(orderItem.getId());
                logger.info("废弃订单中的商品 ："+orderItem.getProductName());
            }
            //更新该订单的状态为关闭
            ordermapper.closeOrderByNo(order.getOrderNo());

        }


    }
}
