package indi.qiangrui.openim.dto.msg;

import lombok.Data;

/**
 * @author Johnny
 * @date 2020/7/2
 * @description todo 扩充消息体必带的参数
 */
@Data
public class MsgBody<T> {

    private MsgTypeEnum msgType;

    private T body;
}
