# android-studio-plugin
该插件为了简化RxJava+Retrofit2的封装过程。

封装过程是一个持续优化的过程。如下:

使用RxJava+Retrofit2的开发过程中，需要对请求进行封装。
一般的封装流程：(kotlin代码)
```
    interface MemberService {
      @GET("xxxxxxx")
      fun memberList(): Observable<List<MemberBean>>
    }
    class MemberServiceImpl{
      fun memberList(): Observable<List<MemberBean>> {
        return retrofit.create(MemberService::class.java)
                       .memberList()
      }
    }
```    
优化1: 访问网络都是需要在非UI线程进行，数据请求成功之后，在切换回UI线程进行处理，所以继续如下的封装，增加线程切换功能
```
    class MemberServiceImpl{
      fun memberList(): Observable<List<MemberBean>> {
        return retrofit.create(MemberService::class.java)
                       .memberList()
                       .subscribeOn(Schedulers.io())
                       .unsubscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
      }
    }
```    
优化2: 多个请求的封装都会调用到线程切换的代码，因此可以抽离出来。
```
    class MemberServiceImpl{
      fun memberList(): Observable<List<MemberBean>> {
        return threadSwitch(retrofit.create(MemberService::class.java)
                       .memberList())
      }
      fun <T> threadSwitch(observable: Observable<T>): Observable<T>{
        return observable.subscribeOn(Schedulers.io())
                  .unsubscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
      }
    }
```    
优化3: 优化2的封装中断了rxjava的链式调用，失去了代码的优雅性，因此借助Transformer操作符继续优化
```
    class MemberServiceImpl{
      fun memberList(): Observable<List<MemberBean>> {
        return retrofit.create(MemberService::class.java)
                       .memberList()
                       .compose(threadSwitch())
      }
      fun <T> threadSwitch(): Observable.Transformer<T, T>{
        return Observable.Transformer { observable ->
          observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
      }
    }
```
至此，该请求封装基本完成，当然也可以借助kotlin的扩展属性进一步优化。每次新增加接口都要重复如下步骤：
1.定义service文件： MemberService
2.定义请求方法：memberList
3.实现接口

其中1，2两步无法避免，优化的只有第3步，因此可以借助Intelli IDEA的插件化技术，彻底解放双手，自动生成第三步的代码。

该项目比较有参考意义，可以根据自己的需求进行定制。
