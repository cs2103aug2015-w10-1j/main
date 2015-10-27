package procrastinate.task;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.fortuna.ical4j.model.TimeZone;

public class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

public DateAdapter() {
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
}

@Override
public JsonElement serialize(Date date, Type typeOfT, JsonSerializationContext context) {
    return new JsonPrimitive(dateFormat.format(date));
}

@Override
public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            return dateFormat.parse(json.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}
