package org.yarpc.common.codec;

/**
 * Created by jingtian.zjt on 2014/12/5.
 */
public class Codecs {
    public static final byte JAVA_CODEC = 1;
    public static final byte JSON_CODEC = 2;

    private static Encoder[] encoders = new Encoder[8];
    private static Decoder[] decoders = new Decoder[8];

    static {
        addEncoder(JAVA_CODEC, new JavaEncoder());
        addDecoder(JAVA_CODEC, new JavaDecoder());

        addEncoder(JSON_CODEC, new FastJsonEncoder());
        addDecoder(JSON_CODEC, new FastJsonDecoder());
    }

    public static void addEncoder(int encoderKey, Encoder encoder) {
        if (encoderKey > encoders.length) {
            Encoder[] newEncoders = new Encoder[encoderKey + 1];
            System.arraycopy(encoders, 0, newEncoders, 0, encoders.length);
            encoders = newEncoders;
        }
        encoders[encoderKey] = encoder;
    }

    public static void addDecoder(int decoderKey, Decoder decoder) {
        if (decoderKey > decoders.length) {
            Decoder[] newDecoders = new Decoder[decoderKey + 1];
            System.arraycopy(decoders, 0, newDecoders, 0, decoders.length);
            decoders = newDecoders;
        }
        decoders[decoderKey] = decoder;
    }

    public static Encoder getEncoder(int encoderKey) {
        return encoders[encoderKey];
    }

    public static Decoder getDecoder(int decoderKey) {
        return decoders[decoderKey];
    }


}


