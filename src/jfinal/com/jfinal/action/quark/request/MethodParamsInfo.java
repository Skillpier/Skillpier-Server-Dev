/**
 * 
 */
package com.jfinal.action.quark.request;

/**
 * @author kingsley
 *
 */
public class MethodParamsInfo {

	private String paramType;
	private String paramName;
	
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	@Override
	public String toString() {
		return "MethodParamsInfo [paramType=" + paramType + ", paramName="
				+ paramName + "]";
	}
	
	
	
}
