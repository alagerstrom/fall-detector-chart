package se.andreaslagerstrom.falldetectorchart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FallResponse {
    private List<Fall> results;

    public List<Fall> getResults() {
        return results;
    }

    public void setResults(List<Fall> results) {
        this.results = results;
    }
}
