# CompressFileOrDirectory
用于压缩或者解压文件和文件夹的工具类。使用到的依赖是[Apache Commons Compress](http://commons.apache.org/proper/commons-compress/)和[Apache Commons IO](http://commons.apache.org/proper/commons-io/)。
JDK版本要求是1.7以上。

## TarUtil类
用于归档和解压缩tar文件

### filesOrDirectories2Tar
归档成tar文件。

`filesOrDirectories2Tar(List<String> needCompressedFileNamesOrPaths,String targetTarNameOrWithPathName)`

参数`needCompressedFileNamesOrPaths`需要归档的文件或者文件夹列表，可以指定文件或者文件的路径，没有指定路径默认当前目录。

参数`targetTarNameOrWithPathName`目标归档文件，可以指定路径，没有指定路径默认为当前目录。

### decompressTarFile
解压缩tar文件

`decompressTarFile(String tarFileName,String targetDirectory)`

参数`tarFileName` 要解压的tar文件，可以指定文件的路径。

参数`targetDirectory`目标目录，把tar文件的内容解压到指定的这个目录下。

## ZipUtil类
用于压缩和解压zip文件

### filesOrDirectories2Zip
压缩成zip文件

`filesOrDirectories2Zip(List<String> filesOrPaths, String targetZipFileName)`

参数`filesOrPaths`需要压缩的文件或者文件夹列表，可以指定文件或者文件的路径，没有指定路径默认当前目录。

参数`targetZipFileName`目标zip文件，可以指定路径，没有指定路径默认为当前目录。

### decompressZipFile
解压缩zip文件

`decompressZipFile(String zipFileName,String targetDirectory)`

参数`zipFileName` 要解压的zip文件，可以指定文件的路径。

参数`targetDirectory`目标目录，把zip文件的内容解压到指定的这个目录下。


