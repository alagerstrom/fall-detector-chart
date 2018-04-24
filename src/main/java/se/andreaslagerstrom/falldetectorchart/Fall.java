package se.andreaslagerstrom.falldetectorchart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Fall {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class MyDate{
        private String iso;

        public String getIso() {
            return iso;
        }

        public void setIso(String iso) {
            this.iso = iso;
        }
    }


    private List<Double> data;
    private MyDate date;

    private String impactStart;
    private String impactEnd;
    private String impactDuration;
    private String averageAcceleration;

    private String impactPeakValue;
    private String impactPeakDuration;

    private String longestValleyValue;
    private String longestValleyDuration;

    private String numberOfPeaksPriorToImpact;
    private String numberOfValleysPriorToImpact;


    private String operatingSystem;
    private String device;
    private ClassificationType classificationType;

    public enum ClassificationType {
        ALARM(0),
        FALL(1),
        JUMP(2),
        RUN(3),
        WALK(4),
        OTHER(5);

        private int numValue;

        ClassificationType(int numValue) {
            this.numValue = numValue;
        }

        public int getNumValue() {
            return numValue;
        }
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public MyDate getDate() {
        return date;
    }

    public void setDate(MyDate date) {
        this.date = date;
    }

    public String getImpactStart() {
        return impactStart;
    }

    public void setImpactStart(String impactStart) {
        this.impactStart = impactStart;
    }

    public String getImpactEnd() {
        return impactEnd;
    }

    public void setImpactEnd(String impactEnd) {
        this.impactEnd = impactEnd;
    }

    public String getImpactDuration() {
        return impactDuration;
    }

    public void setImpactDuration(String impactDuration) {
        this.impactDuration = impactDuration;
    }

    public String getAverageAcceleration() {
        return averageAcceleration;
    }

    public void setAverageAcceleration(String averageAcceleration) {
        this.averageAcceleration = averageAcceleration;
    }

    public String getImpactPeakValue() {
        return impactPeakValue;
    }

    public void setImpactPeakValue(String impactPeakValue) {
        this.impactPeakValue = impactPeakValue;
    }

    public String getImpactPeakDuration() {
        return impactPeakDuration;
    }

    public void setImpactPeakDuration(String impactPeakDuration) {
        this.impactPeakDuration = impactPeakDuration;
    }

    public String getLongestValleyValue() {
        return longestValleyValue;
    }

    public void setLongestValleyValue(String longestValleyValue) {
        this.longestValleyValue = longestValleyValue;
    }

    public String getLongestValleyDuration() {
        return longestValleyDuration;
    }

    public void setLongestValleyDuration(String longestValleyDuration) {
        this.longestValleyDuration = longestValleyDuration;
    }

    public String getNumberOfPeaksPriorToImpact() {
        return numberOfPeaksPriorToImpact;
    }

    public void setNumberOfPeaksPriorToImpact(String numberOfPeaksPriorToImpact) {
        this.numberOfPeaksPriorToImpact = numberOfPeaksPriorToImpact;
    }

    public String getNumberOfValleysPriorToImpact() {
        return numberOfValleysPriorToImpact;
    }

    public void setNumberOfValleysPriorToImpact(String numberOfValleysPriorToImpact) {
        this.numberOfValleysPriorToImpact = numberOfValleysPriorToImpact;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public ClassificationType getClassificationType() {
        return classificationType;
    }

    public void setClassificationType(ClassificationType classificationType) {
        this.classificationType = classificationType;
    }
}
