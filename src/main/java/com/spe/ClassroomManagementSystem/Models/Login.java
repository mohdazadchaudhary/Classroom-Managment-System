package com.spe.ClassroomManagementSystem.Models;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="login")
public class Login implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long loginId;

    @Column
    @NotNull
    private String userType;

    @Column
    @NotNull
    private String userName;

    @Column
    @NotNull
    private String password;

    /** Account lifecycle status: "pending" | "approved" | "rejected" */
    @Column(nullable = false)
    private String status = "approved";

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL,mappedBy="foreignId")
    private Professor professor;

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL,mappedBy="foreignId")
    private TA ta;

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL,mappedBy="foreignId")
    private Committee committee;

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL,mappedBy="foreignId")
    private Sac sac;

    public Login() {
    }

    public Login(@NotNull String userType, @NotNull String userName, @NotNull String password) {
        this.userType = userType;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Constructor for self-registered users (status defaults to "pending").
     *
     * @param userType the role (professor, ta, sac, committee)
     * @param userName the chosen username
     * @param password the chosen password
     * @param status   the initial account status
     */
    public Login(@NotNull String userType, @NotNull String userName, @NotNull String password,
                 @NotNull String status) {
        this.userType = userType;
        this.userName = userName;
        this.password = password;
        this.status = status;
    }

//    @Override
//    public String toString() {
//        return "Login{" +
//                "loginId=" + loginId +
//                ", userType='" + userType + '\'' +
//                ", userName='" + userName + '\'' +
//                ", password='" + password + '\'' +
//                ", professor=" + professor +
//                ", ta=" + ta +
//                ", committee=" + committee +
//                ", sac=" + sac +
//                '}';
//    }

    public long getLoginId() {
        return loginId;
    }

    public void setLoginId(long loginId) {
        this.loginId = loginId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public TA getTa() {
        return ta;
    }

    public void setTa(TA ta) {
        this.ta = ta;
    }

    public Committee getCommittee() {
        return committee;
    }

    public void setCommittee(Committee committee) {
        this.committee = committee;
    }

    public Sac getSac() {
        return sac;
    }

    public void setSac(Sac sac) {
        this.sac = sac;
    }
}
