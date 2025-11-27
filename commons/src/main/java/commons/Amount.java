package commons;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InformalAmount.class, name = "informalAmount"),
        @JsonSubTypes.Type(value = FormalAmount.class, name = "formalAmount")
})
public abstract class Amount {   
}
