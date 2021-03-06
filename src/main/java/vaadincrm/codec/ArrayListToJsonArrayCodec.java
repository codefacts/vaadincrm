package vaadincrm.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by sohan on 7/27/2015.
 */
@Component
public class ArrayListToJsonArrayCodec implements MessageCodec<ArrayList, JsonArray> {
    private final ObjectMapper mapper;

    @Autowired
    public ArrayListToJsonArrayCodec(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void encodeToWire(Buffer buffer, ArrayList arrayList) {
        byte[] bytes = io.crm.util.ExceptionUtil.sallowCall(() -> mapper.writeValueAsBytes(arrayList));
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
    }

    @Override
    public JsonArray decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        final byte[] bytes = buffer.getBytes(pos, pos + length);
        return new JsonArray(new String(bytes));
    }

    @Override
    public JsonArray transform(ArrayList arrayList) {
        return null;
    }

    @Override
    public String name() {
        return "ArrayListToJsonArray";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
