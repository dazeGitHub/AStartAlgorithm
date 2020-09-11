package com.zyz.astaralgorithm.ui.astar.adapter

import android.graphics.Color
import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zyz.astaralgorithm.R
import com.zyz.astaralgorithm.bean.NodeBean
import com.zyz.astaralgorithm.bean.ReachState

/**
 * <pre>
 * author : ZYZ
 * e-mail : zyz163mail@163.com
 * time   : 2020/09/11
 * desc   :
 * version: 1.0
</pre> *
 */
class AStarAdapter(data: MutableList<NodeBean>?) :
    BaseQuickAdapter<NodeBean, BaseViewHolder>(R.layout.item_path_node, data) {

    fun refreshPath(list: MutableList<NodeBean>) {
        setNewInstance(list)
        notifyDataSetChanged()
    }

    override fun convert(helper: BaseViewHolder, item: NodeBean) {

        helper.setVisible(R.id.tv_f, item.isFValidate)
            .setVisible(R.id.tv_g, item.isFValidate)
            .setVisible(R.id.tv_h, item.isFValidate)

        if (item.isFValidate) {
            helper.setText(R.id.tv_f, item.f.toString() + "")
                .setText(R.id.tv_g, item.g.toString() + "")
                .setText(R.id.tv_h, item.h.toString() + "")
        }

        when (item.reachSate) {
            ReachState.NOT_ALLOW_GO -> {
                helper.setBackgroundColor(R.id.iv_bg, Color.parseColor("#F92671"))
            }
            ReachState.NOT_FIND -> {
                helper.setBackgroundColor(R.id.iv_bg, Color.parseColor("#65D9EF"))
            }
            ReachState.FIND_BUT_NOT_GO -> {
                helper.setBackgroundColor(R.id.iv_bg, Color.parseColor("#2DE2A6"))
                helper.setText(R.id.tv_f, item.f.toString() + "")
                helper.setText(R.id.tv_g, item.g.toString() + "")
                helper.setText(R.id.tv_h, item.h.toString() + "")
            }
            ReachState.FIND_AND_GO -> {
                helper.setBackgroundColor(R.id.iv_bg, Color.parseColor("#AE81FF"))
            }
            ReachState.DESTINATION -> {
                helper.setBackgroundColor(R.id.iv_bg, Color.parseColor("#A0DA2D"))
            }
            else -> {
                Log.d("AStarAdapter", "convert bad else !!!")
            }
        }

        helper.setVisible(R.id.iv_path, item.isPath && (!item.isEnd) && !item.isStart)
        helper.setVisible(R.id.iv_end, item.isEnd())
        helper.setVisible(R.id.iv_start, item.isStart())
    }
}
