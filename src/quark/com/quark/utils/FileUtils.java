package com.quark.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.imageio.ImageIO;

import com.jfinal.upload.UploadFile;
import com.quark.app.bean.ImgErToFileUtil;
import com.quark.common.config;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class FileUtils {

	public static void save(String file, String data) {
		FileWriter fwriter = null;
		File f = new File(file);
		try {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			fwriter = new FileWriter(f);
			fwriter.write(data);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
				fwriter = null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void SaveWithEncode(String file, String data, String encode) {
		OutputStreamWriter fwriter = null;
		try {
			File f = new File(file);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			fwriter = new OutputStreamWriter(new FileOutputStream(f), encode);
			fwriter.write(data);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
				fwriter = null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static boolean fileIsExist(StringBuffer pathname) {
		return new File(pathname.toString()).exists();
	}

	/**
	 * 复制文件或文件夹
	 * 
	 * @param srcPath
	 * @param destDir
	 *            目标文件所在的目录
	 * @return
	 */
	public static boolean copyGeneralFile(String srcPath, String destDir) {
		boolean flag = false;
		File file = new File(srcPath);
		if (!file.exists()) {
			System.out.println("源文件或源文件夹不存在!");
			return false;
		}
		if (file.isFile()) { // 源文件
			System.out.println("下面进行文件复制!");
			flag = copyFile(srcPath, destDir);
		} else if (file.isDirectory()) {
			System.out.println("下面进行文件夹复制!");
			flag = copyDirectory(srcPath, destDir);
		}

		return flag;
	}

	/**
	 * 复制文件
	 * 
	 * @param srcPath
	 *            源文件绝对路径
	 * @param destDir
	 *            目标文件所在目录
	 * @return boolean
	 */
	private static boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件不存在
			System.out.println("源文件不存在");
			return false;
		}
		// 获取待复制文件的文件名
		String fileName = srcPath.substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;
		if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
			System.out.println("源文件路径和目标文件路径重复!");
			return false;
		}
		File destFile = new File(destPath);
		if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
			System.out.println("目标目录下已有同名文件!");
			return false;
		}

		File destFileDir = new File(destDir);
		destFileDir.mkdirs();
		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();

			flag = true;
		} catch (IOException e) {
			//
		}

		if (flag) {
			System.out.println("复制文件成功!");
		}

		return flag;
	}

	/**
	 * 
	 * @param srcPath
	 *            源文件夹路径
	 * @param destPath
	 *            目标文件夹所在目录
	 * @return
	 */
	private static boolean copyDirectory(String srcPath, String destDir) {
		System.out.println("复制文件夹开始!");
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件夹不存在
			System.out.println("源文件夹不存在");
			return false;
		}
		// 获得待复制的文件夹的名字，比如待复制的文件夹为"E://dir"则获取的名字为"dir"
		String dirName = getDirName(srcPath);
		// 目标文件夹的完整路径
		String destPath = destDir + File.separator + dirName;
		// System.out.println("目标文件夹的完整路径为：" + destPath);

		if (destPath.equals(srcPath)) {
			System.out.println("目标文件夹与源文件夹重复");
			return false;
		}
		File destDirFile = new File(destPath);
		if (destDirFile.exists()) { // 目标位置有一个同名文件夹
			System.out.println("目标位置已有同名文件夹!");
			return false;
		}
		destDirFile.mkdirs(); // 生成目录

		File[] fileList = srcFile.listFiles(); // 获取源文件夹下的子文件和子文件夹
		if (fileList.length == 0) { // 如果源文件夹为空目录则直接设置flag为true，这一步非常隐蔽，debug了很久
			flag = true;
		} else {
			for (File temp : fileList) {
				if (temp.isFile()) { // 文件
					flag = copyFile(temp.getAbsolutePath(), destPath);
				} else if (temp.isDirectory()) { // 文件夹
					flag = copyDirectory(temp.getAbsolutePath(), destPath);
				}
				if (!flag) {
					break;
				}
			}
		}

		if (flag) {
			System.out.println("复制文件夹成功!");
		}

		return flag;
	}

	/**
	 * 获取待复制文件夹的文件夹名
	 * 
	 * @param dir
	 * @return String
	 */
	private static String getDirName(String dir) {
		if (dir.endsWith(File.separator)) { // 如果文件夹路径以"//"结尾，则先去除末尾的"//"
			dir = dir.substring(0, dir.lastIndexOf(File.separator));
		}
		return dir.substring(dir.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param path
	 *            待删除的文件的绝对路径
	 * @return boolean
	 */
	public static boolean deleteGeneralFile(String path) {
		boolean flag = false;

		File file = new File(path);
		if (!file.exists()) { // 文件不存在
			System.out.println("要删除的文件不存在！");
		}

		if (file.isDirectory()) { // 如果是目录，则单独处理
			flag = deleteDirectory(file.getAbsolutePath());
		} else if (file.isFile()) {
			flag = deleteFile(file);
		}

		if (flag) {
			System.out.println("删除文件或文件夹成功!");
		}

		return flag;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 * @return boolean
	 */
	private static boolean deleteFile(File file) {
		return file.delete();
	}

	/**
	 * 删除目录及其下面的所有子文件和子文件夹，注意一个目录下如果还有其他文件或文件夹
	 * 则直接调用delete方法是不行的，必须待其子文件和子文件夹完全删除了才能够调用delete
	 * 
	 * @param path
	 *            path为该目录的路径
	 */
	private static boolean deleteDirectory(String path) {
		boolean flag = true;
		File dirFile = new File(path);
		if (!dirFile.isDirectory()) {
			return flag;
		}
		File[] files = dirFile.listFiles();
		for (File file : files) { // 删除该文件夹下的文件和文件夹
			// Delete file.
			if (file.isFile()) {
				flag = deleteFile(file);
			} else if (file.isDirectory()) {// Delete folder
				flag = deleteDirectory(file.getAbsolutePath());
			}
			if (!flag) { // 只要有一个失败就立刻不再继续
				break;
			}
		}
		flag = dirFile.delete(); // 删除空目录
		return flag;
	}

	/**
	 * 由上面方法延伸出剪切方法：复制+删除
	 * 
	 * @param destDir
	 *            同上
	 */
	public static boolean cutGeneralFile(String srcPath, String destDir) {
		if (!copyGeneralFile(srcPath, destDir)) {
			System.out.println("复制失败导致剪切失败!");
			return false;
		}
		if (!deleteGeneralFile(srcPath)) {
			System.out.println("删除源文件(文件夹)失败导致剪切失败!");
			return false;
		}

		System.out.println("剪切成功!");
		return true;
	}

	/**
	 * 文件重命名
	 * 
	 * @param translation_file
	 * @return
	 */
	public static String renameToFile(UploadFile translation_file,float width,float height ) {
		String ram = "";
		for (int i = 0; i < 6; i++) {
			int x = (int) (Math.random() * 10);
			ram = ram + x;
		}
		String extName = "";
		String filePath = "";
		String fileName = "";// 新文件名称
		File file = translation_file.getFile();
		extName = getFileExt(file.getName());
		filePath = translation_file.getSaveDirectory();
		fileName = System.currentTimeMillis()+ram + extName;
		file.renameTo(new File(filePath + fileName));
		// 压缩图片
		try {
			compressImage(config.save_path + fileName,width,height);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	/**
	 * 文件重命名
	 * 
	 * @param translation_file
	 * @return
	 */
	public static String renameToFile2(String fileName2,float width,float height ) {
		String fileName = fileName2;// 新文件名称
		// 压缩图片
		try {
			compressImage(config.save_path + fileName2,width,height);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	/**
	 * 文件重命名
	 * 
	 * @param translation_file
	 * @return
	 */
	public static String renameToFileH52(UploadFile image_01) {
		String fileName = "";
		String ram = "";
		for (int i = 0; i < 6; i++) {
			int x = (int) (Math.random() * 10);
			ram = ram + x;
		}
		String contentType = image_01.getContentType();
		System.out.println(contentType+"===contentType");
		if(contentType.startsWith("image/")){
			String suffix = "."+contentType.substring(contentType.indexOf("/")+1);
			fileName = image_01.getFileName()+System.currentTimeMillis()+ram;
			if(!fileName.contains(suffix)){
				image_01.getFile().renameTo(new File(image_01.getSaveDirectory() + File.separator + fileName + suffix));
			}
			fileName = fileName+suffix;
		}
		if(contentType.startsWith("application/")){
			fileName="example_7.png";
		}
		return fileName;
	}
	/**
	 * 文件重命名
	 * 
	 * @param translation_file
	 * @return
	 */
	public static String renameToFileH5(UploadFile translation_file,String fileName,float width,float height ) {
		String filePath = "";
		File file = translation_file.getFile();
		filePath = translation_file.getSaveDirectory();
		file.renameTo(new File(filePath + fileName));
		// 压缩图片
		try {
			compressImage(config.save_path + fileName,width,height);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}
	/**
	 * 获取文件后缀
	 * 
	 * @param @param
	 *            fileName
	 * @param @return
	 *            设定文件
	 * @return String 返回类型
	 */
	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.'), fileName.length());
	}

	/**
	 * 单独删除文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		File f = new File(path); // 输入要删除的文件位置
		if (f.exists()) {
			boolean delete = f.delete();
			return delete;
		} else {
			return false;
		}
	}

	/**
	 * 图片压缩
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws IOException
	 *             图片超过500KB就压缩
	 */
	public static void compressImage(String path,float width,float height) throws FileNotFoundException, IOException {
		File picture = new File(path);
		BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
		double image_size = picture.length() / 1024.0;
		System.out.println("图片大小："+String.format("%.1f", picture.length() / 1024.0));// kb
		if (image_size > 900) {
			createThumbnail(path, path, width, height);
		}
	}

	/**
	 * 创建图片缩略图(等比缩放)
	 * 
	 * @param src
	 *            源图片文件完整路径
	 * @param dist
	 *            目标图片文件完整路径
	 * @param width
	 *            缩放的宽度
	 * @param height
	 *            缩放的高度
	 */
	public static void createThumbnail(String src, String dist, float width, float height) {
		try {
			File srcfile = new File(src);
			if (!srcfile.exists()) {
				System.out.println("文件不存在");
				return;
			}
			BufferedImage image = ImageIO.read(srcfile);

			// 获得缩放的比例
			double ratio = 1.0;
			// 判断如果高、宽都不大于设定值，则不处理
			if (image.getHeight() > height || image.getWidth() > width) {
				if (image.getHeight() > image.getWidth()) {
					ratio = height / image.getHeight();
				} else {
					ratio = width / image.getWidth();
				}
			}
			// 计算新的图面宽度和高度
			int newWidth = (int) (image.getWidth() * ratio);
			int newHeight = (int) (image.getHeight() * ratio);

			BufferedImage bfImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			bfImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0,
					null);

			FileOutputStream os = new FileOutputStream(dist);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
			encoder.encode(bfImage);
			os.close();
			System.out.println("创建缩略图成功");
		} catch (Exception e) {
			System.out.println("创建缩略图发生异常" + e.getMessage());
		}
	}
	/** 
     * 复制单个文件 
     *  
     * @param srcFileName 
     *            待复制的文件名 
     * @param descFileName 
     *            目标文件名 
     * @param overlay 
     *            如果目标文件存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
    public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) {  
        File srcFile = new File(srcFileName);  
  
        // 判断源文件是否存在  
        if (!srcFile.exists()) {  
            return false;  
        } else if (!srcFile.isFile()) {  
            return false;  
        }  
  
        // 判断目标文件是否存在  
        File destFile = new File(destFileName);  
        if (destFile.exists()) {  
            // 如果目标文件存在并允许覆盖  
            if (overlay) {  
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
                new File(destFileName).delete();  
            }  
        } else {  
            // 如果目标文件所在目录不存在，则创建目录  
            if (!destFile.getParentFile().exists()) {  
                // 目标文件所在目录不存在  
                if (!destFile.getParentFile().mkdirs()) {  
                    // 复制文件失败：创建目标文件所在目录失败  
                    return false;  
                }  
            }  
        }  
  
        // 复制文件  
        int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return true;  
        } catch (FileNotFoundException e) {  
            return false;  
        } catch (IOException e) {  
            return false;  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    } 
}
