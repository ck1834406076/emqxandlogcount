/**
  * Copyright 2022 bejson.com 
  */
package com.kfktoexcel.kfktoexcel.entity.rsm;

import lombok.Data;

/**
 * Auto-generated: 2022-05-13 10:23:52
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Participant_list {

    private String ptc_type;
    private int ptcId;
    private int sec_mark;
    private String cross_id;
    private int global_track_id;
    private String source;
    private Pos pos;
    private Pos_confidence pos_confidence;
    private Nez nez;
    private int speed;
    private int heading;
    private String vehicle_color;
    private String vehicle_model;
    private String vehicle_brand;
    private String vehicle_style;
    private Size size;
    private String plate_no;
    private Vehicle_class vehicle_class;


}