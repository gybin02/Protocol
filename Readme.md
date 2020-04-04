# Jet Protocol 模块间协议框架

业务模块间通常通过定义/实现java的interface完成业务逻辑，必然导致模块间存在代码层面的依赖。也导致编译期的工程依赖。事实上，业务模块间仅仅是逻辑上存在依赖，完全没必要产生实际的工程依赖。

该组件提供了一种解藕模块间显式依赖的能力。


### 方案对比
 * 定义请求接口，类似于请求服务器的工作方式； 服务端于前端约定好数据格式，双方进行通信
 * 使用[Jet](https://github.com/gybin02/Jet) 工程里面的@JImplement 和 @JProvider方法；缺点是：需要指定的实现的类全路径。
 * 定义interface，模块间调用使用接口； (本组件使用方式)
 
### 使用：

#### 比如模块A定义接口，提供对外能力：
```java
public interface TestInterface {

     public void test(String msg);
}
```
#### 模块B实现接口：
public class TestImplement implements TestInterface {

    @Override
    public void test(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
#### 最终使用方将会：
```java
TestImplement stub＝new TestImplement();
//这种方式必然导致模块B依赖模块A。 代码产生了依赖，耦合。
```
#### 本组件的使用方式：
模块A：
```java
//@Interface 注解里的Value要全局唯一；
@Interface("protocol_key")
public interface TestInterface {

     public void test(String msg);
}
```
模块B：
```java
   @Implement("protocol_key")
   public class TestImplement {
   
       public void test(String msg) {
           Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
   
       }
   }
   //这里实际上实现了TestInterface的接口方法，要求方法与之签名一致。只是没有使用implements关键字。
 ```
使用方：

在工程build.gradle中配置依赖:
 ```groovy
   //内部版本：0.0.1-SNAPSHOT
  compile 'com.jet.framework:protocol:0.0.1'
  ```
调用的地方：
```java
  ProtocolProxy.getInstance().
                        create(TestInterface.class)
                        .test("Implement By Module B");
  
   //使用方只依赖了TestInterface，ProtocolProxy会自动调用合适的类。
                                
 ```
 初始：
 在Application里面：初始化：
         ProtocolProxy.getInstance().init(this);

### 实现原理
1. 通过编译期注解Apt,实现收集所有的Interface ->  Implement 对应关系文件：relate.json 
2. 路由收集，在app的gradle文件里面新增方法，实现吧json文件 拷贝到 Assets/protocol目录下。
3. 在应用初始化化的时候，调用init()方法，实现把asset/protocol/目录下的json都加载如内存。
3. 使用Java动态代理 ProxyInvoker 调用生成类，实现路由分发。

### 收集路由gradle脚本：（已经不能使用 2020-0404）
```
//拷贝生成的 assets/目录到打包目录
android.applicationVariants.all { variant ->
    def variantName = variant.name
    def variantNameCapitalized = variantName.capitalize()
    def copyMetaInf = tasks.create "copyMetaInf$variantNameCapitalized", Copy
    copyMetaInf.from project.fileTree(javaCompile.destinationDir)
    copyMetaInf.include "assets/**"
    copyMetaInf.into "build/intermediates/sourceFolderJavaResources/$variantName"
    tasks.findByName("transformResourcesWithMergeJavaResFor$variantNameCapitalized").dependsOn copyMetaInf
}
```
原理跟Google auto-server类似。

## 重要提示 2020.0404
1. 在Gradle 5.0上上面脚本已经不能使用了
2. 思路可以参考，需要去重新寻找API。

#### 两个其他思路：
- 使用JavaPoet 把数据放在 中间的类的变量里面
- 使用Asset/json文件保存
关键类
```
/**
     * 保存数据到Asset目录下
     *
     * @param content
     */
    private void createAssetFile(String content) {
        // app/src/assets
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;

        try {
            //filer.createResource()意思是创建源文件
            //我们可以指定为class文件输出的地方，
            //StandardLocation.CLASS_OUTPUT：java文件生成class文件的位置，/app/build/intermediates/javac/debug/classes/目录下
            //StandardLocation.SOURCE_OUTPUT：java文件的位置，一般在/ppjoke/app/build/generated/source/apt/目录下
            //StandardLocation.CLASS_PATH 和 StandardLocation.SOURCE_PATH用的不多，指的了这个参数，就要指定生成文件的pkg包名了

            FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", FLASH_PATH);
            String resourcePath = resource.toUri().getPath();
            //由于我们想要把json文件生成在app/src/main/assets/目录下,所以这里可以对字符串做一个截取，
            //以此便能准确获取项目在每个电脑上的 /app/src/main/assets/的路径
            System.out.println(resourcePath);
            String appPath = resourcePath.substring(0, resourcePath.indexOf("/build/generated"));
            String module = appPath.substring(appPath.lastIndexOf("/") + 1);
            String assetsPath = appPath + "/src/main/assets/flash";
            System.out.println(assetsPath);
            System.out.println(module);


            File file = new File(assetsPath);
            if (!file.exists()) {
                file.mkdir();
            }

            //写入文件
            File outputFile = new File(file, "flash_" + module);
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.createNewFile();

            //利用fastjson把收集到的所有的页面信息 转换成JSON格式的。并输出到文件中
            fos = new FileOutputStream(outputFile);
            writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
```

### 混淆
中间类路径要记得混淆

### 问题：

### 参考
- [微信Android模块化架构重构实践](https://mp.weixin.qq.com/s/mkhCzeoLdev5TyO6DqHEdw)
- [Android程序设计探索：MVP与模块化](http://www.jianshu.com/p/fb057953131e)
- [Android模块化实践](http://www.jianshu.com/p/e812595b5873)
- [组件化、模块化、集中式、分布式、服务化、面向服务的架构、微服务架构
](http://www.hollischuang.com/archives/1628)
- [Android组件化和插件化开发](http://dahei.me/2016/06/30/Android%E7%BB%84%E4%BB%B6%E5%8C%96%E5%92%8C%E6%8F%92%E4%BB%B6%E5%8C%96%E5%BC%80%E5%8F%91/)
- 替代： 可以用：https://github.com/gybin02/fllash

### TODO
- 之前可以通过implements interface 比较方便地获得子类方法的签名，现在没有IDE智能提示，写实际的实现类方法的时候，有点不方便。（fixed）
- 如果出现找不到 Implement 会报 报 空指针， 最好可以处理掉，使用空实现，但是Log 提示
- 错误提示需要更明显：找不到类啊，方法签名不对等等；
- 打AAR发布到 JcCenter

