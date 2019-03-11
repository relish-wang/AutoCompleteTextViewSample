# AutoCompleteTextView最佳实践

写这篇文章主要是为了记录一次使用AutoCompleteTextView的踩坑过程，并复盘整个的解决流程。

### 一、AutoCompleteTextView简介
AutoCompleteTextView是一个可编辑的文本视图，可在用户键入时自动显示候选文本(以下简称ACTV)。候选文本列表显示在下拉菜单中，用户可以从中选择要替换编辑框内容的项目。

由以下的继承树，可以知道ACTV是继承自EditText的，它拥有EditText的所有功能。EditText我们已经再熟悉不过了。ACTV除了继承自EditText，它还是实现了Filter.FilterListener接口。FilterListener接口是用于监听ACTV内容改变时匹配对应的候选词列表。接下来就介绍一下它独特的功能属性。

![继承树](./art/autocompletetextview_extends_tree.png)

### 二、AutoCompleteTextView的基本使用
AutoCompleteTextView常用属性

|属性|描述|
|:-|:-|
|android:completionHint|设置出现在下拉菜单底部的提示信息|
|android:completionThreshold|设置触发补全提示信息的字符个数。最小值为1，设置的数值小于1时则置为1。|
|android:dropDownHorizontalOffset|设置下拉菜单于文本框之间的水平偏移量|
|android:dropDownHeight|设置下拉菜单的高度|
|android:dropDownWidth|设置下拉菜单的宽度|
|android:dropDownVerticalOffset|设置下拉菜单于文本框之间的垂直偏移量|