package org.geektimes.projects.user.domain;

import org.geektimes.projects.user.orm.jpa.JpaDemo;
import org.geektimes.projects.user.validator.register.annotation.PasswordValidator;
import org.geektimes.projects.user.validator.register.annotation.PhoneValidator;
import org.hibernate.validator.constraints.Length;
import org.softee.management.annotation.Description;
import org.softee.management.annotation.MBean;
import org.softee.management.annotation.ManagedAttribute;
import org.softee.management.annotation.ManagedOperation;
import org.softee.management.helper.MBeanRegistration;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

import static javax.persistence.GenerationType.AUTO;

/**
 * 用户领域对象
 *
 * @since 1.0
 */
@Entity
@Table(name = "users")
@MBean(objectName= "pojo-agent-user:name=User" )
@Description( "User MBean 由 pojo-mbean 创建" )
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = AUTO)
    //@NotNull
    @NotNull(groups={JpaDemo.class})
    private Long id;

    @Column
    private String name;

    @Column
    //@Max(32)
    //@Min(6)
    //@Pattern(regexp = "^.{6,32}$", message = "密码长度错误，最少6位，最多32位")
    //@Length(min = 6, max = 32)
    @PasswordValidator
    private String password;

    @Column
    private String email;

    @Column
    //@Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号码格式错误")
    //@Length(min = 11, max = 11)
    @PhoneValidator
    private String phoneNumber;

    @ManagedAttribute
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManagedAttribute
    public String getName() {
        return name;
    }

    @ManagedAttribute
    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute
    public String getPassword() {
        return password;
    }

    @ManagedAttribute
    public void setPassword(String password) {
        this.password = password;
    }

    @ManagedAttribute
    public String getEmail() {
        return email;
    }

    @ManagedAttribute
    public void setEmail(String email) {
        this.email = email;
    }

    @ManagedAttribute
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @ManagedAttribute
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    @ManagedOperation
    @Description("获取对应的hash")
    public int hashCode() {
        return Objects.hash(id, name, password, email, phoneNumber);
    }

    @Override
    @ManagedOperation
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    //lzm add 2021-03-16 06:08:21
    public  void  initMBeanServer()  throws  Exception{
        new MBeanRegistration( this ).register();
    }
}
