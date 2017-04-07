package com.quarkso.utils;

import java.awt.Color;
import java.util.Random;

/**
 * 
 * @author kingsley 生成动态验证码类
 * 
 */
public class IdentifyCode {
	private Random random = new Random();

	public Color getBack() {
		return new Color(random.nextInt(255), random.nextInt(255),
				random.nextInt(255));
	}

	/**
	 * @desc 生成颜色的反色
	 * @author aj
	 * @date 2011-3-30
	 * */
	public Color getFront(Color c) {
		return new Color(0, 0, 0);
	}

	/**
	 * @desc 产生随机字符
	 * @author aj
	 * @date 2011-3-30
	 * */
	public String getString() {
		String old = "123456789abcdefghijkmnpqrstuvwxyz"; // 验证图片上面的随机字符
		StringBuffer sb = new StringBuffer();
		int j = 0;
		for (int i = 0; i < 4; i++) {
			j = random.nextInt(old.length());
			sb.append(old.substring(j, j + 1));
		}
		return sb.toString();
	}
}
