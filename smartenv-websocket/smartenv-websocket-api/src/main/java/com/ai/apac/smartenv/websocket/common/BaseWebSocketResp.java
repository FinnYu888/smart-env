package com.ai.apac.smartenv.websocket.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springblade.core.tool.api.IResultCode;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Optional;

/**
 * 统一Websocket API响应结果封装
 *
 * @author Chill
 */
@Getter
@Setter
@ToString
@ApiModel(description = "WebSocket返回信息")
@NoArgsConstructor
public class BaseWebSocketResp<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "状态码", required = true)
    private int code;
    @ApiModelProperty(value = "是否成功", required = true)
    private boolean success;
    @ApiModelProperty(value = "承载数据")
    private T data;
    @ApiModelProperty(value = "返回消息", required = true)
    private String msg;

    private BaseWebSocketResp(IResultCode resultCode) {
        this(resultCode, null, resultCode.getMessage());
    }

    private BaseWebSocketResp(IResultCode resultCode, String msg) {
        this(resultCode, null, msg);
    }

    private BaseWebSocketResp(IResultCode resultCode, T data) {
        this(resultCode, data, resultCode.getMessage());
    }

    private BaseWebSocketResp(IResultCode resultCode, T data, String msg) {
        this(resultCode.getCode(), data, msg);
    }

    private BaseWebSocketResp(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.success = ResultCode.SUCCESS.getCode() == code;
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isSuccess(@Nullable BaseWebSocketResp<?> result) {
        return Optional.ofNullable(result)
                .map(x -> ObjectUtil.nullSafeEquals(ResultCode.SUCCESS.getCode(), x.code))
                .orElse(Boolean.FALSE);
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isNotSuccess(@Nullable BaseWebSocketResp<?> result) {
        return !BaseWebSocketResp.isSuccess(result);
    }

    /**
     * 返回R
     *
     * @param data 数据
     * @param <T>  T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> data(T data) {
        return data(data, BladeConstant.DEFAULT_SUCCESS_MESSAGE);
    }

    /**
     * 返回R
     *
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> data(T data, String msg) {
        return data(HttpServletResponse.SC_OK, data, msg);
    }

    /**
     * 返回R
     *
     * @param code 状态码
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> data(int code, T data, String msg) {
        return new BaseWebSocketResp<>(code, data, data == null ? BladeConstant.DEFAULT_NULL_MESSAGE : msg);
    }

    /**
     * 返回R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> success(String msg) {
        return new BaseWebSocketResp<>(ResultCode.SUCCESS, msg);
    }

    /**
     * 返回R
     *
     * @param resultCode 业务代码
     * @param <T>        T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> success(IResultCode resultCode) {
        return new BaseWebSocketResp<>(resultCode);
    }

    /**
     * 返回R
     *
     * @param resultCode 业务代码
     * @param msg        消息
     * @param <T>        T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> success(IResultCode resultCode, String msg) {
        return new BaseWebSocketResp<>(resultCode, msg);
    }

    /**
     * 返回R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> fail(String msg) {
        return new BaseWebSocketResp<>(ResultCode.FAILURE, msg);
    }


    /**
     * 返回R
     *
     * @param code 状态码
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> fail(int code, String msg) {
        return new BaseWebSocketResp<>(code, null, msg);
    }

    /**
     * 返回R
     *
     * @param resultCode 业务代码
     * @param <T>        T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> fail(IResultCode resultCode) {
        return new BaseWebSocketResp<>(resultCode);
    }

    /**
     * 返回R
     *
     * @param resultCode 业务代码
     * @param msg        消息
     * @param <T>        T 泛型标记
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> fail(IResultCode resultCode, String msg) {
        return new BaseWebSocketResp<>(resultCode, msg);
    }

    /**
     * 返回R
     *
     * @param flag 成功状态
     * @return BaseWebSocketResp
     */
    public static <T> BaseWebSocketResp<T> status(boolean flag) {
        return flag ? success(BladeConstant.DEFAULT_SUCCESS_MESSAGE) : fail(BladeConstant.DEFAULT_FAILURE_MESSAGE);
    }
}
