package RegionManagers.SockectManager;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

public class FtpUtils {
    // 此处设置为FTP的IP地址
    public String hostname = "192.168.116.1";
    public int port = 21;
    public String username = "user1";
    public String password = "1234";
    private static final int BUFFER_SIZE = 1024 * 1024 * 4;
    public FTPClient ftpClient = null;

    private void login() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            ftpClient.connect(hostname, port);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(BUFFER_SIZE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                closeConnect();
                System.out.println("FTP服务器连接失败");
            }
            //System.out.println("FTP连接成功");
        } catch (Exception e) {
            System.out.println("FTP登录失败" + e.getMessage());
        }
    }

    private void closeConnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                System.out.println("关闭FTP连接失败" + e.getMessage());
            }
        }
    }

    public Boolean downLoadFile(String ftpPath, String fileName, String savePath) {
        login();
        OutputStream os = null;
        if (ftpClient != null) {
            try {
                if (!ftpClient.changeWorkingDirectory(ftpPath)) {
                    System.out.println("/" + ftpPath + "该目录不存在");
                    return false;
                }
                ftpClient.enterLocalPassiveMode();

                FTPFile[] ftpFiles = ftpClient.listFiles();

                if (ftpFiles == null || ftpFiles.length == 0) {
                    System.out.println("/" + ftpPath + "该目录下无文件");
                    return false;
                }
                for(FTPFile file : ftpFiles){
                    if(fileName.equals("") || fileName.equalsIgnoreCase(file.getName())) {
                        if(!file.isDirectory()) {
                            File saveFile = new File(savePath + file.getName());
                            os = new FileOutputStream(saveFile);
                            ftpClient.retrieveFile(file.getName(), os);
                            os.close();
                        }
                    }
                }
                return true;
            } catch (IOException e) {
                System.out.println("下载文件失败" + e.getMessage());
            } finally {
                if(null != os) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                closeConnect();
            }
        }
        return false;
    }

    public boolean additionalDownloadFile(String ftpPath, String fileName) {
        login();
        OutputStream os = null;
        if (ftpClient != null) {
            try {
                if (!ftpClient.changeWorkingDirectory(ftpPath)) {
                    System.out.println("/" + ftpPath + "该目录不存在");
                    return false;
                }
                ftpClient.enterLocalPassiveMode();

                FTPFile[] ftpFiles = ftpClient.listFiles();

                if (ftpFiles == null || ftpFiles.length == 0) {
                    System.out.println("/" + ftpPath + "该目录下无文件");
                    return false;
                }
                for(FTPFile file : ftpFiles){
                    if(fileName.equals("") || fileName.equalsIgnoreCase(file.getName())) {
                        if(!file.isDirectory()) {
                            File saveFile = new File(file.getName().split("#")[1]);
                            os = new FileOutputStream(saveFile, true);
                            ftpClient.retrieveFile(file.getName(), os);
                            os.close();
                        }
                    }
                }
                return true;
            } catch (IOException e) {
                System.out.println("下载文件失败" + e.getMessage());
            } finally {
                if(null != os) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                closeConnect();
            }
        }
        return false;
    }

    public boolean uploadFile(String fileName, String savePath) {
        login();
        boolean flag = false;
        InputStream inputStream = null;
        if (ftpClient != null) {
            try{
                inputStream = new FileInputStream(new File(fileName));
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.makeDirectory(savePath);
                ftpClient.changeWorkingDirectory(savePath);
                ftpClient.storeFile(fileName, inputStream);
                inputStream.close();
                System.out.println("创建FTP文件： " + fileName);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null != inputStream) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                closeConnect();
            }
        }
        return flag;
    }

    public boolean uploadFile(String fileName, String IP, String savePath) {
        login();
        boolean flag = false;
        InputStream inputStream = null;
        if (ftpClient != null) {
            try{
                inputStream = new FileInputStream(new File(fileName));
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.makeDirectory(savePath);
                ftpClient.changeWorkingDirectory(savePath);
                ftpClient.storeFile(fileName, inputStream);
                ftpClient.rename(fileName, "/catalog/" + IP + "#" + fileName);
                inputStream.close();
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null != inputStream) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                closeConnect();
            }
        }
        return flag;
    }

    public boolean deleteFile(String fileName, String filePath) {
        login();
        boolean flag = false;
        if (ftpClient != null) {
            try {
                ftpClient.changeWorkingDirectory(filePath);
                ftpClient.dele(fileName);
                System.out.println("删除FTP文件： " + fileName);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeConnect();
            }
        }
        return flag;
    }
}