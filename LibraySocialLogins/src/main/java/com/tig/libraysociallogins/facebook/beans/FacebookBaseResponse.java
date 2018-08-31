package com.tig.libraysociallogins.facebook.beans;

/**
 * In most apps, the best way to handle expired tokens is to capture the error messages
 * thrown by the API. In each case, the API will return an error message, a code and a
 * subcode in a JSON body explaining the nature of the error.
 * For more information on codes and subcodes please see the error code reference doc.
 * <a href="https://developers.facebook.com/docs/graph-api/using-graph-api/error-handling">Click to learn more...<a/>
 */
public class FacebookBaseResponse {
    /**
     * error : {"message":"Message describing the error","type":"OAuthException","code":190,"error_subcode":460,"error_user_title":"A title","error_user_msg":"A message","fbtrace_id":"EJplcsCHuLu"}
     */

    private ErrorBean error;

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public static class ErrorBean {
        /**
         * message : Message describing the error
         * type : OAuthException
         * code : 190
         * error_subcode : 460
         * error_user_title : A title
         * error_user_msg : A message
         * fbtrace_id : EJplcsCHuLu
         */

        /**
         *  A human-readable description of the error.
         */
        private String message;
        private String type;
        /**
         * An error code. Common values are listed below, along with common recovery tactics.
         * <a href="https://developers.facebook.com/docs/graph-api/using-graph-api/error-handling">Official Doc...</a>
         */
        private int code;
        /**
         * Additional information about the error. Common values are listed below.
         * <a href="https://developers.facebook.com/docs/graph-api/using-graph-api/error-handling">Official Doc...</a>
         */
        private int error_subcode;
        /**
         * The title of the dialog, if shown. The language of the message is based on the locale of the API request.
         */
        private String error_user_title;
        /**
         * The message to display to the user. The language of the message is based on the locale of the API request.
         */
        private String error_user_msg;
        /**
         * Internal support identifier. When reporting a bug related to a Graph API call, include the fbtrace_id to help us find log data for debugging.
         */
        private String fbtrace_id;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getError_subcode() {
            return error_subcode;
        }

        public void setError_subcode(int error_subcode) {
            this.error_subcode = error_subcode;
        }

        public String getError_user_title() {
            return error_user_title;
        }

        public void setError_user_title(String error_user_title) {
            this.error_user_title = error_user_title;
        }

        public String getError_user_msg() {
            return error_user_msg;
        }

        public void setError_user_msg(String error_user_msg) {
            this.error_user_msg = error_user_msg;
        }

        public String getFbtrace_id() {
            return fbtrace_id;
        }

        public void setFbtrace_id(String fbtrace_id) {
            this.fbtrace_id = fbtrace_id;
        }

        @Override
        public String toString() {
            return "ErrorBean{" +
                    "message='" + message + '\'' +
                    ", type='" + type + '\'' +
                    ", code=" + code +
                    ", error_subcode=" + error_subcode +
                    ", error_user_title='" + error_user_title + '\'' +
                    ", error_user_msg='" + error_user_msg + '\'' +
                    ", fbtrace_id='" + fbtrace_id + '\'' +
                    '}';
        }
    }


    //    private String errorReason;
//    private String error;
//    private String errorDescription;
//
//    public String getErrorReason() {
//        return errorReason;
//    }
//
//    public void setErrorReason(String errorReason) {
//        this.errorReason = errorReason;
//    }
//
//    public String getError() {
//        return error;
//    }
//
//    public void setError(String error) {
//        this.error = error;
//    }
//
//    public String getErrorDescription() {
//        return errorDescription;
//    }
//
//    public void setErrorDescription(String errorDescription) {
//        this.errorDescription = errorDescription;
//    }
//
//    @Override
//    public String toString() {
//        return "BaseResponse{" +
//                "errorReason='" + errorReason + '\'' +
//                ", error='" + error + '\'' +
//                ", errorDescription='" + errorDescription + '\'' +
//                '}';
//    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "error=" + error +
                '}';
    }
}
