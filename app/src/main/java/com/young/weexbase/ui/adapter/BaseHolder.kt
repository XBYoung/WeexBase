package com.young.weexbase.ui.adapter

import android.view.View

/**
Young.Lew
2019/1/16 8:50
iot.chinamobile.rearview.ui.adapters
 */
abstract class  BaseHolder <T> (val item: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(item)  {
    abstract fun bind(bean:T,position:Int)
}