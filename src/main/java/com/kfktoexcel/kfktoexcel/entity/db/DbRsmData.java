package com.kfktoexcel.kfktoexcel.entity.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hd
 * @since 2022-05-13
 */
@Getter
@Setter
  @TableName("db_rsm_data")
@ApiModel(value = "DbRsmData对象", description = "")
public class DbRsmData implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    @TableField("time_stamp")
    private String timeStamp;

    @TableField("send_time")
    private String sendTime;

    @TableField("ptc_type")
    private String ptcType;

    private String ptcId;

    @TableField("sec_mark")
    private String secMark;

    @TableField("cross_id")
    private String crossId;

    @TableField("global_track_id")
    private String globalTrackId;

    private String source;

    private String speed;

    private String heading;

    @TableField("vehicle_color")
    private String vehicleColor;

    @TableField("vehicle_model")
    private String vehicleModel;

    @TableField("vehicle_brand")
    private String vehicleBrand;

    @TableField("vehicle_style")
    private String vehicleStyle;

    @TableField("plate_no")
    private String plateNo;


}
