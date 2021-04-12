# office2Pdf

#### 介绍
一个Demo性质的、简单的office文件转换成pdf文件小项目

#### 软件架构
Java office文件转换成PDF文件

#### 模块说明
  ```
  ├── src
        ├── main
            ├── java
                ├── common - 常量类定义
                ├── config - 配置类
                ├── connect - open office 连接相关类，目前并没啥用
                ├── convert - 文件转换类，真正干活的
                ├── util - 工具类
        ├── resources
            ├── lib-jar - 几个需要用到的依赖Jar包
  ```
#### 使用说明
进到convert包下，直接运行Office2PdfConvert类就好了