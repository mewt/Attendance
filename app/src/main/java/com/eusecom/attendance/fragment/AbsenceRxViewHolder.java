package com.eusecom.attendance.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.eusecom.attendance.R;
import com.eusecom.attendance.models.Attendance;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The view holder for an item
 */
public class AbsenceRxViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

  public TextView absence_name;
  public ImageView absence_photo;
  public ImageView starView;
  public TextView numStarsView;
  public TextView datefrom;
  public TextView dateto;
  public TextView hodxb;
  public TextView datm;
  private ClickListener clickListener;
  Context mContext;


  public AbsenceRxViewHolder(View itemView) {
    super(itemView);

    absence_name = (TextView) itemView.findViewById(R.id.absence_name);
    absence_photo = (ImageView) itemView.findViewById(R.id.absence_photo);
    starView = (ImageView) itemView.findViewById(R.id.star);
    numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
    datefrom = (TextView) itemView.findViewById(R.id.datefrom);
    dateto = (TextView) itemView.findViewById(R.id.dateto);
    hodxb = (TextView) itemView.findViewById(R.id.hodxb);
    datm = (TextView) itemView.findViewById(R.id.datm);
    mContext = itemView.getContext();

    itemView.setOnClickListener(this);
    itemView.setOnLongClickListener(this);
  }

  public void bindModel(Attendance attendance) {
    if (attendance == null) {
      throw new IllegalArgumentException("Entity cannot be null");
    }
    absence_name.setText(attendance.dmxa + " " + attendance.dmna);
    if( attendance.dmxa.equals("506")) {
      Picasso.with(mContext).load(R.drawable.abs506).resize(120, 120).into(absence_photo);
    }
    if( attendance.dmxa.equals("510")) {
      Picasso.with(mContext).load(R.drawable.abs510).resize(120, 120).into(absence_photo);
    }
    if( attendance.dmxa.equals("518")) {
      Picasso.with(mContext).load(R.drawable.abs518).resize(120, 120).into(absence_photo);
    }
    if( attendance.dmxa.equals("520")) {
      Picasso.with(mContext).load(R.drawable.abs520).resize(120, 120).into(absence_photo);
    }
    if( attendance.dmxa.equals("801")) {
      Picasso.with(mContext).load(R.drawable.abs801).resize(120, 120).into(absence_photo);
    }
    numStarsView.setText(attendance.aprv);
    Picasso.with(mContext).load(R.drawable.ic_help_black_24dp).resize(120, 120).into(starView);
    if( attendance.aprv.equals("1")) {
      Picasso.with(mContext).load(R.drawable.ic_check_circle_black_24dp).resize(120, 120).into(starView);
    }
    if( attendance.aprv.equals("2")) {
      Picasso.with(mContext).load(R.drawable.ic_remove_circle_black_24dp).resize(120, 120).into(starView);
    }


    //convert unix epoch timestamp (seconds) to milliseconds
    long timestampod = Long.parseLong(attendance.daod) * 1000L;
    datefrom.setText(getDate(timestampod ));

    long timestampdo = Long.parseLong(attendance.dado) * 1000L;
    dateto.setText(getDate(timestampdo ));

    long timestamp = Long.parseLong(attendance.daod) * 1000L;
    hodxb.setText(attendance.hodxb);

    //long timestampm = Long.parseLong(attendance.datm) * 1000L;
    long timestampm = attendance.getDatsLong();
    datm.setText(attendance.usname + " " + getDateTime(timestampm ));
  }

  /* Interface for handling clicks - both normal and long ones. */
  public interface ClickListener {

    /**
     * Called when the view is clicked.
     *
     * @param v view that is clicked
     * @param position of the clicked item
     * @param isLongClick true if long click, false otherwise
     */
    public void onClick(View v, int position, boolean isLongClick);

  }

  /* Setter for listener. */
  public void setClickListener(ClickListener clickListener) {
    this.clickListener = clickListener;
  }

  @Override
  public void onClick(View v) {

    // If not long clicked, pass last variable as false.
    clickListener.onClick(v, getPosition(), false);
  }

  @Override
  public boolean onLongClick(View v) {

    // If long clicked, passed last variable as true.
    clickListener.onClick(v, getPosition(), true);
    return true;
  }


  private String getDate(long timeStamp){

    try{
      DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      Date netDate = (new Date(timeStamp));
      return sdf.format(netDate);
    }
    catch(Exception ex){
      return "xx";
    }
  }

  private String getDateTime(long timeStamp){

    try{
      DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
      Date netDate = (new Date(timeStamp));
      return sdf.format(netDate);
    }
    catch(Exception ex){
      return "xx";
    }
  }

}
