package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 支付宝账号
	public static String account = "3380652340@qq.com";
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088221616878995";
	// 商户的私钥
	public static String private_key =  "MIICXAIBAAKBgQCqq/peXEAsQTuL2ETG+YNxHiZx8UG0qGOD7eEd2uQRr7lHdK8JfCIfLN1bE/I4d8Wgnnhctf4y8p/Q9UxgS792WrS7zcOJhmpJRfo4cm4ypvg2cj10asPPyuz5w8DgffZAP8HHswYbMYJPZ/vGhe2KSOieQvwYjfTIL4tofD1c3wIDAQABAoGAKVLSoB49+3kf7dBDdtnbFXxzWotLmW5cCgmCxDPHnPCrL73005sWt+2o+vBtaXa2hxlXkzsNfVB5maKLB5XPmXcq1H7D73C2AKC+3SzVBLJwtw95eGbDk3UL6vlHAmrheIjQDH2hz02nI1LMA9KMw24fSPfwNnmgsfA5p3sVE2ECQQDb5qYCoLu2QY3cNPzHCWKgK/g9Q8EH7XHskgPpb9CalMhcE0zuA4Ea8eAb1JWI6Cu0aNdo7WStW8ORFcOnUTLjAkEAxrB4X42y1AH9tUgS49MCXtycKbz6TavN0n3OuhU+1ljUlekeVQJkqfs4WWSVfQs1v8Gt02Q70UCdnEWi8srC1QJBAM2cRhEi9SEe/mOt78SzE9KsCjpOhcjBzcnjZWD5udUv1H+zVDUgdbEaA/BBv2f5nhPcC74CaiZmLOAVt5FUrP0CQD4ayPVtMDmOicQRTD5EJZiLMpDlTgPbMNGbm3OU0nhjeceF/4Io5lgve1SN2SAl0d4nZvMNgJkOrnU7WwyzDiUCQAQVSBlHz3kNyJL30ELFmVcBxG7aiyzwSBvTNnN5YokoTD/ugtEcwzVEc4KhRvexkfXSMX8rPtGpTiQDf5ht4sE=";
	public static String private_key_pkcs8 = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKqr+l5cQCxBO4vYRMb5g3EeJnHxQbSoY4Pt4R3a5BGvuUd0rwl8Ih8s3VsT8jh3xaCeeFy1/jLyn9D1TGBLv3ZatLvNw4mGaklF+jhybjKm+DZyPXRqw8/K7PnDwOB99kA/wcezBhsxgk9n+8aF7YpI6J5C/BiN9Mgvi2h8PVzfAgMBAAECgYApUtKgHj37eR/t0EN22dsVfHNai0uZblwKCYLEM8ec8KsvvfTTmxa37aj68G1pdraHGVeTOw19UHmZoosHlc+ZdyrUfsPvcLYAoL7dLNUEsnC3D3l4ZsOTdQvq+UcCauF4iNAMfaHPTacjUswD0ozDbh9I9/A2eaCx8DmnexUTYQJBANvmpgKgu7ZBjdw0/McJYqAr+D1DwQftceySA+lv0JqUyFwTTO4DgRrx4BvUlYjoK7Ro12jtZK1bw5EVw6dRMuMCQQDGsHhfjbLUAf21SBLj0wJe3JwpvPpNq83Sfc66FT7WWNSV6R5VAmSp+zhZZJV9CzW/wa3TZDvRQJ2cRaLyysLVAkEAzZxGESL1IR7+Y63vxLMT0qwKOk6FyMHNyeNlYPm51S/Uf7NUNSB1sRoD8EG/Z/meE9wLvgJqJmYs4BW3kVSs/QJAPhrI9W0wOY6JxBFMPkQlmIsykOVOA9sw0Zubc5TSeGN5x4X/gijmWC97VI3ZICXR3idm8w2AmQ6udTtbDLMOJQJABBVIGUfPeQ3IkvfQQsWZVwHEbtqLLPBIG9M2c3liiShMP+6C0RzDNURzgqFG97GR9dIxfys+0alOJAN/mG3iwQ==";

	// 支付宝的公钥，无需修改该值
	public static String ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "C:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";

	// 签名方式 不需修改
	public static String sign_type = "RSA";

}
