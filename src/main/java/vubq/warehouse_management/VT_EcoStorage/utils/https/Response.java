package vubq.warehouse_management.VT_EcoStorage.utils.https;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {

    private int code;
    private String message;
    private Object data;
    private boolean show = true;

    // ==== Static Factory Methods ====

    public static Response success() {
        return new Response(ResponseCode.SUCCESS, "Success", null, true);
    }

    public static Response success(Object data) {
        return new Response(ResponseCode.SUCCESS, "Success", data, true);
    }

    public static Response success(String message, Object data) {
        return new Response(ResponseCode.SUCCESS, message, data, true);
    }

    public static Response error(int code, String message) {
        return new Response(code, message, null, true);
    }

    public static Response error(int code, String message, Object data) {
        return new Response(code, message, data, true);
    }

    public static Response internalError() {
        return new Response(ResponseCode.INTERNAL_ERROR, "Internal server error", null, true);
    }

    public static Response badRequest(String message) {
        return new Response(ResponseCode.BAD_REQUEST, message, null, true);
    }

    public static Response unauthorized(String message) {
        return new Response(ResponseCode.UNAUTHORIZED, message, null, true);
    }

    public static Response forbidden(String message) {
        return new Response(ResponseCode.FORBIDDEN, message, null, true);
    }

    public static Response notFound(String message) {
        return new Response(ResponseCode.NOT_FOUND, message, null, true);
    }

    // ==== Builder-style methods (optional) ====
    public Response code(int code) {
        this.code = code;
        return this;
    }

    public Response message(String message) {
        this.message = message;
        return this;
    }

    public Response data(Object data) {
        this.data = data;
        return this;
    }

    public Response show(boolean show) {
        this.show = show;
        return this;
    }
}
