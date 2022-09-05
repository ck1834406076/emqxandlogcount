/**
  * Copyright 2022 bejson.com 
  */
package com.kfktoexcel.kfktoexcel.entity.rsm;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2022-05-13 10:23:52
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Content {

    private Rsm_source rsm_source;
    private String id;
    private long time_stamp;
    private long send_time;
    private Ref_pos ref_pos;
    private List<Participant_list> participant_list;

}