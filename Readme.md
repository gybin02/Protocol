# Jet Protocol 模块间协议框架

业务模块间通常通过定义/实现java的interface完成业务逻辑，必然导致模块间存在代码层面的依赖。也导致编译期的工程依赖。事实上，业务模块间仅仅是逻辑上存在依赖，完全没必要产生实际的工程依赖。

该组件提供了一种解藕模块间显式依赖的能力。


### 方案对比
 * 定义请求接口，类似于请求服务器的工作方式； 服务端于前端约定好数据格式，双方进行通信
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
  compile 'com.meiyou.framework:protocol:0.0.1'
  ```
调用的地方：
```java
  ProtocolProxy.getInstance().
                        create(TestInterface.class)
                        .test("Implement By Module B");
  
   //使用方只依赖了ModuleStub，ProtocolInterpreter会自动调用合适的类。
                                
 ```
### 实现原理
1. 通过编译期注解,实现收集所有的Interface ->  Implement 对应关系文件：relate.json
2. 使用Java动态代理 ProxyInvoker 调用

### 混淆
### 问题：

### 参考

### TODO
- 之前可以通过implements interface 比较方便地获得子类方法的签名，现在没有IDE智能提示，写实际的实现类方法的时候，有点不方便。（fixed）
- 如果出现找不到 Implement 会报 报 空指针， 最好可以处理掉，使用空实现，但是Log 提示
- 错误提示需要更明显：找不到类啊，方法签名不对等等；
- Kotlin版本实现
- 打AAR发布到 JcCenter

### License

Copyright 2017 zhengxiaobin@xiaoyouzi.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
