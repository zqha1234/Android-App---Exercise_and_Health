package com.example.qiuhao_zheng_myruns5.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
// health data table in the database
@Entity(tableName = "healthData_table")
// table layout
data class HealthData (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "inputType_column")
    var inputType: String = "",

    @ColumnInfo(name = "type_column")
    var type: String = "",

    @ColumnInfo(name = "date_column")
    var date: String = "",

    @ColumnInfo(name = "duration_column")
    var duration: String = "",

    @ColumnInfo(name = "distance_column")
    var distance: String = "",

    @ColumnInfo(name = "calories_column")
    var calories: String = "",

    @ColumnInfo(name = "heartRate_column")
    var heartRate: String = "",

    @ColumnInfo(name = "comment_column")
    var comment: String = "",

    @ColumnInfo(name = "startPTLat_column")
    var startPTLat: String = "",

    @ColumnInfo(name = "startPTLng_column")
    var startPTLng: String = "",

    @ColumnInfo(name = "endPTLat_column")
    var endPTLat: String = "",

    @ColumnInfo(name = "endPTLng_column")
    var endPTLng: String = "",

    @ColumnInfo(name = "climb_column")
    var climb: String = "",

    @ColumnInfo(name = "avgSpeed_column")
    var avgSpeedDB: String = "",

    @ColumnInfo(name = "path_column")
    var path: String = "",
)