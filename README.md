# 说明

本程序主要是便于将自己的文件上传到FTP服务器，运行setup.bat就能安装，然后运行bin目录下的start.bat就能运行程序，之后只需要将要上传的文件放到upload文件夹中，文件就会自动上传，上传的文件会重命名，通过`https://aliyun.hellozjf.com:7004/uploads/重命名过的文件名`进行访问

# 后续工作

- [ ] 研究如何递归监视目录，这样就可以通过时间分类各文件
- [x] 研究如何增加托盘界面

# 版本介绍

## 1.0.5
剪切板里面的内容，默认将前缀带上，可在配置文件中配置urlPrefix修改前缀

## 1.0.4
增加日期信息

## 1.0.3
利用托盘控制程序

## 1.0.2
图片拷贝到目标文件夹，生成了`uuid/xxxxx`这样的字符串，该字符串会被拷贝到剪切板中

## 1.0.1
测试完成，基本可用

## 1.0.0
开发版本，无法使用