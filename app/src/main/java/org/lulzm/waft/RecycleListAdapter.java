package org.lulzm.waft;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecycleListAdapter extends RecyclerView.Adapter<RecycleListAdapter.ViewHolder> {

    /*리스트 아이템*/
    private final List<CardItem> mDataList;

    /*아이템 클릭이벤트*/
    public interface RecyclerViewClickListener{
        void onItemClicked(int position);
    }


    private RecyclerViewClickListener mListener;
    public void setOnClickListener(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public RecycleListAdapter(List<CardItem> dataList) {
        this.mDataList = dataList;
    }

    /*카드뷰 아이템 연결*/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_card,viewGroup,false);
        return new ViewHolder(view);
    }

    /*뷰에 아이템 삽입*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CardItem item = mDataList.get(i);
        viewHolder.tv_Qr.setText(item.getTv_QR());

        if (mListener != null) {
            final int pos = i;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos);
                }
            });

        }
    }

    /*아이템 개수 받아오기*/
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /*아이콘이름 선언*/
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_Qr;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Qr=itemView.findViewById(R.id.tv_qr);

        }
    }
}
