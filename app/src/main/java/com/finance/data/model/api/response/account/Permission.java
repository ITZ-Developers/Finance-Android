package com.finance.data.model.api.response.account;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable {
    private Long id;
    private String name;
    private String action;
    private String nameGroup;
    private String permissionCode;

    public static Boolean checkPermission(String code, List<Permission> permissionList){
        if(permissionList == null || permissionList.isEmpty()){
            return false;
        }
        for(Permission permission: permissionList){
            if(permission.getPermissionCode().equals(code)){
                return true;
            }
        }
        return false;
    }
}
