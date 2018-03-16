package com.kingbase.db.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 
 * @author feng
 *
 */
public class JschUtil {
	private static String charset = "UTF-8"; // 设置编码格式

	/**
	 * 连接sftp服务器
	 * 
	 * @param host
	 *            远程主机ip地址
	 * @param port
	 *            sftp连接端口，null 时为默认端口
	 * @param user
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * @throws JSchException
	 */
	public static Session connect(String host, Integer port, String user, String password) throws JSchException {
		Session session = null;
		try {
			JSch jsch = new JSch();
			if (port != null) {
				session = jsch.getSession(user, host, port.intValue());
			} else {
				session = jsch.getSession(user, host);
			}
			session.setPassword(password);
			// 设置第一次登陆的时候提示，可选值:(ask | yes | no)
			session.setConfig("StrictHostKeyChecking", "no");
			// 30秒连接超时
			session.connect(20000);
		} catch (JSchException e) {
			e.printStackTrace();
			System.out.println("SFTPUitl 获取连接发生错误");
			throw e;
		}
		return session;
	}

	public static void execCmd(Session session) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String command = "";
		BufferedReader reader = null;
		Channel channel = null;

		try {
			while ((command = br.readLine()) != null) {
				channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);
				channel.setInputStream(null);
				((ChannelExec) channel).setErrStream(System.err);
				channel.connect();
				InputStream in = channel.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charset)));
				String buf = null;
				while ((buf = reader.readLine()) != null) {
					System.out.println(buf);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			channel.disconnect();
			session.disconnect();
		}
	}

	public static ChannelExec execCommand(Session session, String command) {
		ChannelExec openChannel = null;
		try {
			openChannel = (ChannelExec) session.openChannel("exec");
			openChannel.setCommand(command);
			openChannel.connect();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return openChannel;
	}
	/**
	 * 执行命令查看是否成功
	 * 
	 * */
	public static int execCommand1(Session session, String command) {
		ChannelExec openChannel = null;
		int returnSuccess = 0;
		try {
			openChannel = (ChannelExec) session.openChannel("exec");
			openChannel.setCommand(command);
			openChannel.connect();
			returnSuccess = returnSuccess(openChannel, 0);
			openChannel.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return returnSuccess;
	}

	public static ChannelSftp sftp(Session session) {
		ChannelSftp sftp = null;
		try {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return sftp;
	}

	public static void main(String[] args) {

		Session session = null;
		ChannelSftp sftp = null;
		try {
			session = JschUtil.connect("192.168.2.97", 22, "root", "0000");
			System.out.println("=============");
			JschUtil.execCommand1(session, "mkdir -p /home/jpliu/ppp111/k111j");
			System.out.println("=======22222222======");
//			Channel channel = session.openChannel("sftp");
//			channel.connect();
//			sftp = (ChannelSftp) channel;
//			JschUtil.upload("/home/feng/kingbase/", "E:\\aaaaa\\db.zip", sftp);
//
//			ChannelExec openChannel = (ChannelExec) session.openChannel("exec");
//			openChannel.setCommand("cd /home/feng/kingbase/;unzip db.zip");
//			openChannel.connect();
//
//			// openChannel.setCommand("unzip db.zip");
//			// openChannel.connect();
//			InputStream in = openChannel.getInputStream();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//			String buf = null;
//			while ((buf = reader.readLine()) != null) {
//				System.out.println(buf);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sftp != null)
				sftp.disconnect();
			if (session != null)
				session.disconnect();
		}
	}

	/**
	 * sftp上传文件(夹)
	 * 
	 * @param directory
	 * @param uploadFile
	 * @param sftp
	 * @throws Exception
	 */
	public static void upload(String directory, String uploadFile, ChannelSftp sftp) throws Exception {
		System.out.println("sftp upload file [directory] : " + directory);
		System.out.println("sftp upload file [uploadFile] : " + uploadFile);
		File file = new File(uploadFile);
		if (file.exists()) {
			// 这里有点投机取巧，因为ChannelSftp无法去判读远程linux主机的文件路径,无奈之举
			try {
				Vector content = sftp.ls(directory);
				if (content == null) {
					sftp.mkdir(directory);
				}
			} catch (SftpException e) {
				sftp.mkdir(directory);
			}
			// 进入目标路径
			sftp.cd(directory);
			if (file.isFile()) {
				InputStream ins = new FileInputStream(file);
				// 中文名称的
				sftp.put(ins, new String(file.getName().getBytes(), "UTF-8"));
				// sftp.setFilenameEncoding("UTF-8");
			} else {
				File[] files = file.listFiles();
				for (File file2 : files) {
					String dir = file2.getAbsolutePath();
					if (file2.isDirectory()) {
						String str = dir.substring(dir.lastIndexOf(file2.separator));
						directory = directory + "/" + str;
					}
					upload(directory, dir, sftp);
				}
			}
		}
		sftp.disconnect();
	}
	/**
	 * 获取正确返回值
	 * 
	 * */
	public static String returnInputStream(ChannelExec channel) {
		InputStream in = null;
		StringBuffer buf = new StringBuffer();
		try {
			in = channel.getInputStream();
			String line = "";
			InputStreamReader stream = new InputStreamReader(in);
			BufferedReader read = new BufferedReader(stream);
			while((line=read.readLine()) != null){
				buf.append(line+"\n");
			}
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(in!=null){
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buf.toString();
	}
	/**
	 * 获取正确返回值
	 * 
	 * */
	public static String returnInputStream1(ChannelExec channel) {
		if (channel == null) {
			return "";
		}
		StringBuffer strBuffer = new StringBuffer(); // 执行SSH返回的结果
		byte[] tmp = new byte[1024]; // 读数据缓存
		try {
			InputStream in = channel.getInputStream();
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					strBuffer.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					if(in.available()>0) continue;
					break;
				}
				try {
					Thread.sleep(10);
				} catch (Exception ee) {
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmp = null;
		return strBuffer.toString();
	}
	/**
	 * 获取错误返回值
	 * 
	 * */
	public static String returnStream(ChannelExec channel) {
		StringBuffer strBuffer = new StringBuffer(); // 执行SSH返回的结果
		byte[] tmp = new byte[1024]; // 读数据缓存
		try {
//			InputStream in = channel.getInputStream();
			InputStream err = channel.getErrStream();
			InputStream ext = channel.getExtInputStream();

			// 开始获得SSH命令的结果
			while (true) {
				// 获得错误输出
				while (err.available() > 0) {
					int i = err.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					strBuffer.append(new String(tmp, 0, i));
				}
				while (ext.available() > 0) {
					int i = ext.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					strBuffer.append(new String(tmp, 0, i));
				}
//				// 获得标准输出
//				while (in.available() > 0) {
//					int i = in.read(tmp, 0, 1024);
//					if (i < 0) {
//						break;
//					}
//					strBuffer.append(new String(tmp, 0, i));
//				}
				if (channel.isClosed()) {
					break;
				}
				try {
					Thread.sleep(20);
				} catch (Exception ee) {}
			}
//			in.close();
			err.close();
			ext.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmp = null;
		return strBuffer.toString();
	}
	/**
	 * 查看是否执行成功
	 * 
	 * */
	public static int returnSuccess(ChannelExec openChannel, int exitCode) {

		while (true) {
			if (openChannel.isClosed()) {
				exitCode = openChannel.getExitStatus();
				break;
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException ie) {
			}
		}
		return exitCode;
	}
}
