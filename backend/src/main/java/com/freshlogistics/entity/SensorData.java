package com.freshlogistics.entity;

import java.time.LocalDateTime;

/**
 * 传感器数据实体
 * 用于Kafka消息传输和HDFS存储
 */
public class SensorData {
    
    private String sensorId;           // 传感器ID
    private String sensorType;         // 传感器类型（temperature/humidity/gps）
    private Long vehicleId;            // 车辆ID
    private Double dataValue;          // 数据值
    private String dataUnit;           // 数据单位
    private Double longitude;          // 经度
    private Double latitude;           // 纬度
    private LocalDateTime collectionTime; // 采集时间
    
    public SensorData() {
    }
    
    public SensorData(String sensorId, String sensorType, Long vehicleId, Double dataValue) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.vehicleId = vehicleId;
        this.dataValue = dataValue;
        this.collectionTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getSensorId() {
        return sensorId;
    }
    
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
    
    public String getSensorType() {
        return sensorType;
    }
    
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }
    
    public Long getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public Double getDataValue() {
        return dataValue;
    }
    
    public void setDataValue(Double dataValue) {
        this.dataValue = dataValue;
    }
    
    public String getDataUnit() {
        return dataUnit;
    }
    
    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }
    
    public void setCollectionTime(LocalDateTime collectionTime) {
        this.collectionTime = collectionTime;
    }
    
    @Override
    public String toString() {
        return "SensorData{" +
                "sensorId='" + sensorId + '\'' +
                ", sensorType='" + sensorType + '\'' +
                ", vehicleId=" + vehicleId +
                ", dataValue=" + dataValue +
                ", collectionTime=" + collectionTime +
                '}';
    }
}

