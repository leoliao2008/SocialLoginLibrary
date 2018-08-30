package mc21.tgi.com.googlesociallogindemo.bean;

public class TgiSocialLoginResponse extends TgiResponse {
    //    {
    //        "code":0,
    //            "message":"string",
    //            "data: {...}
    //    }


    //    	"accessToken":"string",
    //                "refreshAccessToken":"string",
    //                "expiresIn":0,
    //                "user": {
    //        "id":0,
    //                "firstName":"string",
    //                "lastName":"string",
    //                "email":"string",
    //                "displayName":"string",
    //                "avatar":"string",
    //                "gender":0,
    //                "ageGroup":"string",
    //                "birthday":"string",
    //    }
    Data data;

    class Data{
        String accessToken;
        String refreshToken;
        long expiresIn;
        TgiUser user;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public TgiUser getUser() {
            return user;
        }

        public void setUser(TgiUser user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "accessToken='" + accessToken + '\'' +
                    ", refreshAccessToken='" + refreshToken + '\'' +
                    ", expiresIn=" + expiresIn +
                    ", user=" + user +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TgiSocialLoginResponse{" +
                "data=" + data +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
