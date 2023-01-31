package com.kfktoexcel.kfktoexcel.entity;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Measurement(name = "rsmInflux")
@Setter
@Getter
public class RSMInflux {
    @Column(tag = true)
    public String plate_no;

    @Column(tag = true)
    public String ptcid;

    @Column
    public Double lon;

    @Column
    public Double lat;

    @Column(timestamp = true)
    public Instant time;
}
