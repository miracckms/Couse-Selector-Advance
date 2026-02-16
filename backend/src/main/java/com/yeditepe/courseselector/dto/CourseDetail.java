package com.yeditepe.courseselector.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetail {
    private String fullName;
    private String type;
    private String typeShort;
    private String day;
    
    @JsonProperty("startHour")
    @JsonAlias({"startTime"})
    private String startHour;
    
    @JsonProperty("endHour")
    @JsonAlias({"endTime"})
    private String endHour;
    
    @JsonProperty("roomFloor")
    @JsonAlias({"building"})
    private String roomFloor;
    
    @JsonProperty("roomName")
    @JsonAlias({"room"})
    private String roomName;
    
    private String nameShort;
    
    // Convenience getters for aliases
    public String getStartTime() { return startHour; }
    public String getEndTime() { return endHour; }
    public String getBuilding() { return roomFloor; }
    public String getRoom() { return roomName; }
}
