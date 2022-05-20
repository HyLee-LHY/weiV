# weiV

[English](https://github.com/hackware1993/weiV/blob/master/README.md)

![wave.webp](https://github.com/hackware1993/weiV/blob/master/wave.webp?raw=true)

weiV（发音同 wave），一个基于 Android View 系统的全新声明式 UI 开发框架。核心源文件只有 30 KB。

```kotlin
if ("weiV" == "View".reversed()) {
    Log.d(
        "weiV",
        "它意味着控制反转，你始终应该直接操作 UI 的描述 Widget 而不是直接操作 View。"
    )
}
```

它具有以下优势：

1. 声明式的 UI 写法让原生开发效率翻倍
2. 达到或超越 View 系统的性能
    1. 我将我的 Flutter ConstraintLayout 移植到 Android，依托它先进的布局算法，在不引入固有特性测量的情况下，让 View 树中的子元素在任何情况都只会被
       layout 一次，使得任意嵌套不会引起性能问题。即便 View 树中的每个层级宽高都是 wrap_content 和 match_parent 混用
    2. xml 将被抛弃
3. 你所有的现有 View 系统的经验都将得到保留
4. 所有的现有 UI 组件都将得以复用
5. 它使用 Kotlin 编写，但友好的支持 Java
6. 目前已经开始初步支持实时生效的动态化。你可以下发 JS，使用 JS 来写页面逻辑，并生成描述 Widget 树的 JSON 传递给原生，原生使用非反射的方式将其转为真正的 Widget
   树并渲染。后面可能会考虑在 JS 中实现声明式 API

**没有人愿意推翻自己过去在 View 系统的经验，Compose 的设计太过糟糕。**

# 进展

目前完成了 DSL 的定义，可以解析成 Widget 树了，DSL 风格如下：

Kotlin 风格：

```kotlin
class WeiVCounterKotlinActivity : WeiVActivity() {
    private var count = 0
    private val maxCount = 10
    private val minCount = 0

    override fun build() = WeiV {
        Flex {
            it.orientation = FlexDirection.VERTICAL

            Button(text = "Add count", enable = count < maxCount, onClick = {
                setState {
                    count++
                }
            })

            Button(text = "Sub count", enable = count > minCount, onClick = {
                setState {
                    count--
                }
            })

            Text(text = "count = $count")
        }
    }
}
```

Java 风格：

```java
public class WeiVCounterJavaActivity extends BaseWeiVJavaActivity {
    private int count = 0;
    private int maxCount = 10;
    private int minCount = 0;

    @Override
    public WeiV build() {
        return WeiV(() -> {
            Flex((it) -> {
                it.wOrientation(FlexDirection.VERTICAL);

                Button().wText("Add count").wEnable(count < maxCount).wOnClick(v -> {
                    setState(() -> {
                        count++;
                    });
                });

                Button().wText("Sub count").wEnable(count > minCount).wOnClick(v -> {
                    setState(() -> {
                        count--;
                    });
                });

                Text().wText("count = " + count);
            });
        });
    }
}
```

![effect.gif](https://github.com/hackware1993/weiV/blob/master/effect.gif?raw=true)

weiV 是可扩展的。它会内置所有常用的 Widget，这些 Widget 都是对系统 View 的包装。但对于第三方库，就需要写扩展，写起来也极其简单，比如给 Button 的扩展如下：

```kotlin
class weiVButton(
    key: Key? = null,
    var text: String = "",
    var textSize: Float = TextConst.defaultTextSize,
    var textColor: Int = TextConst.defaultTextColor,
    var onClick: View.OnClickListener? = null,
    var enable: Boolean = true
) :
    LeafRenderWidget<Button>(key), IWeiVExtension {

    override fun createView(context: Context): Button = Button(context)

    override fun doParameter(view: Button, first: Boolean): Button {
        if (view.text != text) {
            view.text = text
        }
        if (view.currentTextColor != textColor) {
            view.setTextColor(textColor)
        }
        if (view.textSize != textSize) {
            view.textSize = textSize
        }
        view.setOnClickListener(onClick)
        if (view.isEnabled != enable) {
            view.isEnabled = enable
        }
        return view
    }

    @JavaOnly
    fun wKey(key: Key? = null): weiVButton {
        this.key = key
        return this
    }

    @JavaOnly
    fun wText(text: String = ""): weiVButton {
        this.text = text
        return this
    }

    @JavaOnly
    fun wTextSize(textSize: Float = TextConst.defaultTextSize): weiVButton {
        this.textSize = textSize
        return this
    }

    @JavaOnly
    fun wTextColor(textColor: Int = TextConst.defaultTextColor): weiVButton {
        this.textColor = textColor
        return this
    }

    @JavaOnly
    fun wOnClick(onClick: View.OnClickListener?): weiVButton {
        this.onClick = onClick
        return this
    }

    @JavaOnly
    fun wEnable(enable: Boolean = true): weiVButton {
        this.enable = enable
        return this
    }

    override fun toString(): String {
        return "weiVButton(text = $text, enable = $enable)"
    }
}

@KotlinOnly
fun WeiV.Button(
    key: Key? = null,
    text: String = "",
    textSize: Float = TextConst.defaultTextSize,
    textColor: Int = TextConst.defaultTextColor,
    onClick: View.OnClickListener? = null,
    enable: Boolean = true
) {
    addLeafRenderWidget(
        weiVButton(
            key = key,
            text = text,
            textSize = textSize,
            textColor = textColor,
            onClick = onClick,
            enable = enable
        )
    )
}
```

weiV 基于 View 系统，因此它可以嵌入到 View 树的任何地方。你可以在 weiV 中嵌入 Flutter、Compose，也可以在 Compose、Flutter 里嵌入 weiV。推荐在
Compose 顶层嵌入 weiV 以改善 Compose 的性能。😀

预计很快 weiV 就可以真正跑起来了。但还任重而道远。首先需要移植 Flutter ConstraintLayout，其次大概率会重写一个 weiV 版本的 RecyclerView，以支持像
Flutter 那样简单的列表用法，不需要写 Adapter。

订阅我的微信公众号以及时获取 weiV 的最新动态。后续也会分享一些高质量的、独特的、有思想的 Flutter 和 Android 技术文章。
![official_account.webp](https://github.com/hackware1993/weiV/blob/master/official_account.webp?raw=true)

# 支持我

如果它对你帮助很大，可以考虑赞助我一杯奶茶，或者给个 star。你的支持是我继续维护的动力。

[Paypal](https://www.paypal.com/paypalme/hackware1993)
![support.webp](https://github.com/hackware1993/weiV/blob/master/support.webp?raw=true)

# 联系方式

hackware1993@gmail.com

# 协议

```
MIT License

Copyright (c) 2022 hackware1993

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
