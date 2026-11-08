package test;

import java.io.Serializable;
import java.util.Date;

public class Position implements Serializable {
    private String posName;
    private Date startDate;
    private Date endDate;
    private String posDescription;
    private String posMemo;
    
    public Position(String posName, Date startDate, Date endDate,  String posDescription, String posMemo) {
        this.posName = posName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.posDescription = posDescription;
        this.posMemo = posMemo;
    }
    
    public String getPosName() {
        return posName;
    }
    
    public void setPosName(String posName) {
        this.posName = posName;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String getPosDescription() {
        return posDescription;
    }
    
    public void setPosDescription(String posDescription) {
        this.posDescription = posDescription;
    }
    
    public String getPosMemo() {
        return posMemo;
    }
    
    public void setPosMemo(String posMemo) {
        this.posMemo = posMemo;
    }
}