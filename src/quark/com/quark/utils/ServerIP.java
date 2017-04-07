/**
 * 
 */
package com.quark.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kingsley
 * 
 *         2014年7月24日
 */
public class ServerIP {

	/**
	 * 在JSP里，获取客户端的IP地址的方法是：request.getRemoteAddr()，这种方法在大部分情况下都是有效的。
	 * 但是在通过了Apache
	 * ,Squid等反向代理软件就不能获取到客户端的真实IP地址了。如果使用了反向代理软件，用request.getRemoteAddr
	 * ()方法获取的IP地址是：127.0.0.1或192.168.1.110或公网IP，而并不是客户端的真实IP。
	 * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
	 * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。如： X-Forwarded-For：192.168.1.110,
	 * 192.168.1.120, 192.168.1.130, 192.168.1.100 用户真实IP为： 192.168.1.110
	 * 
	 * @param request
	 * @return
	 */
	public static String getRealIPAddr(HttpServletRequest request) {
		/*String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)
				|| "null".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)
				|| "null".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)
				|| "null".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}*/
		return request.getRemoteAddr();
	}
}
