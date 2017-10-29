package org.jackframework.testservice.pojo;


public class TFunction {

    private Long functionId;

    private String functionName;

    private String functionRoute;

    private String includeRoutePatterns;

    private String excludeRoutePatterns;

    private String nodeCode;

    private String nodePcode;

    private Integer nodeLevel;

    private Integer nodeOrdinal;

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionRoute(String functionRoute) {
        this.functionRoute = functionRoute;
    }

    public String getFunctionRoute() {
        return functionRoute;
    }

    public void setIncludeRoutePatterns(String includeRoutePatterns) {
        this.includeRoutePatterns = includeRoutePatterns;
    }

    public String getIncludeRoutePatterns() {
        return includeRoutePatterns;
    }

    public void setExcludeRoutePatterns(String excludeRoutePatterns) {
        this.excludeRoutePatterns = excludeRoutePatterns;
    }

    public String getExcludeRoutePatterns() {
        return excludeRoutePatterns;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodePcode(String nodePcode) {
        this.nodePcode = nodePcode;
    }

    public String getNodePcode() {
        return nodePcode;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public Integer getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeOrdinal(Integer nodeOrdinal) {
        this.nodeOrdinal = nodeOrdinal;
    }

    public Integer getNodeOrdinal() {
        return nodeOrdinal;
    }


}