package mc21.tgi.com.googlesociallogindemo.bean;

public class TgiUser {
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
    long id;
    String firstName;
    String lastName;
    String email;
    String displayName;
    String avatar;
    int gender;
    String ageGroup;
    String birthday;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "TgiUser{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender=" + gender +
                ", ageGroup='" + ageGroup + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
