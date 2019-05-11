package com.lxc.mall2.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by 82138 on 2018/8/12.
 */
public class Const {
    public static final String CURRENT_USER = "currentuser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }
    public interface Cart {
        Integer CHECKED = 1;//购物车选中状态
        Integer UN_CHECKED = 0;//购物车未选中

        String LIMIT_SUCCESS = "限制购物车商品数量成功";
        String LIMIT_FAILED = "限制购物车商品数量失败";
    }
    public interface RedisCacheExtime{

        int REDIS_SESSION_EXTIME = 60*30;

    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");

    }
    public enum ProductStatusEnum{
        ON_SALE(1,"ONSALE");

        private int code;
        private String desc;

        ProductStatusEnum(int code,String desc){
            this.code=code;
            this.desc=desc;
        }
        public String getDesc() {
            return desc;
        }
        public int getCode() {
            return code;
        }

    }

    public enum OrderStatus{
        CANCELED(0,"订单取消")
        ,NO_PAY(10,"未付款")
        ,PAID(20,"已付款")
        ,SHIPPED(40,"已发货")
        ,ORDER_SUCCESS(50,"订单完成")
        ,ORDER_CLOSE(60,"订单关闭");

        private int code;
        private String value;
        private OrderStatus(int code,String value) {
            this.code= code;
            this.value = value;
        }
        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static OrderStatus getOrderStauts(Integer code) {
            for(OrderStatus orderStatus: values()) {
                if(code == orderStatus.getCode())
                    return orderStatus;
            }
            throw new RuntimeException("对应订单状态未找到");
        }

    }
    public interface AlipayCallBack{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "false";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        private int code;
        private String value;

        PayPlatformEnum(int code,String value) {
            this.code = code;
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }


    }
    public enum PaymentTypeEnum {
        ONLINE_PAY(1,"在线支付");

        private final Integer code;
        private final String desc;
        private PaymentTypeEnum(Integer paymentTypeId,String desc) {
            this.code = paymentTypeId;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static String getPaymentTypeDesc(Integer code) {
            for(PaymentTypeEnum paymentTypeEnum : values()) {
                if(code == paymentTypeEnum.getCode())
                    return paymentTypeEnum.getDesc();
            }
            throw new RuntimeException("对应支付类型未找到");
        }
    }


    public interface RedisLock{
        public static final String REDIS_LOCK_KEY = "REDIS_LOCK_KEY";
    }
}
