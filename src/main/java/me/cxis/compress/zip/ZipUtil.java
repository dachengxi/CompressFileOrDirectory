package me.cxis.compress.zip;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by cheng.xi on 13/12/2016.
 * 压缩和解压zip文件
 */
public class ZipUtil {

    /**
     * 压缩文件或者目录到指定的zip文件中
     * @param filesOrPaths 文件名或者带路径的文件名，或者目录的list
     * @param targetZipFileName 最终的zip文件名
     * @throws Exception
     */
    public static void filesOrDirectories2Zip(List<String> filesOrPaths, String targetZipFileName) throws Exception {
        if(null == filesOrPaths || filesOrPaths.isEmpty()){
            throw new Exception("需要压缩的文件不能为空！");
        }

        if(null == targetZipFileName || targetZipFileName.isEmpty()){
            throw new Exception("目标zip文件名不能为空！");
        }

        Path targetFile = Paths.get(targetZipFileName);
        if(Files.isDirectory(targetFile)){
            throw new Exception("目标zip文件名不能指定为路径！");
        }

        ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(Files.newOutputStream(targetFile));
        for(String fileNameOrPath : filesOrPaths){
            zip(zipArchiveOutputStream,fileNameOrPath,"");
        }
        IOUtils.closeQuietly(zipArchiveOutputStream);
    }

    /**
     * 将指定的zip文件解压到目标文件夹中
     * @param zipFileName 需要解压的归档文件
     * @param targetDirectory 目标文件夹
     */
    private static void decompressZipFile(String zipFileName,String targetDirectory) throws Exception {
        if(null == zipFileName || zipFileName.length() == 0){
            throw new Exception("需要解压的文件不能为空！");
        }

        Path targetDir = Paths.get(targetDirectory);
        if(!"".equals(targetDirectory) && Files.notExists(targetDir)){
            throw new Exception("要存放解压文件的文件夹不存在！");
        }

        //要解压的zip文件
        ZipFile zipFile = new ZipFile(Paths.get(zipFileName).toFile());
        Enumeration<ZipArchiveEntry> entryEnumeration = zipFile.getEntries();
        ZipArchiveEntry zipArchiveEntry = null;

        while(entryEnumeration.hasMoreElements()){
            zipArchiveEntry = entryEnumeration.nextElement();
            InputStream inputStream = zipFile.getInputStream(zipArchiveEntry);

            //是目录的先创建目录
            if(zipArchiveEntry.isDirectory()){
                Files.createDirectory(Paths.get(targetDirectory + zipArchiveEntry.getName()));
            }else {//写文件
                OutputStream outputStream = new FileOutputStream(targetDirectory + zipArchiveEntry.getName());
                IOUtils.copy(inputStream,outputStream);
                IOUtils.closeQuietly(outputStream);
            }
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * 压缩文件或目录
     * @param archiveOutputStream 压缩文件输出流
     * @param fileNameOrPath 需要压缩的文件或者我目录
     * @param internalFilePath zip文件内部文件的路径
     * @throws IOException
     */
    private static void zip(ArchiveOutputStream archiveOutputStream, String fileNameOrPath, String internalFilePath) throws IOException {
        Path path = Paths.get(fileNameOrPath);
        String fileName = path.getFileName().toString();
        //文件夹
        if(Files.isDirectory(path)){
            if("".equals(internalFilePath)){
                internalFilePath += fileName + "/";
            }else {
                internalFilePath += "/";
            }

            //压缩目录
            zipDirectory(archiveOutputStream,path,internalFilePath);
        }else{
            if("".equals(internalFilePath)){
                internalFilePath += fileName;
            }

            //压缩文件
            if(!fileName.startsWith(".")){
                zipFile(archiveOutputStream, path,internalFilePath);
            }
        }
    }

    /**
     * 压缩目录
     * @param archiveOutputStream 压缩文件输出流
     * @param path 目录
     * @param internalFilePath zip文件内部路径
     * @throws IOException
     */
    private static void zipDirectory(ArchiveOutputStream archiveOutputStream, Path path,String internalFilePath) throws IOException {
        ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(path.toFile(),internalFilePath);
        archiveOutputStream.putArchiveEntry(zipArchiveEntry);
        archiveOutputStream.closeArchiveEntry();

        for(File file : path.toFile().listFiles()){
            //递归调用压缩方法
            zip(archiveOutputStream,file.getPath(),internalFilePath  + file.getName());
        }
    }

    /**
     * 压缩文件
     * @param archiveOutputStream 压缩文件输出流
     * @param file 文件
     * @param internalFilePath zip文件内部路径
     * @throws IOException
     */
    private static void zipFile(ArchiveOutputStream archiveOutputStream, Path file,String internalFilePath) throws IOException {
        ZipArchiveEntry tarArchiveEntry = new ZipArchiveEntry(file.toFile(),internalFilePath);
        archiveOutputStream.putArchiveEntry(tarArchiveEntry);
        InputStream inputStream = Files.newInputStream(file);
        IOUtils.copy(inputStream,archiveOutputStream);
        archiveOutputStream.closeArchiveEntry();
        IOUtils.closeQuietly(inputStream);
    }

    public static void main(String[] args) throws Exception {
        long zipStartTime = System.currentTimeMillis();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("/xxx/test");
        filesOrDirectories2Zip( filePathList,"/xxx/test.zip");
        long zipEndTime = System.currentTimeMillis();
        System.out.println("zip times:" + (zipEndTime - zipStartTime));

        long unzipStartTime = System.currentTimeMillis();
        decompressZipFile("/xxx/test.zip","/xxx/testunzip/");
        long unzipEndTime = System.currentTimeMillis();
        System.out.println("unzip times:" + (unzipEndTime - unzipStartTime));
    }


}
