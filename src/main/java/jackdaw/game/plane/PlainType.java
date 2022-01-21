package jackdaw.game.plane;

public enum PlainType {
    WOODS((byte) 10),
    PLAINS((byte) 20),
    MOUNTAINS((byte) 30),
    FIELDS((byte) 40),
    PITS((byte) 50),
    RIVERS((byte) 52),
    EMPTY((byte) 53),
    WATER((byte) -1);
    private byte range;

    PlainType(byte range) {
        this.range = range;
    }

    public static PlainType get(byte b) {
        for (PlainType plain : PlainType.values()) {
            byte lowByte = (byte) (plain.ordinal() == 0 ? 0 : PlainType.values()[plain.ordinal() - 1].range + 1);
            byte highByte = plain.range;
            if (highByte > 0) {
                if (b >= lowByte && b <= highByte) {
                    return plain;
                }
            } else
                return plain.range == b ? plain : null;
        }

        return null;
    }
}
