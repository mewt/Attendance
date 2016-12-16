package com.eusecom.attendance.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eusecom.attendance.R;

public class AbsTypesViewHolder extends RecyclerView.ViewHolder  {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;

    public AbsTypesViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);



    }

    public void bindToAbsence(com.eusecom.attendance.models.Absence absence, View.OnClickListener starClickListener) {
        titleView.setText(absence.idm);
        authorView.setText(absence.iname);
        numStarsView.setText("0");
        bodyView.setText("0");

        starView.setOnClickListener(starClickListener);


    }




}