# AutoComleteTextView最佳实践-原理剖析篇

![banner][banner]

本文着重讲解ACTV触发候选列表展示的代码总流程，深入了解Android的控件传递事件的机制。

**关于作者**

> 景三，程序员，主要从事Android平台基础架构方面的工作，欢迎交流技术方面的问题，可以去我的[Github](https://github.com/relish-wang)提issue或者发邮件至relish.wang@gmail.com与我交流。


首先我们想到ACTV是在输入框的内容文字改变的时候回触发候选列表展示。由于ACTV继承自EditText，我们就想到了这个功能一定是配合TextWatcher实现的。接下来我们来寻找这个`TextWatcher`。

ACTV有多个构造方法，所有的构造方法，最终都调用了参数最多的构造方法。果不其然，我们在这里找到它为自己设置了一个`TextWatcher`。

**ACTV的构造方法**:

```java
public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr,
        int defStyleRes, Theme popupTheme) {
    super(context, attrs, defStyleAttr, defStyleRes);
    // ...省略部分代码...
    // 添加输入框内容文字变化的监听器
    addTextChangedListener(new MyWatcher());
    // ...省略部分代码...
}
```

**MyWatcher**:

```java
// MyTextWatcher是ACTV的一个私有内部类
private class MyWatcher implements TextWatcher {
    public void afterTextChanged(Editable s) {
        doAfterTextChanged();// 调用了ACTV的doAfterTextChanged方法
    }
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        doBeforeTextChanged();
    }
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}

void doAfterTextChanged() {
    // ...省略部分代码...
    // 判断是否可以展示候选列表(前文提过ACTV有一个completionThreshold属性, 设置的数字的值代表，达到多少个字符就展示候选列表)
    if (enoughToFilter()) { 
        if (mFilter != null) {
            mPopupCanBeUpdated = true;
            performFiltering(getText(), mLastKeyCode); // 进入这个方法中继续查看
        }
    } else {
       // ...省略部分代码...
    }
}
```

根据前面贴出的部分源码可知，代码流程走入了`MyWatcher`里的`afterTextChanged`,`afterTextChanged`又调用了ACTV的`performFiltering`方法。

**ACTV#performFiltering:**

```java
protected void performFiltering(CharSequence text, int keyCode) {
    // 这里的第二个参数(this)传入的其实是一个FilterListener。
    // （ACTV实现了FilterListener的onFilterComplete方法）
    mFilter.filter(text, this);
}
```

`performFiltering`里直接调用了`mFilter`的`filter`方法。我们来找一下mFilter这个对象是从哪里来的。

纵观整个ACTV的源码我们发现`mFilter`只有一处被赋值的地方，就在ACTV的`setAdapter`方法里:

**ACTV#setAdapter**:

```java
public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
    // ...省略部分代码...
    if (mAdapter != null) {
        mFilter = ((Filterable) mAdapter).getFilter();// 获取Adapter中的Filter对象
        // ...省略部分代码...
    } else {
        mFilter = null;
    }
		// ...省略部分代码...
}
```

由上面贴出的源码节选可知，`mFilter`来自于我们外部为ACTV设置的适配器中。找到了`mFilter`的来源，那我们看一下前文提到调用了`mFilter`的`filter`方法。

**Filter#filter**:

```java
public final void filter(CharSequence constraint, FilterListener listener) {
    synchronized (mLock) {
				// ...省略部分代码...
        mThreadHandler = new RequestHandler(thread.getLooper());
        Message message = mThreadHandler.obtainMessage(FILTER_TOKEN);
        RequestArguments args = new RequestArguments();
        args.constraint = constraint != null ? constraint.toString() : null;
        args.listener = listener;
        message.obj = args;
        // 向mThreadHandler发送了what为FILTER_TOKEN的Message
        mThreadHandler.sendMessageDelayed(message, delay);
      	// ...省略部分代码...
    }
}
```

**Filter#RequestHandler#handleMessage**:

```java
private class RequestHandler extends Handler {
    public void handleMessage(Message msg) {
        int what = msg.what;
        switch (what) {
            case FILTER_TOKEN:
                RequestArguments args = (RequestArguments) msg.obj;
            		// performFiltering具体实现在Adapter中
                args.results = performFiltering(args.constraint);
                Message message = mResultHandler.obtainMessage(what);
                message.obj = args;
                message.sendToTarget();
      					// ...省略部分代码...
                break;
           // ...省略部分代码...
        }
    }
}
```

看到这里的`args.result`数据来源于`performFiltering`方法，这个方法在Filter中是一个抽象方法，具体实现就是在我们为ACTV设置的Adapter中。

**Filter#performFiltering**:

这个方法实现的代码前文没贴，不过没关系，笔者来说明。这个方法是用来根据ACTV输入框里的关键字来过滤出需要展示候选数据的List。`performFiltering`中就是实现具体的过滤规则(比如匹配一下用户的手机号，姓名等)。

上面我们看到`performFiltering`的数据传入`Massage`被发送到了`mResultHandler`里我们来看一下`mResultHandler`的`handleMessage`。

**Filter#ResultsHandler**:

```java
private class ResultsHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
         RequestArguments args = (RequestArguments) msg.obj;
				 // 将performFiltering返回的数据作为入参传入publishResults
         publishResults(args.constraint, args.results);
         if (args.listener != null) {
            int count = args.results != null ? args.results.count : -1;
            // 再将过滤出来的数据量传给Listener，通知设置监听器的一方过滤操作结束且告知过滤后的数量
            args.listener.onFilterComplete(count);
        }
    }
}
```

**Filter#publishResults**

最后`performFiltering`返回的过滤结果传入`publishResults`。`publishResults`就负责将数据展示出来就行了(设置数据源，调用Adapter的`notifiyDataSetChanged`)。

**ACTV#onFilterComplete**:——(ACTV实现`FilterListener#publishResults`)

在`publishResults`之后就会调用后`FilterListener#onFilterComplete`, 这个监听器在ACTV中有实现：

```java
public void onFilterComplete(int count) {
    updateDropDownForFilter(count);// 调用下方的方法
}

private void updateDropDownForFilter(int count) {
  	// ...省略部分代码...
  	// （展示数据数量大于0 或 候选窗口总是展示）且 输入文字足够触发过滤器
    if ((count > 0 || dropDownAlwaysVisible) && enoughToFilter) {
        if (hasFocus() && hasWindowFocus() && mPopupCanBeUpdated) {
            showDropDown(); // 展示候选窗口
        }
    } 
  	// ...省略部分代码...
}
```

**ACTV#showDropDown**：

```java
/**
 * <p>Displays the drop down on screen.</p>
 */
public void showDropDown() {
    // ...省略部分代码...
    mPopup.show();
    // ...省略部分代码...
}
```

**mPopup#show**:

```java
/**
 * Show the popup list. If the list is already showing, this method
 * will recalculate the popup's size and position.
 */
@Override
public void show() {
    int height = buildDropDown();// 1 在show的时候会动态计算高度
    // ...省略部分代码...
    if (mPopup.isShowing()) {
    // ...省略部分代码...
    } else {
        final int heightSpec;
        if (mDropDownHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            heightSpec = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            if (mDropDownHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                heightSpec = height;
            } else {
                heightSpec = mDropDownHeight;
            }
        }
        mPopup.setWidth(widthSpec);
        mPopup.setHeight(heightSpec); // 2 并设置为mPopup的高度
    }
}
```

通过上述的代码流程梳理，我们大概知道了ACTV的工作原理。我们再简单整理一下过程:

- 1 ACTV设置TextWatcher

- 2 TextWatcher的afterTextChanged中执行了mFilter.filter方法(*mFilter来自我们为ACTV设置的Adapter*)

- 3 mFilter.filter方法先执行了performFiltering, performFiltering传入的值就是ACTV输入框中的输入的文字

- 4 performFiltering返回根据关键字匹配到的数据，再将返回数据传入publishResults。

- 5 最后通知ACTV数据过滤工作结束(onFilterComplete), ACTV在onFilterComplete中展示候选列表窗口。



[banner]: ./art/banner.png