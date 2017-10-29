package org.jackframework.testservice.pojo;


public class TRoleFunction {

    private Long roleFunctionId;

    private Long roleId;

    private Long functionId;

    private Boolean enable;

    private Boolean editable;

    public void setRoleFunctionId(Long roleFunctionId) {
        this.roleFunctionId = roleFunctionId;
    }

    public Long getRoleFunctionId() {
        return roleFunctionId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getEditable() {
        return editable;
    }


}