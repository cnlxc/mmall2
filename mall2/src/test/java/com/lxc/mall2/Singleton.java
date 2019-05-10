package com.lxc.mall2;

/**
 * Created by 82138 on 2019/5/3.
 */
public class Singleton {
    public static void main(String[] args) {
        Singleton.method();
    }
    private Singleton(){}
    public Object getInstance(){
        return DefalutObject.singleton;
    }

    public static void method(){
        System.out.println("这里是外部类的方法");
    }
    private static class DefalutObject{
        public DefalutObject(){
            System.out.println("这里是内部类的构造器");
        }

        static final Object singleton = getInstance();

        //加载单例对象
        private static Object getInstance(){
            System.out.println("这里内部类的单例对象加载方法");
            return new Object();
        }

    }
}
