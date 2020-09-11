package com.zyz.astaralgorithm.utils.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wangxiangle on 2018/8/3 18:52
 * E-Mail Address： wang_x_le@163.com
 */

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private String TAG  = GridItemDecoration.this.getClass().getName();
    public static final int GRID_OFFSETS_HORIZONTAL = GridLayoutManager.HORIZONTAL;
    public static final int GRID_OFFSETS_VERTICAL = GridLayoutManager.VERTICAL;

    final Builder builder;
    Paint mVerPaint, mHorPaint;

    @IntDef({
            GRID_OFFSETS_HORIZONTAL,
            GRID_OFFSETS_VERTICAL
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }

    @Orientation
    private int mOrientation;

    public GridItemDecoration(Builder builder) {
        this.builder = builder;
        mVerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mVerPaint.setStyle(Paint.Style.FILL);
        mVerPaint.setColor(builder.color);
        mHorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHorPaint.setStyle(Paint.Style.FILL);
        mHorPaint.setColor(builder.color);
        setOrientation(builder.mOrientation);
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (builder.isHor) {
            drawHorizontal(c, parent);
        }
        drawVertical(c, parent);
    }

    protected void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
//        int spanCount = getSpanCount(parent);

        for (int i = 0; i < childCount; i++) {
            if (i == 0 && builder.isHasHead) {
                continue;
            }
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            int left = child.getRight() + params.rightMargin;
            int right = left + builder.size;

            c.drawRect(left, top, right, bottom, mVerPaint);

//            boolean isFirstColumn = isFirstColumn(i, spanCount, childCount);
//            TLog.e(TAG," drawVertical isFirstColumn=" + isFirstColumn + " i=" + i + " spanCount=" + spanCount + " childCount=" + spanCount);
//            if (isFirstColumn) {
//                left = 0;
//                right = left + builder.size;
//                TLog.e(TAG,"drawVertical left=" + left + " top=" + " right=" + right + " bottom=" + bottom);
//                c.drawRect(left, top, right, bottom, mHorPaint);
//            }
        }
    }

    protected void drawHorizontal(Canvas c, RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {

            boolean isFirstColumn = isFirstColumn(i, spanCount, childCount);
//            boolean isFirstRow = isFirstRow(i, spanCount, childCount);

            if (i == 0 && builder.isHasHead) {
                continue;
            }
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + builder.size;
            int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + builder.size;

            if (isFirstColumn) {
                left = 0;
            }

            c.drawRect(left, top, right, bottom, mHorPaint);

//            if (isFirstRow) {
//                top = 0;
//                bottom = top + builder.size;
//                c.drawRect(left, top, right, bottom, mHorPaint);
//            }
        }
    }

    protected boolean isFirstRow(int position, int spanCount, int childCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            return position < spanCount;
        } else {
            return position % spanCount == 0;
        }
    }

    protected boolean isFirstColumn(int position, int spanCount, int childCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            return position % spanCount == 0;
        } else {
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position < childCount - lastColumnCount;
        }
    }

    protected int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else {
            throw new UnsupportedOperationException("the GridDividerItemDecoration can only be used in " +
                    "the RecyclerView which use a GridLayoutManager or StaggeredGridLayoutManager");
        }
    }

    protected Item getSpan(RecyclerView parent, View view) {
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        Item item = new Item(1, 1, 0);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager.SpanSizeLookup spanSizeLookup = ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            int spanSize = spanSizeLookup.getSpanSize(position);
            int spanIndex = ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanIndex(position, spanCount);
            item.spanSize = spanSize;
            item.spanCount = spanCount;
            item.spanIndex = spanIndex;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            if (view != null) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                item.spanIndex = layoutParams.getSpanIndex();
            } else {
                item.spanIndex = 0;
            }
            item.spanCount = spanCount;
            item.spanSize = 1;
        }
        return item;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (itemPosition == 0 && builder.isHasHead) {
            return;
        }
        Item span = getSpan(parent, view);
        int spanCount = span.spanCount;
        int spanSize = span.spanSize;
        int spanIndex = span.spanIndex;

//        int left = spanIndex * builder.size / (spanCount);
//        int right = builder.size - (spanIndex + spanSize) * builder.size / (spanCount);
        int left = builder.size - spanIndex * builder.size / spanCount;
        int right = (spanIndex + spanSize) * builder.size / spanCount;
        int bottom = 0;
        int top = 0;
        if (builder.isHor) {
            bottom = builder.size;
            if (!builder.isHasHead) {
                //没有头部 画横线头部
                if (itemPosition < spanCount && builder.isDrawFirstTopLine) {
                    top = builder.size;
                }
            }
        }
        outRect.set(left, top, right, bottom);
    }


    public static class Builder {
        private Context c;
        int color;
        int size;
        boolean isHor;
        boolean isHasHead;
        boolean isDrawFirstTopLine = false;
        boolean mIsDrawOverFirstRowDeco;        //是否画第一行顶部的间隔线      //可能需要设置 mOrientation
        boolean mIsDrawOverFirstColumnDeco;     //是否画第一列左边的间隔线
        @Orientation
        private int mOrientation = GRID_OFFSETS_VERTICAL;

        public Builder(Context c) {
            this.c = c;
            isHor = true;
            isHasHead = false;
            color = Color.WHITE;
            size = 20;
            isDrawFirstTopLine = false;
        }

        public void setOrientation(int orientation) {
            this.mOrientation = orientation;
        }

        public Builder setDrawOverFirstRowDeco(boolean isDraw) {
            this.mIsDrawOverFirstRowDeco = isDraw;
            return this;
        }

        public Builder setDrawOverFirstColumnDeco(boolean isDraw) {
            this.mIsDrawOverFirstColumnDeco = isDraw;
            return this;
        }

        /**
         * 设置divider的颜色
         *
         * @param color
         * @return
         */
        public Builder color(@ColorInt int color) {
            this.color = color;
            return this;
        }

        /**
         * 是否画横着的线
         *
         * @param isPaint
         * @return
         */
        public Builder setHor(boolean isPaint) {
            this.isHor = isPaint;
            return this;
        }


        /**
         * 设置是否有头部
         *
         * @param isHasHead
         * @return
         */
        public Builder setHead(boolean isHasHead) {
            this.isHasHead = isHasHead;
            return this;
        }

        /**
         * 设置divider的宽度
         *
         * @param size
         * @return
         */
        public Builder size(int size) {
            this.size = size;
            return this;
        }


        /**
         * @param drawFirstTopLine true 画第一行顶部横线
         * @return
         */
        public Builder setDrawFirstTopLine(boolean drawFirstTopLine) {
            isDrawFirstTopLine = drawFirstTopLine;
            return this;
        }

        public GridItemDecoration build() {
            return new GridItemDecoration(this);
        }

    }

    class Item {
        int spanCount;
        int spanSize;
        int spanIndex;

        public Item(int spanCount, int spanSize, int spanIndex) {
            this.spanCount = spanCount;
            this.spanSize = spanSize;
            this.spanIndex = spanIndex;
        }
    }

}
