package ru.sellerbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.sellerbot.model.enums.Action;
import ru.sellerbot.model.enums.Stage;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GptDealResponse {
    private String intent;
    private Stage stage;
    private Extracted extracted;
    @JsonProperty("missing_fields")
    private List<String> missingFields;
    private Action action;
    @JsonProperty("message_to_user")
    private String messageToUser;
    @JsonProperty("lookup_request")
    private LookupRequest lookupRequest;
    private Double confidence;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
