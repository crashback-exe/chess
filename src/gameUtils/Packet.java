package gameUtils;

import java.awt.*;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class Packet implements Serializable {
    // Starting position
    public final Point from;

    // Arrival position
    public final Point to;

    // Is game over?
    public final boolean continuePlaying;

    public final SpecialMove specialMove;

    public Packet(Point from, Point to) {
        this(from, to, SpecialMove.NONE, true);
    }

    public Packet(Point from, Point to, SpecialMove specialMove) {
        this(from, to, specialMove, true);
    }

    public Packet(Point from, Point to, boolean continuePlaying) {
        this(from, to, SpecialMove.NONE, continuePlaying);
    }

    /**
     * @param from Starting position of the piece
     * @param to Arrival position of the piece
     * @param endGame Is game over?
     */
    public Packet(Point from, Point to, SpecialMove specialMove, boolean continuePlaying) {
        this.from = from;
        this.to = to;

        this.specialMove = specialMove;

        this.endGame = endGame;
    }

    /**
     * Deserialize a base-64 encoded string to a Packet object
     * @param input String to be decoded and deserialized
     * @return The serialized Packet object
     */
    public static Packet fromString(String input) throws IOException, ClassNotFoundException {
        // Decode the base-64 string
        byte[] byteData = Base64.getDecoder().decode(input);

        // Deserialize the object
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteData));

        // Cast the deserialized object to Packet
        Packet object = (Packet) inputStream.readObject();

        inputStream.close();

        return object;
    }

    /**
     * Serialize the Packet to a base-64 encoded String
     * @return The serialized, encoded string
     */
    public String serializeToString() throws IOException {
        // Serialize the object to string
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(output);
        objectOutput.writeObject(this);
        objectOutput.close();

        // Encode the string to base-64 and return
        return Base64.getEncoder().encodeToString(output.toByteArray());
    }
}
