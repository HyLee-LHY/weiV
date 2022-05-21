package cn.flutterfirst.weiv.core.widgets

import android.view.ViewGroup
import cn.flutterfirst.weiv.core.elements.ContainerRenderElement
import cn.flutterfirst.weiv.core.elements.Element
import cn.flutterfirst.weiv.core.keys.Key
import cn.flutterfirst.weiv.core.others.LayoutParam

abstract class ContainerRenderWidget<VIEW_GROUP : ViewGroup, WIDGET : ContainerRenderWidget<VIEW_GROUP, WIDGET>>(
    key: Key? = null,
    layoutParam: LayoutParam<*>? = null,
    childWidgets: ArrayList<Widget<*>>,
    extra: Any? = null
) : LeafRenderWidget<VIEW_GROUP, WIDGET>(key, layoutParam, childWidgets, extra) {

    override fun createElement(): Element {
        return ContainerRenderElement(this)
    }
}