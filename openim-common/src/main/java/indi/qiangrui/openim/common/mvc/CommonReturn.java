package indi.qiangrui.openim.common.mvc;

/**
 * @author Johnny
 * @date 2020/7/2
 * @description 用于统一参数的返回
 */
public abstract class CommonReturn<T> {

    private Integer code;

    private String message;

    private T data;

}
