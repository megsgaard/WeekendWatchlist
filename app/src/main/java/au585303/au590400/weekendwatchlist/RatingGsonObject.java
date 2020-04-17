
package au585303.au590400.weekendwatchlist; ;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingGsonObject {

    @SerializedName("Source")
    @Expose
    private String source;
    @SerializedName("Value")
    @Expose
    private String value;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
