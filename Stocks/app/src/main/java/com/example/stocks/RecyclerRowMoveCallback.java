package com.example.stocks;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerRowMoveCallback extends ItemTouchHelper.Callback {

    private RecyclerViewRowTouchHelperContract touchHelperContract;
    public RecyclerRowMoveCallback(RecyclerViewRowTouchHelperContract touchHelperContract){
        this.touchHelperContract = touchHelperContract;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlag = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        this.touchHelperContract.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if(viewHolder instanceof RecyclerViewAdapter.MyViewModel){
            RecyclerViewAdapter.MyViewModel myViewHolder = (RecyclerViewAdapter.MyViewModel)viewHolder;
            touchHelperContract.onRowClear(myViewHolder);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(viewHolder instanceof RecyclerViewAdapter.MyViewModel){
            RecyclerViewAdapter.MyViewModel myViewHolder = (RecyclerViewAdapter.MyViewModel)viewHolder;
            touchHelperContract.onRowSwiped(myViewHolder);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        Paint p = new Paint();
        p.setARGB(255, 255, 0, 0);

        Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_delete);

        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                (float) itemView.getRight(), (float) itemView.getBottom(), p);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        int intrinsicHeight = icon.getIntrinsicHeight();
        int intrinsicWidth = icon.getIntrinsicWidth();
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int itemTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int itemMargin = (itemHeight - intrinsicHeight) / 2;
        int itemLeft = itemView.getRight() - itemMargin - intrinsicWidth;
        int itemRight = itemView.getRight() - itemMargin;
        int itemBottom = itemTop + intrinsicHeight;

        icon.setBounds(itemLeft, itemTop, itemRight, itemBottom);
        icon.draw(c);

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            if(viewHolder instanceof RecyclerViewAdapter.MyViewModel){
                RecyclerViewAdapter.MyViewModel myViewHolder = (RecyclerViewAdapter.MyViewModel)viewHolder;
                touchHelperContract.onRowSelected(myViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    public interface RecyclerViewRowTouchHelperContract{
        void onRowMoved(int from, int to);
        void onRowSelected(RecyclerViewAdapter.MyViewModel myViewHolder);
        void onRowClear(RecyclerViewAdapter.MyViewModel myViewHolder);
        void onRowSwiped(RecyclerViewAdapter.MyViewModel myViewHolder);
    }


}
