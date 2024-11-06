package src.main.java.com.example;
import java.util.EnumMap;
import java.util.List;
import java.util.Arrays;

public class SpaceSpecializationMap {
    public static final EnumMap<SpaceType, List<SpecializationType>> SPACE_SPECIALIZATION_MAP = new EnumMap<>(SpaceType.class);

    static {
        SPACE_SPECIALIZATION_MAP.put(SpaceType.RINK, Arrays.asList(SpecializationType.HOCKEY));
        SPACE_SPECIALIZATION_MAP.put(SpaceType.FIELD, Arrays.asList(SpecializationType.SOCCER, SpecializationType.FOOTBALL));
        SPACE_SPECIALIZATION_MAP.put(SpaceType.GYM, Arrays.asList(SpecializationType.GYM, SpecializationType.BASKETBALL));
        SPACE_SPECIALIZATION_MAP.put(SpaceType.STUDIO, Arrays.asList(SpecializationType.YOGA, SpecializationType.DANCE));
        SPACE_SPECIALIZATION_MAP.put(SpaceType.POOL, Arrays.asList(SpecializationType.SWIM));
        SPACE_SPECIALIZATION_MAP.put(SpaceType.COURT, Arrays.asList(SpecializationType.BASKETBALL));
        SPACE_SPECIALIZATION_MAP.put(SpaceType.TRACK, Arrays.asList(SpecializationType.RUNNING));
    }
}

enum SpaceType {
    RINK("rink"),
    FIELD("field"),
    GYM("gym"),
    STUDIO("studio"),
    POOL("pool"),
    COURT("court"),
    TRACK("track");

    private final String value;

    SpaceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

enum SpecializationType {
    HOCKEY("hockey"),
    SOCCER("soccer"),
    FOOTBALL("football"),
    GYM("gym"),
    YOGA("yoga"),
    DANCE("dance"),
    SWIM("swim"),
    BASKETBALL("basketball"),
    RUNNING("running");

    private final String value;

    SpecializationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
