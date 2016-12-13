package me.cxis.compress.tar;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng.xi on 11/12/2016.
 * 文件或者文件夹归档成tar包
 */
public class TarUtil {

    /**
     * 把指定的文件或者目录归档成tar包
     *
     * 此方法接受两个参数，needCompressedFileNamesOrPaths是需要归档的文件或目录，此参数是一个存放String类型的List，
     * List中可以是需要归档的文件名，文件夹名称，带有路径的文件名，带有路径的文件夹名，也可以同时都包括。targetTarNameOrWithPathName，
     * 可以是只指定tar名字，也可以指定带路径的tar名字
     * @param needCompressedFileNamesOrPaths 需要归档的文件或者目录
     * @param targetTarNameOrWithPathName 目标tar文件名或者带路径的文件名
     */
    public static void filesOrDirectories2Tar(List<String> needCompressedFileNamesOrPaths,String targetTarNameOrWithPathName) throws Exception {
        if(null == needCompressedFileNamesOrPaths || needCompressedFileNamesOrPaths.isEmpty()){
            throw new Exception("需要归档的文件不能为空！");
        }

        if(null == targetTarNameOrWithPathName || targetTarNameOrWithPathName.isEmpty()){
            throw new Exception("目标tar文件名不能为空！");
        }

        //目标tar文件
        Path targetPath = Paths.get(targetTarNameOrWithPathName);
        //目标文件不能为路径
        if(Files.isDirectory(targetPath)){
            throw new Exception("目标tar文件名不能指定为路径！");
        }
        ArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(Files.newOutputStream(targetPath));

        //循环需要归档的list
        for(String fileNameOrPath : needCompressedFileNamesOrPaths){
            archive(archiveOutputStream, fileNameOrPath,"");
        }
    }

    /**
     * 将指定的归档文件解压到目标文件夹中
     * @param tarFileName 需要解压的归档文件
     * @param targetDirectory 目标文件夹
     */
    public static void decompressTarFile(String tarFileName,String targetDirectory) throws Exception {
        if(null == tarFileName || tarFileName.length() == 0){
            throw new Exception("需要解压的文件不能为空！");
        }

        Path targetDir = Paths.get(targetDirectory);
        if(!"".equals(targetDirectory) && Files.notExists(targetDir)){
            throw new Exception("要存放解压文件的文件夹不存在！");
        }

        //要解压的tar文件
        Path tarFile = Paths.get(tarFileName);
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(Files.newInputStream(tarFile));

        TarArchiveEntry tarArchiveEntry = null;
        while((tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()) != null){
            //是目录的先创建目录
            if(tarArchiveEntry.isDirectory()){
                Path dir = Paths.get(targetDirectory + tarArchiveEntry.getName());
                Files.createDirectory(dir);
            }else {//写文件
                byte[] content = new byte[(int)tarArchiveEntry.getSize()];
                OutputStream outputStream = new FileOutputStream(targetDirectory + tarArchiveEntry.getName());
                int length = 0;
                while((length = tarArchiveInputStream.read(content)) != -1){
                    outputStream.write(content,0,length);
                }
                IOUtils.closeQuietly(outputStream);
            }

        }

    }

    /**
     * 归档文件或者文件夹
     * @param archiveOutputStream 归档文件输出流
     * @param fileNameOrPath 需要归档的文件
     * @param internalFilePath 归档文件内部文件存储的路径，暂时为空，指定路径的还未实现
     * @throws IOException
     */
    private static void archive(ArchiveOutputStream archiveOutputStream, String fileNameOrPath,String internalFilePath) throws IOException {
        Path path = Paths.get(fileNameOrPath);
        String fileName = path.getFileName().toString();
        //文件夹
        if(Files.isDirectory(path)){
            if("".equals(internalFilePath)){
                internalFilePath += fileName + "/";
            }else {
                internalFilePath += "/";
            }

            //归档文件夹
            archiveDirectory(archiveOutputStream,path,internalFilePath);
        }else{
            if("".equals(internalFilePath)){
                internalFilePath += fileName;
            }

            //归档文件
            if(!fileName.startsWith(".")){
                archiveFile(archiveOutputStream, path,internalFilePath);
            }
        }
    }

    /**
     * 归档文件夹
     * @param archiveOutputStream 归档文件输出流
     * @param path 需要添加进归档文件的文件
     * @param internalFilePath 归档文件内部路径
     * @throws IOException
     */
    private static void archiveDirectory(ArchiveOutputStream archiveOutputStream, Path path,String internalFilePath) throws IOException {
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(path.toFile());
        tarArchiveEntry.setName(internalFilePath);
        archiveOutputStream.putArchiveEntry(tarArchiveEntry);
        archiveOutputStream.closeArchiveEntry();

        for(File file : path.toFile().listFiles()){
            //递归调用归档方法
            archive(archiveOutputStream,file.getPath(),internalFilePath  + file.getName());
        }
    }

    /**
     * 归档文件
     * @param archiveOutputStream 归档文件输出流
     * @param file 需要添加进归档文件的文件
     * @param internalFilePath 归档文件内部路径
     * @throws IOException
     */
    private static void archiveFile(ArchiveOutputStream archiveOutputStream, Path file,String internalFilePath) throws IOException {
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file.toFile());
        tarArchiveEntry.setName(internalFilePath);
        archiveOutputStream.putArchiveEntry(tarArchiveEntry);
        IOUtils.copy(Files.newInputStream(file),archiveOutputStream);
        archiveOutputStream.closeArchiveEntry();
    }

    public static void main(String[] args) throws Exception {
        List<String> filePathList = new ArrayList<>();
        filePathList.add("/xxx/upload/images/1452322008677.jpg");
        filePathList.add("/xxx/upload/images/14523220086772.jpg");
        filePathList.add("/xxx/upload/images/test");
        filesOrDirectories2Tar( filePathList,"/xxx/upload/images/1.tar");

        decompressTarFile("/xxx/upload/images/1.tar","/xxx/upload/");
    }
}
