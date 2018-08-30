package com.tig.libraysociallogins.amazon.bean;

public class AmazonUserProfile extends AmazonBaseResponse  {

    /**
     * user_id : amznl.account.K2LI23KL2LK2
     * email : mhashimoto-04@plaxo.com
     * name : Mork Hashimoto
     * postal_code : 98052
     * error : machine-readable error code
     * error_description : human-readable error description
     * request_id : bef0c2f8-e292-4l96-8c95-8833fbd559df
     */

    private String user_id;
    private String email;
    private String name;
    private String postal_code;
    private String request_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }


    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    @Override
    public String toString() {
        return "AmazonUserProfile{" +
                "user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", postal_code='" + postal_code + '\'' +
                ", request_id='" + request_id + '\'' +
                "} " + super.toString();
    }
}
