package tutorial.functions.serde;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.pulsar.functions.api.SerDe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TweetSerDe implements SerDe<Tweet> {
    private final Kryo kryo;

    public TweetSerDe() {
        kryo = new Kryo();
        kryo.register(Tweet.class, 1);
    }

    @Override
    public Tweet deserialize(byte[] bytes) {
        Tweet tweet;
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            Input input = new Input(in);
            tweet = kryo.readObject(input, Tweet.class);
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tweet;
    }

    @Override
    public byte[] serialize(Tweet tweet) {
        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Output output = new Output(out);
            kryo.writeObject(output, tweet);
            bytes = output.toBytes();
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
