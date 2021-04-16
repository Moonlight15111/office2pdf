# office2Pdf

#### 介绍
一个Demo性质的、简单的office文件转换成pdf文件小项目

#### 软件架构
Java office文件转换成PDF文件，可跨平台
1. 需要安装open office, 传送门: http://www.openoffice.org/download/
   安装参考: https://blog.csdn.net/qq_38294614/article/details/85621544
   Linux下乱码参考: https://blog.csdn.net/Wjhsmart/article/details/105505164
2. 需要jodconverter 2.2.2版本Jar包, 其他版本不支持docx、xlsx等等且该版本不存在maven中央仓库中
   传送门: https://sourceforge.net/projects/jodconverter/files/latest/download?source=files


#### 模块说明
  ```
  ├── src
        ├── main
            ├── java
                ├── common - 常量类定义
                ├── config - 配置类
                ├── connect - open office 连接池及连接配置类
                ├── convert - 文件转换类，真正干活的
                ├── util - 工具类
        ├── resources
            ├── lib-jar - jodconverter 2.2.2、itextpdf 5.5.11等需要用到的依赖Jar包
            ├── vue-example - Vue 前端页面示例，需要先 npm install --save vue-pdf 安装一下vue-pdf
  ```
#### 使用说明
进到convert包下，直接运行Office2PdfConvert类就好了