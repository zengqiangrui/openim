package indi.qiangrui.openim.dto.msg;

import lombok.Data;

/**
 * @author Johnny
 * @date 2020/7/2
 * @description 文本消息
 */
@Data
public class TextMsg implements IMsg {
    private String content;
}
