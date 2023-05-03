package com.dxx.takeOut.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {
    //Serializable接口
    //https://blog.csdn.net/weixin_44209555/article/details/107837108?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522168204752016800182791581%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=168204752016800182791581&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-107837108-null-null.142^v86^insert_down28v1,239^v2^insert_chatgpt&utm_term=Serializable&spm=1018.2226.3001.4187
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)  //插入时自动填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)  //插入和更新时自动填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)  //插入时自动填充字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)  //插入和更新时自动填充字段
    private Long updateUser;

}
