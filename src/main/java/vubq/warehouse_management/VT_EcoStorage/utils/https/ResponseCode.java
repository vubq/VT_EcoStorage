package vubq.warehouse_management.VT_EcoStorage.utils.https;

public class ResponseCode {
    // Thành công
    public static final int SUCCESS = 0;

    // Xác thực và phân quyền
    public static final int UNAUTHORIZED = 100;        // Không có token / chưa đăng nhập
    public static final int FORBIDDEN = 101;           // Không đủ quyền
    public static final int LOGIN_REQUIRED = 102;      // Yêu cầu đăng nhập lại
    public static final int TOKEN_EXPIRED = 103;       // Token hết hạn
    public static final int TOKEN_INVALID = 104;       // Token không hợp lệ

    // Lỗi nghiệp vụ
    public static final int BUSINESS_ERROR = 200;      // Lỗi nghiệp vụ chung
    public static final int OPERATION_NOT_ALLOWED = 201; // Không cho phép thao tác

    // Lỗi dữ liệu
    public static final int NOT_FOUND = 300;           // Không tìm thấy dữ liệu
    public static final int ALREADY_EXISTS = 301;      // Đã tồn tại
    public static final int VALIDATION_FAILED = 302;   // Dữ liệu không hợp lệ
    public static final int CONFLICT = 303;            // Xung đột dữ liệu (ví dụ update trạng thái sai)

    // Lỗi từ phía client
    public static final int BAD_REQUEST = 400;         // Yêu cầu không hợp lệ
    public static final int METHOD_NOT_ALLOWED = 405;  // Phương thức không được hỗ trợ

    // Lỗi hệ thống
    public static final int INTERNAL_ERROR = 500;      // Lỗi hệ thống
    public static final int DATABASE_ERROR = 501;      // Lỗi khi truy cập database
    public static final int TIMEOUT = 502;             // Quá thời gian xử lý

    // Các lỗi đặc biệt/khác
    public static final int UNKNOWN_ERROR = 9999;      // Lỗi không xác định
}
