package com.young.weexbase.weex.component

import android.content.Context
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import org.apache.weex.WXSDKInstance
import org.apache.weex.ui.action.BasicComponentData
import org.apache.weex.ui.component.WXComponent
import org.apache.weex.ui.component.WXComponentProp
import org.apache.weex.ui.component.WXVContainer

class CustomView : WXComponent<RelativeLayout> {
    private var content = ""
    private var contentView: TextView? = null

    constructor(instance: WXSDKInstance?, parent: WXVContainer<*>?, instanceId: String?, isLazy: Boolean, basicComponentData: BasicComponentData<*>?)
            : super(instance, parent, instanceId, isLazy, basicComponentData)

    constructor(instance: WXSDKInstance?, parent: WXVContainer<*>?, isLazy: Boolean, basicComponentData: BasicComponentData<*>?)
            : super(instance, parent, isLazy, basicComponentData)

    constructor(instance: WXSDKInstance?, parent: WXVContainer<*>?, basicComponentData: BasicComponentData<*>?) : super(instance, parent, basicComponentData)
    constructor(instance: WXSDKInstance?, parent: WXVContainer<*>?, type: Int, basicComponentData: BasicComponentData<*>?) : super(instance, parent, type, basicComponentData)

    @WXComponentProp(name = "text")
    fun upText(content: String?) {
        this.content = content ?: ""
        contentView?.let {
            it.text = content
        }

    }

    override fun initComponentHostView(context: Context): RelativeLayout {
        val root = RelativeLayout(context)
        if (null == contentView) {
            contentView = TextView(context)
            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            contentView?.apply {
                this.layoutParams = layoutParams
                gravity = Gravity.CENTER
            }

        }
        contentView?.let {
            it.text = content
            root.addView(it)
        }
        return root
    }

}