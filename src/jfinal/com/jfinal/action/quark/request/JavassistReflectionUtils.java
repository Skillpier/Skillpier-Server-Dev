package com.jfinal.action.quark.request;

import java.util.ArrayList;
import java.util.List;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class JavassistReflectionUtils {

	
	public static void main(String[] args) {
		getMethodInfo("tt", Test.class);
	}
	public static List<MethodParamsInfo> getMethodInfo(String method,Class clazz){
    	List<MethodParamsInfo> list = new ArrayList<MethodParamsInfo>();
        try {  
            ClassPool pool = ClassPool.getDefault();  
            pool.insertClassPath(new ClassClassPath(JavassistReflectionUtils.class));
            CtClass cc = pool.get(clazz.getName());  
            CtMethod cm = cc.getDeclaredMethod(method);  
            // 使用javaassist的反射方法获取方法的参数名  
            MethodInfo methodInfo = cm.getMethodInfo();  
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
            if (attr == null) {  
                // exception  
            }  
            CtClass types [] = cm.getParameterTypes();
            String[] paramNames = new String[cm.getParameterTypes().length]; 
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
            for (int i=0;i<types.length;++i) {
            	MethodParamsInfo info = new MethodParamsInfo();
            	info.setParamType(types[i].getName());//参数类型
            	info.setParamName(attr.variableName(i + pos));
				list.add(info); 
			}
        } catch (NotFoundException e) {  
            e.printStackTrace();  
        }  
        return list;
	}
}
