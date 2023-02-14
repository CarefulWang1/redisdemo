package com.wuwei.redisdemo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "xx_user")
public class User  implements Serializable {   // User   user
    // @TableField("user_id")
    private Integer userId;  // user_id

    private String userName;

    private String  password;
//com.wuwei.redisdemo.po.User

}
