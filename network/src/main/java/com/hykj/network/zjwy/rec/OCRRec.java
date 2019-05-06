package com.hykj.network.zjwy.rec;

/**
 * created by cjf
 * on:2019/5/6 10:44
 * 云脉的数据接收
 */
public class OCRRec<T> {
    /*-90	无接口权限
-91	余额不足
-92	账号被冻结
-93	账号已被删除
-100	API账号或密码错误
-101	上传文件失败
-102	文件上传太大
-110	识别失败。原因：1、用户上传图片存在模糊曝光等问题；2、用户上传非银行卡照片；3:服务端识别引擎出现异常
-117	账号超过15天试用期或试用期内当天可识别次数已达上限
-118	账户余额为0或可识别次数为0*/
    private String status;// OK 成功
    private String info;//当status不是OK的时候，显示的报错提示
    private T data;

    public String getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }

    public T getData() {
        return data;
    }
}
