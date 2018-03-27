package com.ai.zhihao.hw5;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zhihaoai on 3/24/18.
 */

public class OfficialsAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private List<Official> officialsList;
    private MainActivity ma;

    public OfficialsAdapter(List<Official> officialsList, MainActivity ma) {
        this.officialsList = officialsList;
        this.ma = ma;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list_row, parent, false);

        itemView.setOnClickListener(ma);
        itemView.setOnLongClickListener(ma);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        Official official = officialsList.get(position);
        holder.office.setText(official.getOffice());
        holder.name.setText(String.format("%s (%s)", official.getName(), official.getParty()));
    }

    @Override
    public int getItemCount() {
        return officialsList.size();
    }
}
