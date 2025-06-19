package vubq.warehouse_management.VT_EcoStorage.utils.https;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataTableResponse {

    private Integer code;
    private Object items;
    private Long totalRows;

    // ==== Static Factory Methods ====

    public static DataTableResponse success() {
        return new DataTableResponse(ResponseCode.SUCCESS, null, 0L);
    }

    public static DataTableResponse success(Object items, Long totalRows) {
        return new DataTableResponse(ResponseCode.SUCCESS, items, totalRows);
    }

    public static DataTableResponse error(int code) {
        return new DataTableResponse(code, null, 0L);
    }

    public static DataTableResponse error(int code, Object items, Long totalRows) {
        return new DataTableResponse(code, items, totalRows);
    }

    public static DataTableResponse internalError() {
        return new DataTableResponse(ResponseCode.INTERNAL_ERROR, null, 0L);
    }

    public static DataTableResponse badRequest() {
        return new DataTableResponse(ResponseCode.BAD_REQUEST, null, 0L);
    }

    public static DataTableResponse notFound() {
        return new DataTableResponse(ResponseCode.NOT_FOUND, null, 0L);
    }

    // ==== Builder-style methods ====

    public DataTableResponse code(int code) {
        this.code = code;
        return this;
    }

    public DataTableResponse items(Object items) {
        this.items = items;
        return this;
    }

    public DataTableResponse totalRows(Long totalRows) {
        this.totalRows = totalRows;
        return this;
    }
}
