# ClipboardMonitor
实现andrion与win之前的剪切板共享

## web_go

AI prompt:  

用go语言实现一个web网站：

1、数据保存在内存中，以kv形式保存，key是序号从c0到c9， value存放的是剪切板的内容，此内容从手机端调用https://yourserver.com/api/clipdata，传送过来。

2、用户访问https://yourserver.com/show/clipdata，可显示内存中保存的所有10条剪切板内容，key的序号从c0到c9；用户可以中界面中修改。修改的输入框要大些，用三行text输入框，带滚动条；用户选中text输入框后，其内容，自动拷贝当前剪贴板。

3、设计两个按钮，一个保存数据，一个是刷新数据。保存数据，提交到web网站，并保存到内存中；刷新数据则从web网站从新获取数据，并显示。保存数据和刷新数据的接口由你来定义和设计

4、界面自然，易操作，颜色柔和。

5、go程序目录结构的设计要符合主流

## android

AI prompt:

写一个andriod的APP, 使用kotlin语言作为开发语言

1、Android studio程序目录结构的设计

2、主界面显示一个表格，显示最近的10条剪切板内容，内容初始值从服务器端get通过HTTP://192.168.0.118:3388/api/clipdata ；获取的DATA数据结构是json格式：[  {    "key": "C0",    "value": "11"  },  {    "key": "C1",    "value": "22"  },  {    "key": "C2",    "value": "33"  },  {    "key": "C3",    "value": "44"  },  {    "key": "C4",    "value": "55"  },  {    "key": "C5",    "value": "66"  },  {    "key": "C6",    "value": "77"  },  {    "key": "C7",    "value": "88"  },  {    "key": "C8",    "value": "99"  },  {    "key": "C9",    "value": "00"  }]

3、主界面的表格，采用RecyclerView组件，当用户点item时，将该item的value值拷贝到剪切板中

3、主界面设计两个按钮，两个按钮并列位于主界面表格的下方，中间有空隙分开。一个是刷新数据，一个是上传剪切板内容，刷新数据则从web网站从新获取数据，并显示，内容从服务器端get通过HTTP://192.168.0.118:3388/api/clipdata 获取，同上； 上传按钮，首先从剪切板获取剪切内容，将数据上传给服务器，URL是HTTP://192.168.0.118:3388/insert/clipdata, POST方工，data是JSON格式 [{value: XXX}], 上传后，更新上面的主界面，第一行显示最新的剪切内容，其它行移次下移，最后一行删除。

4、界面自然，易操作，颜色柔和。

5、主线程（UI 线程）是用来处理用户界面更新的，要求所有耗时操作（如网络请求、文件操作等）必须在后台线程中执行，而不是在主线程中。

6、 HTTP 访问改为使用标准的 HTTP 库（如 `HttpURLConnection`），而不是使用 Retrofit

