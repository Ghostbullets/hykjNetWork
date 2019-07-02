# hykjNetWork
使用前请在app或者你要使用的该依赖的模块中设置

 implementation 'com.google.code.gson:gson:2.8.2'
 implementation 'com.squareup.okio:okio:1.14.0'
 implementation 'com.zhy:okhttputils:2.6.2'
    
 然后新建一个类，继承AbsReq，并实现添加头部param的接口。
 
 如果要使用rxjava请求，请看下面,具体使用可参考RxJavaHelper
 
public abstract class AbsReq<H> {
    private String baseUrl;//基础请求url
    private Class<H> service;//泛型传入接口类
    private Map<String, String> headers = new LinkedHashMap<>();//头布局集合参数，由继承AbsReq的类选择是否设置
 
  /**
     * 初始化，得到对应的网络请求的接口
     *
     * @return
     */
    public H init(OkHttpClient.Builder builder) {
    
    //得到继承AbsReq的类所定义的属性名、属性值map对象参数(注：不包含AbsReq内定义的参数)
     public Map<String, String> getParams() {
     
     //得到一个RequestBody类，里面的参数以键值对存放{key:value}
     public RequestBody getRequestBody() {
     
     //得到一格RequestBody类，里面的参数组成json编码格式的，也就是编码成{"mid":"10","method":"userInfo","dateInt":"20160818"}
     public RequestBody getJSONBody() {
     
     添加头部参数集
     public <T extends AbsReq> T addHeaders(Map<String, String> headers) {
     
     
     public class FileUploadReq<H> extends AbsReq<H> {
       private String name;//文件传入参数名
       private List<File> fileList = new ArrayList<>();//文件列表
     
       public FileUploadReq(String baseUrl, List<String> filePaths, String name) {
       public FileUploadReq(String baseUrl, String name, String... filePaths) {
       public FileUploadReq(String baseUrl, String name, File... fileList) {
       public FileUploadReq(String baseUrl, String name, List<File> fileList) {
        
       //得到文件上传RequestBody
       public RequestBody getUploadBody() {
     
     
     public class EasyHttp {
      private Observable mObservable;//第一个网络请求被观察者
      private boolean showProgress;//是否显示弹窗
      private String progress;//弹窗文字描述
      
      
     public class ApiFactoryAbs<H> {
      private Class<H> service;//泛型传入接口类
      private String baseUrl;//基础请求url
      private Map<String, String> headers = new LinkedHashMap<>();//头布局集合参数，由继承AbsReq的类选择是否设置
      
     public abstract class AbsRxJavaHelper<T> {
      protected boolean isFailResultObject;//是否在数据获取失败时返回Object泛型的被观察者，而不是error的被观察者,默认false，请注意在网络请求结束后恢复状态为false

        /**
     * 添加线程管理并订阅
     * activity中使用
     *
     * @param ob                被观察者
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param mView             接口
     * @param event             在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
     * @param progressSubscribe 观察者
     */
     public void toSubscribe(Observable ob, final boolean isShowProgress, final String progress, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
    
        /**
     * 合并四个网络请求，并将他们的数据放到{@link FourResultData}类中
     *
     * @param ob1               被观察者1
     * @param ob2               被观察者2
     * @param ob3               被观察者3
     * @param ob4               被观察者4
     * @param isShowProgress    是否显示弹窗
     * @param progress          进度条字符串
     * @param mView             接口
     * @param event             在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
     * @param progressSubscribe 观察者
     */
     public <T, H, Z, X> void zipToSubscribe(Observable ob1, Observable ob2, Observable ob3, Observable ob4, final boolean isShowProgress, final String progress, RxView mView, Object event, final ProgressSubscribe progressSubscribe) {
