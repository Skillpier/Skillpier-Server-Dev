package com.tenpay;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.tenpay.util.MD5Util;
import com.tenpay.util.TenpayUtil;

/**
 * 应答处理类
 * 应答处理类继承此类，重写isTenpaySign方法即可。
 * @author miklchen
 *
 */
public class ResponseHandler { 
	
	/** 密钥 */
	private String key;
	
	/** 应答的参数 */
	private SortedMap parameters; 
	
	/** debug信息 */
	private String debugInfo;
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private String uriEncoding;
	
	/**
	 * 构造函数
	 * 
	 * @param request
	 * @param response
	 */
	public ResponseHandler(HttpServletRequest request,
			HttpServletResponse response)  {
		this.request = request;
		this.response = response;

		try {
			InputStream is = request.getInputStream();
			String requestXml = readStream(is);
			System.out.println(requestXml);
			Document doc = Jsoup.parse(requestXml);
			String appid = doc.select("appid").text();
			String attach = doc.select("attach").text();
			String bank_type = doc.select("bank_type").text();
			String cash_fee = doc.select("cash_fee").text();
			String fee_type = doc.select("fee_type").text();
			String is_subscribe = doc.select("is_subscribe").text();
			String mch_id = doc.select("mch_id").text();
			String nonce_str = doc.select("nonce_str").text();
			String openid = doc.select("openid").text();
			String product_id = doc.select("product_id").text();
			String out_trade_no = doc.select("out_trade_no").text();
			String result_code = doc.select("result_code").text();
			String return_code = doc.select("return_code").text();
			String sign = doc.select("sign").text();
			String time_end = doc.select("time_end").text();
			String total_fee = doc.select("total_fee").text();
			String trade_type = doc.select("trade_type").text();
			String transaction_id = doc.select("transaction_id").text();
			this.key = "";
			this.parameters = new TreeMap();
			this.debugInfo = "";
			this.uriEncoding = "";
			this.setParameter("appid",appid);
			this.setParameter("attach",attach);
			this.setParameter("bank_type",bank_type);
			this.setParameter("cash_fee",cash_fee);
			this.setParameter("fee_type",fee_type);
			this.setParameter("is_subscribe",is_subscribe);
			this.setParameter("mch_id",mch_id);
			this.setParameter("nonce_str",nonce_str);
			this.setParameter("openid",openid);
			this.setParameter("productid",product_id);
			this.setParameter("out_trade_no",out_trade_no);
			this.setParameter("result_code",result_code);
			this.setParameter("return_code",return_code);
			this.setParameter("sign",sign);
			this.setParameter("time_end",time_end);
			this.setParameter("total_fee",total_fee);
			this.setParameter("trade_type",trade_type);
			this.setParameter("transaction_id",transaction_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}
	/** 
     * @功能 读取流 
     * @param inStream 
     * @return 字节数组 
     * @throws Exception 
     */  
    public static String readStream(InputStream inStream) throws Exception {  
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = -1;  
        while ((len = inStream.read(buffer)) != -1) {  
            outSteam.write(buffer, 0, len);  
        }  
        outSteam.close();  
        inStream.close();  
        return new String(outSteam.toByteArray(),"UTF-8");  
    }  
	/**
	*获取密钥
	*/
	public String getKey() {
		return key;
	}

	/**
	*设置密钥
	*/
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取参数值
	 * @param parameter 参数名称
	 * @return String 
	 */
	public String getParameter(String parameter) {
		String s = (String)this.parameters.get(parameter); 
		return (null == s) ? "" : s;
	}
	
	/**
	 * 设置参数值
	 * @param parameter 参数名称
	 * @param parameterValue 参数值
	 */
	public void setParameter(String parameter, String parameterValue) {
		String v = "";
		if(null != parameterValue) {
			v = parameterValue.trim();
		}
		this.parameters.put(parameter, v);
	}
	
	/**
	 * 返回所有的参数
	 * @return SortedMap
	 */
	public SortedMap getAllParameters() {
		return this.parameters;
	}
	
	/**
	 * 是否财付通签名,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 * @return boolean
	 */
	public boolean isTenpaySign() {
		StringBuffer sb = new StringBuffer();
		Set es = this.parameters.entrySet();
		Iterator it = es.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			String v = (String)entry.getValue();
			if(!"sign".equals(k) && null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}
		
		sb.append("key=" + this.getKey());
		
		//算出摘要
		String enc = TenpayUtil.getCharacterEncoding(this.request, this.response);
		String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toLowerCase();
		
		String tenpaySign = this.getParameter("sign").toLowerCase();
		
		//debug信息
		this.setDebugInfo(sb.toString() + " => sign:" + sign +
				" tenpaySign:" + tenpaySign);
		System.out.println(sb.toString());
		System.out.println("tenpaySign:"+tenpaySign);
		System.out.println("sign:"+sign);
		return tenpaySign.equals(sign);
	}
	
	/**
	 * 返回处理结果给财付通服务器。
	 * @param msg: Success or fail。
	 * @throws IOException 
	 */
	public void sendToCFT(String msg) throws IOException {
		String strHtml = msg;
		PrintWriter out = this.getHttpServletResponse().getWriter();
		out.println(strHtml);
		out.flush();
		out.close();

	}
	
	/**
	 * 获取uri编码
	 * @return String
	 */
	public String getUriEncoding() {
		return uriEncoding;
	}

	/**
	 * 设置uri编码
	 * @param uriEncoding
	 * @throws UnsupportedEncodingException
	 */
	public void setUriEncoding(String uriEncoding)
			throws UnsupportedEncodingException {
		if (!"".equals(uriEncoding.trim())) {
			this.uriEncoding = uriEncoding;

			// 编码转换
			String enc = TenpayUtil.getCharacterEncoding(request, response);
			Iterator it = this.parameters.keySet().iterator();
			while (it.hasNext()) {
				String k = (String) it.next();
				String v = this.getParameter(k);
				v = new String(v.getBytes(uriEncoding.trim()), enc);
				this.setParameter(k, v);
			}
		}
	}

	/**
	*获取debug信息
	*/
	public String getDebugInfo() {
		return debugInfo;
	}
	
	/**
	*设置debug信息
	*/
	protected void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}
	
	protected HttpServletRequest getHttpServletRequest() {
		return this.request;
	}
	
	protected HttpServletResponse getHttpServletResponse() {
		return this.response;
	}
	
}
