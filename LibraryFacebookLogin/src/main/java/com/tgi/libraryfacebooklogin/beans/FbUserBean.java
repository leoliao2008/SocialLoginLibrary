package com.tgi.libraryfacebooklogin.beans;

import java.util.List;

public class FbUserBean extends BaseResponse {

    /**
     * data : {"app_id":138483919580948,"type":"USER","application":"Social Cafe","expires_at":1352419328,"is_valid":true,"issued_at":1347235328,"metadata":{"sso":"iphone-safari"},"scopes":["email","publish_actions"],"user_id":"1207059"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * app_id : 138483919580948
         * type : USER
         * application : Social Cafe
         * expires_at : 1352419328
         * is_valid : true
         * issued_at : 1347235328
         * metadata : {"sso":"iphone-safari"}
         * scopes : ["email","publish_actions"]
         * user_id : 1207059
         */

        private long app_id;
        private String type;
        private String application;
        private int expires_at;
        private boolean is_valid;
        private int issued_at;
        private MetadataBean metadata;
        private String user_id;
        private List<String> scopes;

        public long getApp_id() {
            return app_id;
        }

        public void setApp_id(long app_id) {
            this.app_id = app_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }

        public int getExpires_at() {
            return expires_at;
        }

        public void setExpires_at(int expires_at) {
            this.expires_at = expires_at;
        }

        public boolean isIs_valid() {
            return is_valid;
        }

        public void setIs_valid(boolean is_valid) {
            this.is_valid = is_valid;
        }

        public int getIssued_at() {
            return issued_at;
        }

        public void setIssued_at(int issued_at) {
            this.issued_at = issued_at;
        }

        public MetadataBean getMetadata() {
            return metadata;
        }

        public void setMetadata(MetadataBean metadata) {
            this.metadata = metadata;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public List<String> getScopes() {
            return scopes;
        }

        public void setScopes(List<String> scopes) {
            this.scopes = scopes;
        }

        public static class MetadataBean {
            /**
             * sso : iphone-safari
             */

            private String sso;

            public String getSso() {
                return sso;
            }

            public void setSso(String sso) {
                this.sso = sso;
            }

            @Override
            public String toString() {
                return "MetadataBean{" +
                        "sso='" + sso + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "app_id=" + app_id +
                    ", type='" + type + '\'' +
                    ", application='" + application + '\'' +
                    ", expires_at=" + expires_at +
                    ", is_valid=" + is_valid +
                    ", issued_at=" + issued_at +
                    ", metadata=" + metadata +
                    ", user_id='" + user_id + '\'' +
                    ", scopes=" + scopes +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FbUserBean{" +
                "data=" + data +
                "} " + super.toString();
    }
}
