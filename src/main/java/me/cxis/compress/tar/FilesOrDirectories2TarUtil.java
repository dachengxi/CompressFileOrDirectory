package me.cxis.compress.tar;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng.xi on 11/12/2016.
 * 文件或者文件夹压缩成tar包
 */
public class FilesOrDirectories2TarUtil {

    /**
     * 把指定的文件或者目录压缩成tar包
     *
     * 此方法接受两个参数，needCompressedFileNamesOrPaths是需要压缩的文件或目录，此参数是一个存放String类型的List，
     * List中可以是需要压缩的文件名，文件夹名称，带有路径的文件名，带有路径的文件夹名，也可以同时都包括。targetTarNameOrWithPathName，
     * 可以是只指定tar名字，也可以指定带路径的tar名字
     * @param needCompressedFileNamesOrPaths 需要压缩的文件或者目录
     * @param targetTarNameOrWithPathName 目标tar文件名或者带路径的文件名
     */
    public static void filesOrDirectories2Tar(List<String> needCompressedFileNamesOrPaths,String targetTarNameOrWithPathName) throws Exception {
        if(null == needCompressedFileNamesOrPaths || needCompressedFileNamesOrPaths.isEmpty()){
            throw new Exception("需要压缩的文件不能为空！");
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

        //解析需要压缩的文件或文件夹
        for(String fileNameOrPath : needCompressedFileNamesOrPaths){
            archive(archiveOutputStream, fileNameOrPath,"");



        }
    }

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

            archiveDirectory(archiveOutputStream,path,internalFilePath);
        }else{
            //文件
            //先解析文件名和路径
            //String filenName = path.getFileName().toString();
            //String filePath = fileNameOrPath.substring(0,fileNameOrPath.lastIndexOf(File.separator) + 1);
            if("".equals(internalFilePath)){
                internalFilePath += path.getFileName().toString();
            }

            archiveFile(archiveOutputStream, path,internalFilePath);
        }
    }

    private static void archiveDirectory(final ArchiveOutputStream archiveOutputStream, final Path path,String internalFilePath) throws IOException {
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(path.toFile());
        tarArchiveEntry.setName(internalFilePath);
        archiveOutputStream.putArchiveEntry(tarArchiveEntry);
        archiveOutputStream.closeArchiveEntry();


        for(File file : path.toFile().listFiles()){
            System.out.println(internalFilePath + file.getName());
            archive(archiveOutputStream,file.getPath(),internalFilePath  + file.getName());
        }

        /*Files.walkFileTree(path,new SimpleFileVisitor<Path>(){


            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(path.getFileName() + "/" + file.getFileName());
                archive(archiveOutputStream,file.toRealPath().toString(),path.getFileName() + "/" + file.getFileName());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println(dir.getFileName() + "=-");
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println(dir.getFileName() + "=");
                return FileVisitResult.CONTINUE;
            }
        });*/
    }

    private static void archiveFile(ArchiveOutputStream archiveOutputStream, Path file,String internalFilePath) throws IOException {
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file.toFile());
        tarArchiveEntry.setName(internalFilePath);
        archiveOutputStream.putArchiveEntry(tarArchiveEntry);

        IOUtils.copy(Files.newInputStream(file),archiveOutputStream);
        archiveOutputStream.closeArchiveEntry();
    }

    public static void main(String[] args) throws Exception {
        List<String> filePathList = new ArrayList<>();
        filePathList.add("/mos/upload/images/food/1452322008677.jpg");
        filePathList.add("/mos/upload/images/food/14523220086772.jpg");
        filePathList.add("/mos/upload/images/food/test");
        filesOrDirectories2Tar( filePathList,"/mos/upload/images/food/1.tar");
    }
}
